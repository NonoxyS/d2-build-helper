import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.navigation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.navigation)
        }
    }
}
