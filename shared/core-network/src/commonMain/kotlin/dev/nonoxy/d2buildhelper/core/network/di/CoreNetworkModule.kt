package dev.nonoxy.d2buildhelper.core.network.di

import com.apollographql.apollo.ApolloClient
import dev.nonoxy.d2buildhelper.core.network.BuildConfig
import dev.nonoxy.d2buildhelper.core.network.ktor.di.coreNetworkKtorModule
import org.koin.dsl.module

val coreNetworkModule = module {
    includes(coreNetworkKtorModule)

    single<ApolloClient> {
        ApolloClient.Builder()
            .serverUrl(BuildConfig.API_BASE_URL)
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.STRATZ_API_KEY}")
            .build()
    }
}
