package dev.nonoxy.d2buildhelper.core.di

import Dota___Build_Helper.composeApp.BuildConfig
import com.apollographql.apollo3.ApolloClient

object InjectProvider {
    private val dependencies: HashMap<String, Any> = hashMapOf()

    init {
        addDependency(
            key = DependencyKey.ApolloClient.title,
            dependency = ApolloClient.Builder()
                .serverUrl(BuildConfig.API_BASE_URL)
                .addHttpHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
        )
    }

    fun addDependency(key: String, dependency: Any) {
        dependencies[key] = dependency
    }

    fun <T> getDependency(key: String): T {
        return dependencies[key] as T
    }

    fun getApolloClient(): ApolloClient = getDependency(DependencyKey.ApolloClient.title)
}

private enum class DependencyKey(val title: String) {
    ApolloClient("apolloClient"),
}