package dev.nonoxy.d2buildhelper.core.data.local.resources.constants

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import kotlinx.coroutines.flow.Flow

interface ConstantResources {
    fun getHeroConstants(): Flow<List<Hero>>

    fun getItemConstants(): Flow<List<Item>>

    fun getAbilityConstants(): Flow<List<Ability>>
}