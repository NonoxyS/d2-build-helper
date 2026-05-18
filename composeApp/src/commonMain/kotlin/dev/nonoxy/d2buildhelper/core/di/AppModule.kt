package dev.nonoxy.d2buildhelper.core.di

import dev.nonoxy.d2buildhelper.common.di.commonModule
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesDataSource
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepositoryImpl
import dev.nonoxy.d2buildhelper.core.network.di.coreNetworkModule
import dev.nonoxy.d2buildhelper.core.presentation.di.coreMVIKotlinModule
import dev.nonoxy.d2buildhelper.core.resources.di.coreResourcesModule
import dev.nonoxy.d2buildhelper.core.storage.di.coreStorageModule
import dev.nonoxy.d2buildhelper.features.guides.impl.di.featureGuidesImplModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    includes(
        commonModule,
        coreMVIKotlinModule,
        coreNetworkModule,
        coreStorageModule,
        coreResourcesModule,
        featureGuidesImplModule,
    )

    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::GuidesRepositoryImpl) bind GuidesRepository::class
}
