package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.ConstantsApiClientImpl
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import dev.nonoxy.d2buildhelper.core.resources.data.storage.ConstantsStorage
import dev.nonoxy.d2buildhelper.core.resources.data.storage.ConstantsStorageImpl
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val coreResourcesModule = module {
    factory<ConstantsApiClient> { new(::ConstantsApiClientImpl) }
    single<ConstantsStorage> { new(::ConstantsStorageImpl) }
    single<ResourcesRepository> { new(::ResourcesRepositoryImpl) }
}
