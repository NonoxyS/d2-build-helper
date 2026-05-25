import extensions.androidLibraryConfig
import extensions.apis
import extensions.commonMainDependencies
import extensions.implementations

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.storage"
}

commonMainDependencies {
    implementations(
        projects.shared.common,
    )

    apis(
        libs.androidx.datastore,
    )
}
