import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common.ui"
}

commonMainDependencies {
    api(libs.coil)
    api(libs.coil.network.ktor)
    api(projects.shared.commonResources)
    implementation(libs.compose.material3)
}

androidMainDependencies {
    implementation(libs.androidx.activityCompose)
}
