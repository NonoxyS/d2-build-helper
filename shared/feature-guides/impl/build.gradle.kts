import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import org.gradle.kotlin.dsl.kotlin

plugins {
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.feature.guides.impl"
}

commonMainDependencies {
    implementation(projects.shared.coreNetwork)
    implementation(projects.shared.coreResources)
}

commonTestDependencies {
    implementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.mvikotlin.coroutines)
    implementation(libs.ktor.client.mock)
}
