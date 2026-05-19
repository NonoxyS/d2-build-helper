import extensions.androidLibraryConfig
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.presentation"
}

commonMainDependencies {
    api(projects.shared.coreMvikotlin)
    api(libs.moko.mvvm.flow)
    api(libs.androidx.lifecycle.viewmodel)
    implementation(projects.shared.common)
}
