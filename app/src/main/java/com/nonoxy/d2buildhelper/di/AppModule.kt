package com.nonoxy.d2buildhelper.di

import com.apollographql.apollo3.ApolloClient
import com.google.firebase.ktx.Firebase
import com.nonoxy.d2buildhelper.BuildConfig
import com.nonoxy.d2buildhelper.data.repository.ApolloHeroBuildClient
import com.nonoxy.d2buildhelper.data.repository.FirebaseResourceClient
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient
import com.nonoxy.d2buildhelper.domain.repository.ResourceClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.stratz.com/graphql")
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.apiKey}")
            .build()
    }

    @Provides
    @Singleton
    fun provideHeroBuildClient(apolloClient: ApolloClient): HeroBuildClient {
        return ApolloHeroBuildClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideFirebaseClient(): Firebase {
        return Firebase
    }

    @Provides
    @Singleton
    fun provideResourceClient(firebaseClient: Firebase): ResourceClient {
        return FirebaseResourceClient(firebaseClient)
    }
}