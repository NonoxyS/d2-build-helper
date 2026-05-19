package dev.nonoxy.d2buildhelper.core.resources.data.api.image

import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto

internal interface ImageResourcesApi {
    /**
     * Returns Map (Hero(id: Short, shortName: String, displayName: String) to URL)
     */
    suspend fun getHeroImageUrls(heroConstants: List<HeroDto>): Result<Map<HeroDto, String>>

    /**
     * Returns Map (Item(id: Short, shortName: String, displayName: String) to URL)
     */
    suspend fun getItemImageUrls(itemConstants: List<ItemDto>): Result<Map<ItemDto, String>>

    /**
     * Returns Map (Ability(id: Short, name: String) to URL)
     */
    suspend fun getAbilityImageUrls(abilityConstants: List<AbilityDto>): Result<Map<AbilityDto, String>>

    /**
     * Returns Map (additionalName to URL)
     */
    suspend fun getAdditionalImageUrls(): Result<Map<String, String>>
}
