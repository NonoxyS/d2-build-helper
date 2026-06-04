package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.di

import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailLabelMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailLabelMapperImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailStateMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailStateMapperImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featureGuideDetailsPresentationModule = module {
    singleOf(::UiGuideDetailStateMapperImpl) { bind<UiGuideDetailStateMapper>() }
    singleOf(::UiGuideDetailLabelMapperImpl) { bind<UiGuideDetailLabelMapper>() }
}
