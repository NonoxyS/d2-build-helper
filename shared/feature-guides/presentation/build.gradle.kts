import extensions.androidLibraryConfig
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.presentation"
}

commonMainDependencies {
    implementation(projects.shared.commonResources)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.moko.resources.compose)
}
