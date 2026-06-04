package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.di

import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClientImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapperImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.repository.GuideDetailRepositoryImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.GuideDetailStoreFactory
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featureGuideDetailsImplModule = module {
    factoryOf(::GuideDetailApiClientImpl) { bind<GuideDetailApiClient>() }
    factoryOf(::GuideDetailMapperImpl) { bind<GuideDetailMapper>() }
    factoryOf(::GuideDetailRepositoryImpl) { bind<GuideDetailRepository>() }

    factory { (matchId: Long, steamAccountId: Long) ->
        GuideDetailStoreFactory(get(), get(), get(), get()).create(matchId, steamAccountId)
    }
}
