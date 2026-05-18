plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.resources)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.nonoxy.d2buildhelper.common.resources"
    generateResClass = always
}
