plugins {
    id("kmp-library")
    id("json-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)
            implementation(projects.common)
            implementation(projects.commonResources)
            implementation(projects.coreNetwork)
            implementation(projects.coreStorage)
            api(libs.koin.core)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
