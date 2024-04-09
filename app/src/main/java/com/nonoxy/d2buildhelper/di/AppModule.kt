package com.nonoxy.d2buildhelper.di

import com.apollographql.apollo3.ApolloClient
import com.google.firebase.ktx.Firebase
import com.nonoxy.d2buildhelper.BuildConfig
import com.nonoxy.d2buildhelper.data.repository.ApolloHeroBuildClient
import com.nonoxy.d2buildhelper.data.repository.FirebaseResourceClient
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient
import com.nonoxy.d2buildhelper.domain.repository.ResourceClient
import com.nonoxy.d2buildhelper.domain.usecases.GetGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuideBuildUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetAdditionalImageUrlByNameUseCase
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
    fun provideGetGuidesInfoUseCase(heroBuildClient: HeroBuildClient): GetGuidesInfoUseCase {
        return GetGuidesInfoUseCase(heroBuildClient)
    }

    @Provides
    @Singleton
    fun provideGetHeroGuidesInfoUseCase(heroBuildClient: HeroBuildClient): GetHeroGuidesInfoUseCase {
        return GetHeroGuidesInfoUseCase(heroBuildClient)
    }

    @Provides
    @Singleton
    fun provideGetHeroGuideBuildUseCase(heroBuildClient: HeroBuildClient): GetHeroGuideBuildUseCase {
        return GetHeroGuideBuildUseCase(heroBuildClient)
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

    @Provides
    @Singleton
    fun provideGetHeroNameByIdUseCase(firebaseClient: ResourceClient): GetHeroNameByIdUseCase {
        return GetHeroNameByIdUseCase(firebaseClient)
    }

    @Provides
    @Singleton
    fun provideGetItemNameByIdUseCase(firebaseClient: ResourceClient): GetItemNameByIdUseCase {
        return GetItemNameByIdUseCase(firebaseClient)
    }

    @Provides
    @Singleton
    fun provideGetHeroImageUrlByNameUseCase(firebaseClient: ResourceClient): GetHeroImageUrlByNameUseCase {
        return GetHeroImageUrlByNameUseCase(firebaseClient)
    }

    @Provides
    @Singleton
    fun provideGetItemImageUrlByNameUseCase(firebaseClient: ResourceClient): GetItemImageUrlByNameUseCase {
        return GetItemImageUrlByNameUseCase(firebaseClient)
    }

    @Provides
    @Singleton
    fun provideGetAdditionalImageUrlByNameUseCase(firebaseClient: ResourceClient): GetAdditionalImageUrlByNameUseCase {
        return GetAdditionalImageUrlByNameUseCase(firebaseClient)
    }
}