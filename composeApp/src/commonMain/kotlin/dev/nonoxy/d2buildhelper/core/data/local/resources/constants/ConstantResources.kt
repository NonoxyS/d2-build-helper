package dev.nonoxy.d2buildhelper.core.data.local.resources.constants

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import kotlinx.coroutines.flow.Flow

interface ConstantResources {
    suspend fun getHeroConstants(): Flow<RequestResult<List<Hero>>>

    suspend fun getItemConstants(): Flow<RequestResult<List<Item>>>

    suspend fun getAbilityConstants(): Flow<RequestResult<List<Ability>>>
}