package dev.nonoxy.d2buildhelper.features.guides.impl.di

import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesLabelMapperImpl
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesStateMapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureGuidesImplModule = module {
    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
    singleOf(::UiGuidesStateMapperImpl) bind UiGuidesStateMapper::class
    singleOf(::UiGuidesLabelMapperImpl) bind UiGuidesLabelMapper::class
    viewModelOf(::GuidesViewModel)
}
