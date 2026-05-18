plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coil)
            api(libs.coil.network.ktor)
            api(projects.commonResources)
            implementation(libs.compose.material3)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
        }
    }
}
