plugins {
    id("kmp-library")
    id("json-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.api)
            implementation(projects.common)
            implementation(projects.coreDomain)
            implementation(projects.coreNetwork)
            implementation(projects.corePresentation)
            implementation(projects.coreResources)
            implementation(libs.koin.core)
            implementation(libs.napier)
        }
        commonTest.dependencies {
            implementation(projects.common)
            implementation(projects.coreDomain)
            implementation(projects.coreResources)
            implementation(libs.mvikotlin.coroutines)
        }
    }
}
