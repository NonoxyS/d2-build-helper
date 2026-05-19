import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.koin.core)
        }
    }
}
