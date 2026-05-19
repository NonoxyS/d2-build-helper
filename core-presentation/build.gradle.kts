plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreMvikotlin)
            api(libs.moko.mvvm.flow)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(projects.common)
        }
    }
}
