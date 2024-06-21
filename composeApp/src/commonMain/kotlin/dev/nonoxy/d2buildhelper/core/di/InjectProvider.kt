package dev.nonoxy.d2buildhelper.core.di

import Dota___Build_Helper.composeApp.BuildConfig
import com.apollographql.apollo3.ApolloClient
import kotlin.reflect.KClass

object InjectProvider {
    private val dependencies: HashMap<String, Any> = hashMapOf()

    init {
        addDependency(type = ApolloClient::class, dependency = createApolloClient())
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
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
            .build()
    }
}
