package dev.nonoxy.d2buildhelper.core.data.api.resources.image

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto
import kotlinx.coroutines.flow.Flow

internal interface ImageResourcesApi {
    /**
     * Returns Map (Hero(id: Short, shortName: String, displayName: String) to URL)
     */
    fun getHeroImageUrls(heroConstants: List<HeroDto>): Flow<RequestResult<Map<HeroDto, String>>>

    /**
     * Returns Map (Item(id: Short, shortName: String, displayName: String) to URL)
     */
    fun getItemImageUrls(itemConstants: List<ItemDto>): Flow<RequestResult<Map<ItemDto, String>>>

    /**
     * Returns Map (Ability(id: Short, name: String) to URL)
     */
    fun getAbilityImageUrls(abilityConstants: List<AbilityDto>): Flow<RequestResult<Map<AbilityDto, String>>>

    /**
     * Returns Map (additionalName to URL)
     */
    fun getAdditionalImageUrls(): Flow<RequestResult<Map<String, String>>>
}
