package dev.nonoxy.d2buildhelper.core.resources.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteAbilityConstant
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteConstantsResponse
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteHeroConstant
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteItemConstant
import kotlin.concurrent.Volatile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface ResourcesRepository {
    suspend fun getHeroImages(): Result<Map<Hero, String>>
    suspend fun getItemImages(): Result<Map<Item, String>>
    suspend fun getAbilityImages(): Result<Map<Ability, String>>
}

internal class ResourcesRepositoryImpl(
    private val constantsApiClient: ConstantsApiClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ResourcesRepository {

    private val constantsMutex = Mutex()
    private var cachedConstants: RemoteConstantsResponse? = null

    @Volatile private var heroImagesCache: Map<Hero, String>? = null
    @Volatile private var itemImagesCache: Map<Item, String>? = null
    @Volatile private var abilityImagesCache: Map<Ability, String>? = null

    override suspend fun getHeroImages(): Result<Map<Hero, String>> {
        heroImagesCache?.let { return Result.success(it) }
        return ensureConstants().mapCatching { constants ->
            val images = withContext(coroutineDispatchers.default) {
                constants.heroes.associate { it.toDomain() to it.iconUrl }
            }
            images.also { heroImagesCache = it }
        }
    }

    override suspend fun getItemImages(): Result<Map<Item, String>> {
        itemImagesCache?.let { return Result.success(it) }
        return ensureConstants().mapCatching { constants ->
            val images = withContext(coroutineDispatchers.default) {
                constants.items.associate { it.toDomain() to it.iconUrl }
            }
            images.also { itemImagesCache = it }
        }
    }

    override suspend fun getAbilityImages(): Result<Map<Ability, String>> {
        abilityImagesCache?.let { return Result.success(it) }
        return ensureConstants().mapCatching { constants ->
            val images = withContext(coroutineDispatchers.default) {
                constants.abilities.associate { it.toDomain() to it.iconUrl }
            }
            images.also { abilityImagesCache = it }
        }
    }

    private suspend fun ensureConstants(): Result<RemoteConstantsResponse> = constantsMutex.withLock {
        cachedConstants?.let { return Result.success(it) }
        constantsApiClient.getConstants().onSuccess { cachedConstants = it }
    }
}

private fun RemoteHeroConstant.toDomain(): Hero =
    Hero(id = id.toShort(), shortName = shortName, displayName = displayName)

private fun RemoteItemConstant.toDomain(): Item =
    Item(id = id.toShort(), shortName = shortName, displayName = displayName)

private fun RemoteAbilityConstant.toDomain(): Ability =
    Ability(id = id.toShort(), name = name)
