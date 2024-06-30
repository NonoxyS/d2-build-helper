package dev.nonoxy.d2buildhelper.core.di

import Dota___Build_Helper.composeApp.BuildConfig
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.crossfade
import com.apollographql.apollo3.ApolloClient
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.ImageResourcesDataSource
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.ConstantResourcesDataSource
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.GetGuidesAndImagesUseCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import kotlin.reflect.KClass

object InjectProvider {
    private val dependencies: HashMap<String, Any> = hashMapOf()

    init {
        addDependency(type = ApolloClient::class, dependency = createApolloClient())
        addDependency(type = SupabaseClient::class, dependency = createSupabaseClient())
        addDependency(type = GetGuidesAndImagesUseCase::class, dependency = createGetGuidesAndImagesUseCase())
    }

    fun addDependency(type: KClass<*>, dependency: Any) {
        if (type.qualifiedName == null)
            throw IllegalArgumentException("Local and anonymous can not be added")

        if (!dependencies.contains(type.qualifiedName)) {
            dependencies[type.qualifiedName!!] = dependency
        }
    }

    fun <T> getDependency(type: KClass<*>): T {
        @Suppress("UNCHECKED_CAST")
        return dependencies[type.qualifiedName] as T
    }

    private fun createApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_BASE_URL)
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.STRATZ_API_KEY}")
            .build()
    }

    private fun createSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_BASE_URL,
            supabaseKey = BuildConfig.SUPABASE_API_KEY
        ) {
            install(Storage)
        }
    }

    private fun createGetGuidesAndImagesUseCase(): GetGuidesAndImagesUseCase {
        return GetGuidesAndImagesUseCase(
            guidesRepository = GuidesRepository(),
            resourcesRepository = ResourcesRepository(
                imageResourcesDataSource = ImageResourcesDataSource(),
                constantResourcesDataSource = ConstantResourcesDataSource()
            )
        )
    }

    fun createCoinImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader
            .Builder(context)
            .crossfade(true)
            .build()
    }
}
