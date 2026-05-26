import extensions.androidLibraryConfig
import extensions.apis
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.presentation"
}

commonMainDependencies {
    apis(
        projects.shared.coreMvikotlin,
        libs.moko.mvvm.flow,
        libs.androidx.lifecycle.viewmodel,
    )
    implementations(projects.shared.common)
}
