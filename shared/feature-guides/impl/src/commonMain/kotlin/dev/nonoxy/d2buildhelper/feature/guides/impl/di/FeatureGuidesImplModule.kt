package dev.nonoxy.d2buildhelper.feature.guides.impl.di

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.GuidesApi
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.GuidesDataSource
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository.GuidesRepository
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository.GuidesRepositoryImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureGuidesImplModule = module {
    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::GuidesRepositoryImpl) bind GuidesRepository::class

    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
}
