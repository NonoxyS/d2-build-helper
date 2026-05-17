package dev.nonoxy.d2buildhelper.core.data.local.resources.constants

import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto
import kotlinx.coroutines.flow.Flow

internal interface ConstantResources {
    fun getHeroConstants(): Flow<List<HeroDto>>

    fun getItemConstants(): Flow<List<ItemDto>>

    fun getAbilityConstants(): Flow<List<AbilityDto>>
}
