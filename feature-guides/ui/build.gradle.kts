plugins {
    id("kmp-feature-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.materialIconsExtended)
        }
    }
}
