package dev.nonoxy.d2buildhelper.core.storage.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.storage.datastore.DATA_STORE_FILE_NAME
import dev.nonoxy.d2buildhelper.core.storage.datastore.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal actual val platformCoreStorageModule = module {

    single<DataStore<Preferences>> {
        val ioDispatcher = get<CoroutineDispatchers>().io

        createDataStore(
            coroutineScope = CoroutineScope(ioDispatcher + SupervisorJob()),
            producePath = { androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath },
        )
    }
}
