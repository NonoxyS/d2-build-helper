import extensions.androidLibraryConfig
import extensions.apis
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.mvikotlin"
}

commonMainDependencies {
    apis(
        libs.mvikotlin.core,
        libs.mvikotlin.main,
        libs.mvikotlin.logging,
        libs.mvikotlin.coroutines,
    )
}
