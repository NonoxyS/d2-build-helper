package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.api.image.ImageResourcesApi
import dev.nonoxy.d2buildhelper.core.resources.data.api.image.ImageResourcesDataSource
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.ConstantResources
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.ConstantResourcesDataSource
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreResourcesModule = module {
    singleOf(::ImageResourcesDataSource) bind ImageResourcesApi::class
    singleOf(::ConstantResourcesDataSource) bind ConstantResources::class
    singleOf(::ResourcesRepositoryImpl) bind ResourcesRepository::class
}
