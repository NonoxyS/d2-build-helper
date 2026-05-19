import dev.icerock.gradle.MRVisibility
import extensions.androidLibraryConfig

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
    alias(libs.plugins.moko.resources)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.moko.resources)
            api(libs.moko.resources.compose)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("dev.nonoxy.d2buildhelper.common.resources")
    resourcesClassName.set("MR")
    resourcesVisibility.set(MRVisibility.Public)
}
