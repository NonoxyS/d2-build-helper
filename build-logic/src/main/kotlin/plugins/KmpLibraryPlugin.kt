package plugins

import extensions.androidLibrary
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.kotlinMultiplatformConfig
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.multiplatform.get().pluginId)
                apply(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
            }

            kotlinMultiplatformConfig {
                jvmToolchain(JAVA_VERSION)

                @Suppress("UnstableApiUsage")
                androidLibrary {
                    namespace = derivedNamespace(target)
                    compileSdk = libs.versions.android.compileSdk.get().toInt()
                    minSdk = libs.versions.android.minSdk.get().toInt()
                }

                jvm()

                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }

            commonMainDependencies {
                implementation(libs.kotlinx.coroutines.core)
            }

            commonTestDependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }

    private fun derivedNamespace(project: Project): String {
        val segments = project.path
            .removePrefix(":")
            .split(":", "-")
            .map { it.replace(Regex("[^A-Za-z0-9]"), "") }
            .filter { it.isNotEmpty() }
        return (listOf("dev", "nonoxy", "d2buildhelper") + segments).joinToString(".")
    }

    companion object {
        private const val JAVA_VERSION = 17
    }
}
