package dev.nonoxy.d2buildhelper.core.resources.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.resources.data.IconUrls
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.ConstantResources
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto
import dev.nonoxy.d2buildhelper.core.domain.Ability
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.Item
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface ResourcesRepository {
    suspend fun getHeroImages(): Result<Map<Hero, String>>
    suspend fun getItemImages(): Result<Map<Item, String>>
    suspend fun getAbilityImages(): Result<Map<Ability, String>>
}

internal class ResourcesRepositoryImpl(
    private val constantResources: ConstantResources,
    private val dispatchers: CoroutineDispatchers,
) : ResourcesRepository {

    private val constantsMutex = Mutex()
    private var heroConstants: List<HeroDto>? = null
    private var itemConstants: List<ItemDto>? = null
    private var abilityConstants: List<AbilityDto>? = null

    private var heroImagesCache: Map<Hero, String>? = null
    private var itemImagesCache: Map<Item, String>? = null
    private var abilityImagesCache: Map<Ability, String>? = null

    override suspend fun getHeroImages(): Result<Map<Hero, String>> {
        heroImagesCache?.let { return Result.success(it) }
        return ensureHeroes().mapCatching { heroes ->
            val domain = withContext(dispatchers.default) {
                heroes.associate { dto -> dto.toDomain() to IconUrls.hero(dto.shortName) }
            }
            heroImagesCache = domain
            domain
        }
    }

    override suspend fun getItemImages(): Result<Map<Item, String>> {
        itemImagesCache?.let { return Result.success(it) }
        return ensureItems().mapCatching { items ->
            val domain = withContext(dispatchers.default) {
                items.associate { dto -> dto.toDomain() to IconUrls.item(dto.shortName) }
            }
            itemImagesCache = domain
            domain
        }
    }

    override suspend fun getAbilityImages(): Result<Map<Ability, String>> {
        abilityImagesCache?.let { return Result.success(it) }
        return ensureAbilities().mapCatching { abilities ->
            val domain = withContext(dispatchers.default) {
                abilities.associate { dto -> dto.toDomain() to IconUrls.ability(dto.name) }
            }
            abilityImagesCache = domain
            domain
        }
    }

    private suspend fun ensureHeroes(): Result<List<HeroDto>> = constantsMutex.withLock {
        heroConstants?.let { return Result.success(it) }
        constantResources.getHeroConstants().onSuccess { heroConstants = it }
    }

    private suspend fun ensureItems(): Result<List<ItemDto>> = constantsMutex.withLock {
        itemConstants?.let { return Result.success(it) }
        constantResources.getItemConstants().onSuccess { itemConstants = it }
    }

    private suspend fun ensureAbilities(): Result<List<AbilityDto>> = constantsMutex.withLock {
        abilityConstants?.let { return Result.success(it) }
        constantResources.getAbilityConstants().onSuccess { abilityConstants = it }
    }
}

private fun HeroDto.toDomain(): Hero = Hero(heroId = id, shortName = shortName, displayName = displayName)
private fun ItemDto.toDomain(): Item = Item(id = id, shortName = shortName, displayName = displayName)
private fun AbilityDto.toDomain(): Ability = Ability(id = id, name = name)
