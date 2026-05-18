package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.common.resources.Res
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
internal class ConstantResourcesDataSource(
    private val dispatchers: CoroutineDispatchers,
) : ConstantResources {
    private val json = Json { coerceInputValues = true }

    override suspend fun getHeroConstants(): Result<List<HeroDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                json.decodeFromString<List<HeroDto>>(Res.readBytes(HEROES_JSON_PATH).decodeToString())
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getHeroConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getItemConstants(): Result<List<ItemDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                json.decodeFromString<List<ItemDto>>(Res.readBytes(ITEMS_JSON_PATH).decodeToString())
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getItemConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getAbilityConstants(): Result<List<AbilityDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                json.decodeFromString<List<AbilityDto>>(Res.readBytes(ABILITIES_JSON_PATH).decodeToString())
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getAbilityConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }

    private companion object {
        private const val HEROES_JSON_PATH = "files/constants/heroes.json"
        private const val ITEMS_JSON_PATH = "files/constants/items.json"
        private const val ABILITIES_JSON_PATH = "files/constants/abilities.json"
    }
}
