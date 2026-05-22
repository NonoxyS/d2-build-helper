package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClientImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import org.koin.dsl.module

val coreResourcesModule = module {
    factory<ConstantsApiClient> { ConstantsApiClientImpl(ktorClient = get()) }
    single<ResourcesRepository> { ResourcesRepositoryImpl(constantsApiClient = get(), coroutineDispatchers = get()) }
}
