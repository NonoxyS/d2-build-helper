package plugins

import extensions.androidConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.kotlinMultiplatformConfig
import extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.multiplatform.get().pluginId)
                apply(libs.plugins.android.library.get().pluginId)
            }

            kotlinMultiplatformConfig {
                jvmToolchain(JAVA_VERSION)

                androidTarget {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
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

            androidConfig {
                namespace = derivedNamespace(target)
                compileSdk = libs.versions.android.compileSdk.get().toInt()
                defaultConfig {
                    minSdk = libs.versions.android.minSdk.get().toInt()
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
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
