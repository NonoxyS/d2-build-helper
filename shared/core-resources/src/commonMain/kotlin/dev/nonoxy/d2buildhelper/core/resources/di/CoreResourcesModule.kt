package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClientImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ConstantsStorageImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ConstantsStorage
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import org.koin.dsl.module

val coreResourcesModule = module {
    factory<ConstantsApiClient> { ConstantsApiClientImpl(ktorClient = get()) }
    single<ConstantsStorage> {
        ConstantsStorageImpl(dataStore = get(), dispatchers = get(), json = get())
    }
    single<ResourcesRepository> {
        ResourcesRepositoryImpl(apiClient = get(), storage = get(), dispatchers = get())
    }
}
