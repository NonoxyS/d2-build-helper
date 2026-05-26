import dev.icerock.gradle.MRVisibility
import extensions.androidLibraryConfig
import extensions.apis
import extensions.commonMainDependencies

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.moko.resources)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.common.resources"
}

commonMainDependencies {
    apis(libs.moko.resources)
}

multiplatformResources {
    resourcesPackage.set("dev.nonoxy.d2buildhelper.common.resources")
    resourcesClassName.set("MR")
    resourcesVisibility.set(MRVisibility.Public)
}
