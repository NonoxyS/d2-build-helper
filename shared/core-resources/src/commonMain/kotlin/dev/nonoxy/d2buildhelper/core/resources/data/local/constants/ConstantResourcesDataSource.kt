package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class ConstantResourcesDataSource(
    private val dispatchers: CoroutineDispatchers,
    private val fileContentReader: FileContentReader,
) : ConstantResources {
    private val json = Json { coerceInputValues = true }

    override suspend fun getHeroConstants(): Result<List<HeroDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                json.decodeFromString<List<HeroDto>>(fileContentReader.read(MR.files.constant_heroes_json))
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
                json.decodeFromString<List<ItemDto>>(fileContentReader.read(MR.files.constant_items_json))
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
                json.decodeFromString<List<AbilityDto>>(fileContentReader.read(MR.files.constant_abilities_json))
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getAbilityConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }
}
