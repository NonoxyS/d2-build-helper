import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guidedetails.impl"
}

commonMainDependencies {
    implementations(
        projects.shared.coreNetwork,
        projects.shared.coreResources,
        projects.shared.coreMatch,
    )
}
