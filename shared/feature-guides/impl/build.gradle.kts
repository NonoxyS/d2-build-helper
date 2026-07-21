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
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.impl"
}

commonMainDependencies {
    implementations(
        projects.shared.coreMatch.domain,
        projects.shared.coreNetwork,
        projects.shared.coreResources,
    )
}

commonTestDependencies {
    implementations(
        kotlin("test"),
        libs.kotlinx.coroutines.test,
        libs.mvikotlin.coroutines,
        libs.ktor.client.mock,
    )
}
