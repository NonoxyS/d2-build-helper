import extensions.androidLibraryConfig
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.ui"
}

commonMainDependencies {
    implementation(libs.compose.material.icons.extended)
}
