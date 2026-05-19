import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.coreNetwork)
            implementation(projects.shared.coreResources)
        }
        commonTest.dependencies {
            implementation(libs.mvikotlin.coroutines)
        }
    }
}
