plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.api)
            api(projects.corePresentation)
            implementation(projects.coreDomain)
            implementation(projects.common)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}
