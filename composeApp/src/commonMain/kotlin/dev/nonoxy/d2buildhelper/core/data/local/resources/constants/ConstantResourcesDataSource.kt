package dev.nonoxy.d2buildhelper.core.data.local.resources.constants

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import dota_2_build_helper.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class ConstantResourcesDataSource : ConstantResources {
    override suspend fun getHeroConstants(): Flow<RequestResult<List<Hero>>> {
        val request = flow {
            try {
                val heroConstants = Json.decodeFromString<List<Hero>>(
                    Res.readBytes(HEROES_JSON_PATH).decodeToString()
                )
                emit(RequestResult.Success(heroConstants))
            } catch (e: Exception) {
                emit(RequestResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)

        val start = flowOf<RequestResult<List<Hero>>>(RequestResult.InProgress())

        return merge(request, start)
    }

    override suspend fun getItemConstants(): Flow<RequestResult<List<Item>>> {
        val request = flow {
            try {
                val itemConstants = Json.decodeFromString<List<Item>>(
                    Res.readBytes(ITEMS_JSON_PATH).decodeToString()
                )
                emit(RequestResult.Success(itemConstants))
            } catch (e: Exception) {
                emit(RequestResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)

        val start = flowOf<RequestResult<List<Item>>>(RequestResult.InProgress())

        return merge(request, start)
    }

    override suspend fun getAbilityConstants(): Flow<RequestResult<List<Ability>>> {
        val request = flow {
            try {
                val abilityConstants = Json.decodeFromString<List<Ability>>(
                    Res.readBytes(ABILITIES_JSON_PATH).decodeToString()
                )
                emit(RequestResult.Success(abilityConstants))
            } catch (e: Exception) {
                emit(RequestResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)

        val start = flowOf<RequestResult<List<Ability>>>(RequestResult.InProgress())

        return merge(request, start)
    }


    private companion object {
        private const val HEROES_JSON_PATH = "files/constants/heroes.json"
        private const val ITEMS_JSON_PATH = "files/constants/items.json"
        private const val ABILITIES_JSON_PATH = "files/constants/abilities.json"
    }
}