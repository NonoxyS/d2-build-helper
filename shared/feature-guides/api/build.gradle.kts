import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.api"
}
