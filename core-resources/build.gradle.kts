import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)
            implementation(projects.common)
            implementation(projects.commonResources)
            implementation(projects.coreNetwork)
            implementation(projects.coreStorage)
            api(libs.koin.core)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
