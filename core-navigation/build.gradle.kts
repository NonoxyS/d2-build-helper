plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.navigation)
        }
    }
}
