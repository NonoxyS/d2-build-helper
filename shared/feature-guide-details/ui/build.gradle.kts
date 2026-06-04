import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guidedetails.ui"
}

commonMainDependencies {
    implementations(
        libs.compose.multiplatform.material.iconsExtended,
        libs.coil.compose,
        libs.kotlinx.collections.immutable
    )
}
