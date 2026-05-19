package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto

internal interface ConstantResources {
    suspend fun getHeroConstants(): Result<List<HeroDto>>

    suspend fun getItemConstants(): Result<List<ItemDto>>

    suspend fun getAbilityConstants(): Result<List<AbilityDto>>
}
