import dev.icerock.gradle.MRVisibility

plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
    alias(libs.plugins.moko.resources)
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
