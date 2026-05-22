import extensions.androidLibraryConfig
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.resources"
}

commonMainDependencies {
    api(projects.shared.coreDomain)
    implementation(projects.shared.common)
    implementation(projects.shared.commonResources)
    implementation(projects.shared.coreNetwork)
}
