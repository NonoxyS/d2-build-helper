package dev.nonoxy.d2buildhelper.core.data.api.resources.image

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import kotlinx.coroutines.flow.Flow

interface ImageResourcesApi {
    /**
     * Returns Map (Hero(id: Short, shortName: String, displayName: String) to URL)
     */
    fun getHeroImageUrls(heroConstants: List<Hero>): Flow<RequestResult<Map<Hero, String>>>

    /**
     * Returns Map (Item(id: Short, shortName: String, displayName: String) to URL)
     */
    fun getItemImageUrls(itemConstants: List<Item>): Flow<RequestResult<Map<Item, String>>>

    /**
     * Returns Map (Ability(id: Short, name: String) to URL)
     */
    fun getAbilityImageUrls(abilityConstants: List<Ability>): Flow<RequestResult<Map<Ability, String>>>

    /**
     * Returns Map (additionalName to URL)
     */
    fun getAdditionalImageUrls(): Flow<RequestResult<Map<String, String>>>
}