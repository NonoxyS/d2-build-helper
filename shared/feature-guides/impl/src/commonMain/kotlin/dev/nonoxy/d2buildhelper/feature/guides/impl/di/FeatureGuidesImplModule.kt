package dev.nonoxy.d2buildhelper.feature.guides.impl.di

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClientImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers.GuidesPageMapper
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers.GuidesPageMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository.GuidesRepositoryImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val featureGuidesImplModule = module {
    factory<GuidesApiClient> { new(::GuidesApiClientImpl) }
    factoryOf<GuidesPageMapper>(::GuidesPageMapperImpl)
    factory<GuidesRepository> { new(::GuidesRepositoryImpl) }

    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
}
