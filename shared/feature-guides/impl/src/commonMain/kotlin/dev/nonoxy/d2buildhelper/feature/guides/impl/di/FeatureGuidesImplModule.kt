package dev.nonoxy.d2buildhelper.feature.guides.impl.di

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClientImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository.GuidesRepositoryImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import org.koin.dsl.module

val featureGuidesImplModule = module {
    factory<GuidesApiClient> { GuidesApiClientImpl(ktorClient = get()) }
    factory<GuidesRepository> { GuidesRepositoryImpl(apiClient = get(), coroutineDispatchers = get()) }

    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
}
