plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.mvikotlin.core)
            api(libs.mvikotlin.main)
            api(libs.mvikotlin.logging)
            api(libs.mvikotlin.coroutines)
            api(libs.moko.mvvm.flow)
            api(libs.napier)
            api(libs.koin.core)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(projects.common)
        }
    }
}
