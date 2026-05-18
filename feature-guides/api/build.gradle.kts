plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)
            api(libs.mvikotlin.core)
        }
    }
}
