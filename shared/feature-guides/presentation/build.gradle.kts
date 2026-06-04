import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.presentation"
}

commonMainDependencies {
    implementations(
        projects.shared.coreMatch,
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
