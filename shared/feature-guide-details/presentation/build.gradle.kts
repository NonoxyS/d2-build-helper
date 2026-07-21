import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guidedetails.presentation"
}

commonMainDependencies {
    implementations(
        projects.shared.commonUi,
        projects.shared.coreMatch.domain,
        projects.shared.coreMatch.presentation,
        projects.shared.commonResources,
        libs.kotlinx.collections.immutable,
        libs.moko.resources.compose,
    )
}

commonTestDependencies {
    implementations(
        kotlin("test"),
    )
}
