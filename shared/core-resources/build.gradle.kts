import extensions.androidLibraryConfig
import extensions.apis
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations
import org.gradle.kotlin.dsl.kotlin

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.resources"
}

commonMainDependencies {
    apis(projects.shared.coreDomain)
    implementations(
        projects.shared.common,
        projects.shared.coreNetwork,
        projects.shared.coreStorage,
        libs.kotlinx.datetime,
    )
}

commonTestDependencies {
    implementations(
        kotlin("test"),
        libs.kotlinx.coroutines.test,
        libs.ktor.client.mock,
    )
}
