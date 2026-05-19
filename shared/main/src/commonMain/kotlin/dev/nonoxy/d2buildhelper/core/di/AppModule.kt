package dev.nonoxy.d2buildhelper.core.di

import dev.nonoxy.d2buildhelper.common.di.commonModule
import dev.nonoxy.d2buildhelper.core.network.di.coreNetworkModule
import dev.nonoxy.d2buildhelper.core.mvikotlin.di.coreMVIKotlinModule
import dev.nonoxy.d2buildhelper.core.resources.di.coreResourcesModule
import dev.nonoxy.d2buildhelper.core.storage.di.coreStorageModule
import dev.nonoxy.d2buildhelper.feature.guides.impl.di.featureGuidesImplModule
import dev.nonoxy.d2buildhelper.feature.guides.presentation.di.featureGuidesPresentationModule
import org.koin.dsl.module

val appModule = module {
    includes(
        commonModule,
        coreMVIKotlinModule,
        coreNetworkModule,
        coreStorageModule,
        coreResourcesModule,
        featureGuidesImplModule,
        featureGuidesPresentationModule,
    )
}
