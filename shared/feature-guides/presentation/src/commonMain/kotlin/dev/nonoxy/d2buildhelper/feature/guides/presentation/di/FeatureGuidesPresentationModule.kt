package dev.nonoxy.d2buildhelper.feature.guides.presentation.di

import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapperImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureGuidesPresentationModule = module {
    singleOf(::UiGuidesStateMapperImpl) { bind<UiGuidesStateMapper>() }
    singleOf(::UiGuidesLabelMapperImpl) { bind<UiGuidesLabelMapper>() }
    viewModelOf(::GuidesViewModel)
}
