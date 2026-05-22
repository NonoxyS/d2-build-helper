package dev.nonoxy.d2buildhelper.core.resources.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.domain.Ability
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.Item
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteAbilityConstantResponse
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteConstantsResponse
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteHeroConstantResponse
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteItemConstantResponse
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

private fun RemoteHeroConstantResponse.toDomain(): Hero =
    Hero(heroId = id.toShort(), shortName = shortName, displayName = displayName)

private fun RemoteItemConstantResponse.toDomain(): Item =
    Item(id = id.toShort(), shortName = shortName, displayName = displayName)

private fun RemoteAbilityConstantResponse.toDomain(): Ability =
    Ability(id = id.toShort(), name = name)
