plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.presentation)
            implementation(projects.common)
            implementation(projects.commonResources)
            implementation(projects.commonUi)
            implementation(projects.coreDomain)
            implementation(projects.coreNavigation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling)
        }
    }
}
