package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.di

import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClientImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapperImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.repository.GuideDetailRepositoryImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.GuideDetailStoreFactory
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val featureGuideDetailsImplModule = module {
    factory<GuideDetailApiClient> { new(::GuideDetailApiClientImpl) }
    factoryOf<GuideDetailMapper>(::GuideDetailMapperImpl)
    factory<GuideDetailRepository> { new(::GuideDetailRepositoryImpl) }

    factory { (matchId: Long, steamAccountId: Long) ->
        GuideDetailStoreFactory(
            storeFactory = get(),
            guideDetailRepository = get(),
            resourcesRepository = get(),
            dispatchers = get()
        ).create(matchId = matchId, steamAccountId = steamAccountId)
    }
}
