package dev.nonoxy.d2buildhelper.core.resources.data.repository

import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.resources.domain.models.CachedDotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
internal data class SerializableCachedDotaConstants(
    val gameVersionId: Int,
    val heroes: List<SerializableHero>,
    val items: List<SerializableItem>,
    val abilities: List<SerializableAbility>,
    val fetchedAtEpochMillis: Long,
) {
    fun toDomain(): CachedDotaConstants = CachedDotaConstants(
        data = DotaConstants(
            gameVersion = GameVersion(gameVersionId),
            heroes = heroes.associate { hero ->
                val id = HeroId(hero.id)
                id to Hero(
                    id = id,
                    shortName = hero.shortName,
                    displayName = hero.displayName,
                    iconUrl = ImageUrl(hero.iconUrl),
                )
            },
            items = items.associate { item ->
                val id = ItemId(item.id)
                id to Item(
                    id = id,
                    shortName = item.shortName,
                    displayName = item.displayName,
                    iconUrl = ImageUrl(item.iconUrl),
                    quality = item.quality,
                    isRecipe = item.isRecipe,
                    components = item.components.map { ItemId(it) },
                )
            },
            abilities = abilities.associate { ab ->
                val id = AbilityId(ab.id)
                id to Ability(id = id, name = ab.name, iconUrl = ImageUrl(ab.iconUrl))
            },
        ),
        fetchedAt = Instant.fromEpochMilliseconds(fetchedAtEpochMillis),
    )
}

@Serializable
internal data class SerializableHero(
    val id: Short,
    val shortName: String,
    val displayName: String,
    val iconUrl: String,
)

@Serializable
internal data class SerializableItem(
    val id: Short,
    val shortName: String,
    val displayName: String,
    val iconUrl: String,
    val quality: String? = null,
    val isRecipe: Boolean = false,
    val components: List<Short> = emptyList(),
)

@Serializable
internal data class SerializableAbility(
    val id: Short,
    val name: String,
    val iconUrl: String,
)

internal fun CachedDotaConstants.toSerializable(): SerializableCachedDotaConstants =
    SerializableCachedDotaConstants(
        gameVersionId = data.gameVersion.id,
        heroes = data.heroes.values.map {
            SerializableHero(it.id.raw, it.shortName, it.displayName, it.iconUrl.raw)
        },
        items = data.items.values.map {
            SerializableItem(
                id = it.id.raw,
                shortName = it.shortName,
                displayName = it.displayName,
                iconUrl = it.iconUrl.raw,
                quality = it.quality,
                isRecipe = it.isRecipe,
                components = it.components.map { component -> component.raw },
            )
        },
        abilities = data.abilities.values.map {
            SerializableAbility(it.id.raw, it.name, it.iconUrl.raw)
        },
        fetchedAtEpochMillis = fetchedAt.toEpochMilliseconds(),
    )
