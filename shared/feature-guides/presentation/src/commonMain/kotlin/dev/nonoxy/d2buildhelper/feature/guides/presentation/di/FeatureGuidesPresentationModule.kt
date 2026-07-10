package dev.nonoxy.d2buildhelper.feature.guides.presentation.di

import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapperImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureGuidesPresentationModule = module {
    factoryOf<UiGuidesStateMapper>(::UiGuidesStateMapperImpl)
    factoryOf<UiGuidesLabelMapper>(::UiGuidesLabelMapperImpl)
    viewModelOf(::GuidesViewModel)
}
