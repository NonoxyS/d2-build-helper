package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClientImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ConstantsStorageImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ConstantsStorage
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreResourcesModule = module {
    factoryOf(::ConstantsApiClientImpl) { bind<ConstantsApiClient>() }
    singleOf(::ConstantsStorageImpl) { bind<ConstantsStorage>() }
    singleOf(::ResourcesRepositoryImpl) { bind<ResourcesRepository>() }
}
