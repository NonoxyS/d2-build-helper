package dev.nonoxy.d2buildhelper.core.di

import dev.nonoxy.d2buildhelper.common.di.commonModule
import dev.nonoxy.d2buildhelper.common.resources.di.commonResourcesModule
import dev.nonoxy.d2buildhelper.core.mvikotlin.di.coreMVIKotlinModule
import dev.nonoxy.d2buildhelper.core.network.di.coreNetworkModule
import dev.nonoxy.d2buildhelper.core.resources.di.coreResourcesModule
import dev.nonoxy.d2buildhelper.core.storage.di.coreStorageModule
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.di.featureGuideDetailsImplModule
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.di.featureGuideDetailsPresentationModule
import dev.nonoxy.d2buildhelper.feature.guides.impl.di.featureGuidesImplModule
import dev.nonoxy.d2buildhelper.feature.guides.presentation.di.featureGuidesPresentationModule
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    Napier.d(message = "start initKoin")

    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            commonModule,
            commonResourcesModule,

            coreStorageModule,
            coreMVIKotlinModule,
            coreNetworkModule,
            coreResourcesModule,

            featureGuidesImplModule,
            featureGuidesPresentationModule,

            featureGuideDetailsImplModule,
            featureGuideDetailsPresentationModule,
        )
    }

    Napier.d(message = "finish initKoin")
}
