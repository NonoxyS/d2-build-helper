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
            api(projects.shared.coreDomain)
            implementation(projects.shared.common)
            implementation(projects.shared.commonResources)
            implementation(projects.shared.coreNetwork)
            implementation(projects.shared.coreStorage)
            api(libs.koin.core)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
