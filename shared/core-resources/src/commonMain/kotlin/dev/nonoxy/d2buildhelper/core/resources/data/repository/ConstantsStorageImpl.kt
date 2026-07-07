package dev.nonoxy.d2buildhelper.core.resources.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.core.resources.domain.models.CachedDotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ConstantsStorage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ConstantsStorageImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: CoroutineDispatchers,
    private val json: Json,
) : ConstantsStorage {

    private val key = stringPreferencesKey(CONSTANTS_KEY)

    override suspend fun load(): CachedDotaConstants? = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                val raw = dataStore.data.first()[key]
                    ?: return@coRunCatching Result.success<CachedDotaConstants?>(null)
                Result.success(json.decodeFromString<SerializableCachedDotaConstants>(raw).toDomain())
            },
            catchBlock = { throwable ->
                if (throwable is SerializationException) {
                    Napier.w(throwable = throwable, message = "ConstantsStorage.load: corrupt payload — ignoring")
                    Result.success(null)
                } else {
                    Napier.e(throwable = throwable, message = "ConstantsStorage.load failed")
                    Result.failure(throwable)
                }
            },
        ).getOrThrow()
    }

    override suspend fun save(value: CachedDotaConstants): Unit = withContext(dispatchers.io) {
        val encoded = json.encodeToString(value.toSerializable())
        dataStore.edit { prefs -> prefs[key] = encoded }
    }

    private companion object {
        const val CONSTANTS_KEY = "dota_constants_v1"
    }
}
