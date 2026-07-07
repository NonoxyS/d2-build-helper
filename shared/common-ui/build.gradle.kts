import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common.ui"
}

commonMainDependencies {
    implementations(
        projects.shared.common,
        projects.shared.commonResources,
        projects.shared.coreMatch,

        libs.compose.multiplatform.material3,
        libs.moko.resources.compose,
        libs.coil.compose,
    )
}

androidMainDependencies {
    implementations(libs.androidx.activity.compose)
}
