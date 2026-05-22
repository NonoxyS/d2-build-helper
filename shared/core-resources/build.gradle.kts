import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import org.gradle.kotlin.dsl.kotlin

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
    implementation(projects.shared.coreNetwork)
}

commonTestDependencies {
    implementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.ktor.client.mock)
}
