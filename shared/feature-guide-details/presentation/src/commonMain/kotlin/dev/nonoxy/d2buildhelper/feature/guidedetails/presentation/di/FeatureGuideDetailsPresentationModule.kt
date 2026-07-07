package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.di

import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.GuideDetailViewModel
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailLabelMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailLabelMapperImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailStateMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailStateMapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val featureGuideDetailsPresentationModule = module {
    singleOf<UiGuideDetailStateMapper>(::UiGuideDetailStateMapperImpl)
    singleOf<UiGuideDetailLabelMapper>(::UiGuideDetailLabelMapperImpl)
    viewModel { (matchId: Long, steamAccountId: Long) ->
        GuideDetailViewModel(get { parametersOf(matchId, steamAccountId) }, get(), get())
    }
}
