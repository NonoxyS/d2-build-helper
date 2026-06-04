import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations
import org.gradle.kotlin.dsl.kotlin

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guidedetails.impl"
}

commonMainDependencies {
    implementations(
        projects.shared.coreNetwork,
        projects.shared.coreResources,
        projects.shared.coreMatch,
    )
}

commonTestDependencies {
    implementations(
        kotlin("test"),
        libs.kotlinx.coroutines.test,
    )
}
