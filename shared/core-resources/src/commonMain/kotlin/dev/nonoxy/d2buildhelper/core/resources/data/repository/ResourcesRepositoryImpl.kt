package dev.nonoxy.d2buildhelper.core.resources.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.HeroTalent
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteAbilityConstant
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteConstantsResponse
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteHeroConstant
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteItemConstant
import dev.nonoxy.d2buildhelper.core.resources.domain.models.CachedDotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ConstantsStorage
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import io.github.aakira.napier.Napier
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

private val REFRESH_THROTTLE = 1.minutes

internal class ResourcesRepositoryImpl(
    private val apiClient: ConstantsApiClient,
    private val storage: ConstantsStorage,
    private val dispatchers: CoroutineDispatchers,
) : ResourcesRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val refreshMutex = Mutex()

    @Volatile private var l1Cache: DotaConstants? = null
    @Volatile private var lastFetchedAt: Instant? = null
    @Volatile private var inFlight: Deferred<Result<DotaConstants>>? = null

    override suspend fun getDotaConstants(): Result<DotaConstants> = coRunCatching(
        tryBlock = {
            val cached = readL1OrL2()
            if (cached != null) {
                scope.launch { refreshDotaConstants() }
                Result.success(cached)
            } else {
                refreshDotaConstants()
            }
        },
        catchBlock = { throwable ->
            Napier.e(throwable = throwable, message = "ResourcesRepositoryImpl.getDotaConstants failed")
            throwable.wrapResultFailure()
        },
    )

    override suspend fun refreshDotaConstants(
        expectedVersion: GameVersion?,
    ): Result<DotaConstants> {
        val deferredOrCached = refreshMutex.withLock {
            inFlight?.takeIf { it.isActive }?.let { return@withLock RefreshDecision.Join(it) }

            val cached = l1Cache
            val versionSatisfied = expectedVersion == null ||
                (cached != null && cached.gameVersion >= expectedVersion)
            val freshEnough = lastFetchedAt?.let { Clock.System.now() - it < REFRESH_THROTTLE } == true

            val shouldFire = when {
                cached == null -> true
                expectedVersion != null && !versionSatisfied -> true
                expectedVersion != null && versionSatisfied -> false
                else -> !freshEnough
            }

            if (!shouldFire) {
                return Result.success(checkNotNull(cached))
            }

            val deferred = scope.async {
                coRunCatching(
                    tryBlock = { Result.success(fetchAndCache()) },
                    catchBlock = { throwable ->
                        Napier.e(throwable = throwable, message = "ResourcesRepositoryImpl.fetchAndCache failed")
                        throwable.wrapResultFailure()
                    },
                )
            }
            inFlight = deferred
            RefreshDecision.Join(deferred)
        }
        return deferredOrCached.deferred.await()
    }

    private suspend fun readL1OrL2(): DotaConstants? {
        l1Cache?.let { return it }
        val l2 = withContext(dispatchers.io) { storage.load() } ?: return null
        l1Cache = l2.data
        lastFetchedAt = l2.fetchedAt
        return l2.data
    }

    private suspend fun fetchAndCache(): DotaConstants {
        val remote = apiClient.getConstants().getOrThrow()
        val constants = withContext(dispatchers.default) { remote.toDotaConstants() }
        val now = Clock.System.now()
        withContext(dispatchers.io) { storage.save(CachedDotaConstants(constants, now)) }
        l1Cache = constants
        lastFetchedAt = now
        return constants
    }

    private sealed interface RefreshDecision {
        data class Join(val deferred: Deferred<Result<DotaConstants>>) : RefreshDecision
    }
}

private fun RemoteConstantsResponse.toDotaConstants(): DotaConstants = DotaConstants(
    gameVersion = GameVersion(gameVersionId),
    heroes = heroes.associate { it.toDomain().let { hero -> hero.id to hero } },
    items = items.associate { it.toDomain().let { item -> item.id to item } },
    abilities = abilities.associate { it.toDomain().let { ab -> ab.id to ab } },
)

private fun RemoteHeroConstant.toDomain(): Hero = Hero(
    id = HeroId(id.toShort()),
    shortName = shortName,
    displayName = displayName,
    iconUrl = ImageUrl(iconUrl),
    talents = talents.map { HeroTalent(AbilityId(it.abilityId.toShort()), it.slot) },
)

private fun RemoteItemConstant.toDomain(): Item = Item(
    id = ItemId(id.toShort()),
    shortName = shortName,
    displayName = displayName,
    iconUrl = ImageUrl(iconUrl),
    quality = quality,
    isRecipe = isRecipe,
    components = components.map { ItemId(it.toShort()) },
)

private fun RemoteAbilityConstant.toDomain(): Ability = Ability(
    id = AbilityId(id.toShort()),
    name = name,
    displayName = displayName,
    iconUrl = ImageUrl(iconUrl),
)
