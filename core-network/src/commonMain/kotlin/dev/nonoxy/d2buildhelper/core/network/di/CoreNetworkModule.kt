package dev.nonoxy.d2buildhelper.core.network.di

import com.apollographql.apollo.ApolloClient
import dev.nonoxy.d2buildhelper.core.network.BuildConfig
import org.koin.dsl.module

val coreNetworkModule = module {
    single<ApolloClient> {
        ApolloClient.Builder()
            .serverUrl(BuildConfig.API_BASE_URL)
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.STRATZ_API_KEY}")
            .build()
    }
}
