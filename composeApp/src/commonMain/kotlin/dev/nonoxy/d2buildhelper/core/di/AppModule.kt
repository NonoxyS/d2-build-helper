package dev.nonoxy.d2buildhelper.core.di

import Dota___Build_Helper.composeApp.BuildConfig
import com.apollographql.apollo.ApolloClient
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesDataSource
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.ImageResourcesApi
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.ImageResourcesDataSource
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.ConstantResources
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.ConstantResourcesDataSource
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.usecases.GetGuidesUseCase
import dev.nonoxy.d2buildhelper.features.guides.domain.usecases.GetImagesUseCase
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<ApolloClient> {
        ApolloClient.Builder()
            .serverUrl(BuildConfig.API_BASE_URL)
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.STRATZ_API_KEY}")
            .build()
    }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_BASE_URL,
            supabaseKey = BuildConfig.SUPABASE_API_KEY
        ) {
            install(Storage)
        }
    }

    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::ImageResourcesDataSource) bind ImageResourcesApi::class
    singleOf(::ConstantResourcesDataSource) bind ConstantResources::class
    singleOf(::ResourcesRepository)

    factoryOf(::GetGuidesUseCase)
    factoryOf(::GetImagesUseCase)

    viewModelOf(::GuidesViewModel)
}
