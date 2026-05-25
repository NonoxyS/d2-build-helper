package dev.nonoxy.d2buildhelper.core.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.nonoxy.d2buildhelper.common.coroutines.ioDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME = "d2bh_prefs.preferences_pb"

internal fun createDataStore(
    coroutineScope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob()),
    producePath: () -> String,
): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = coroutineScope,
        produceFile = { producePath().toPath() },
    )
