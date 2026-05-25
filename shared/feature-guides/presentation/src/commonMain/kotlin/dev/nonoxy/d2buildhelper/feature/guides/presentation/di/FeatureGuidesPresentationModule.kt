package dev.nonoxy.d2buildhelper.feature.guides.presentation.di

import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiMatchPlayerPositionMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiMatchPlayerPositionMapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureGuidesPresentationModule = module {
    singleOf(::UiMatchPlayerPositionMapperImpl) bind UiMatchPlayerPositionMapper::class
    singleOf(::UiGuidesStateMapperImpl) bind UiGuidesStateMapper::class
    singleOf(::UiGuidesLabelMapperImpl) bind UiGuidesLabelMapper::class
    viewModelOf(::GuidesViewModel)
}
