import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.match.presentation"
}

commonMainDependencies {
    implementations(
        projects.shared.coreMatch.domain,
        projects.shared.commonResources,
        libs.moko.resources,
    )
}
