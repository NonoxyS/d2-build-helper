import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.mvikotlin"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.mvikotlin.core)
            api(libs.mvikotlin.main)
            api(libs.mvikotlin.logging)
            api(libs.mvikotlin.coroutines)
            api(libs.napier)
            api(libs.koin.core)
        }
    }
}
