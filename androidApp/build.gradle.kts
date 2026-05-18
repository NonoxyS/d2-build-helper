import utils.AppVersion

plugins {
    id("android-application-setup")
}

android {
    namespace = "dev.nonoxy.d2buildhelper.android"

    defaultConfig {
        applicationId = "dev.nonoxy.d2buildhelper.androidApp"
        versionCode = AppVersion.getVersionCode(project).get()
        versionName = AppVersion.getVersionName(project).get()
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/main/AndroidManifest.xml")
        res.srcDirs("src/main/res")
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activityCompose)
    implementation(libs.koin.android)
    implementation(libs.napier)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
}
