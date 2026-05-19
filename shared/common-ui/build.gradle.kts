import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coil)
            api(libs.coil.network.ktor)
            api(projects.shared.commonResources)
            implementation(libs.compose.material3)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
        }
    }
}
