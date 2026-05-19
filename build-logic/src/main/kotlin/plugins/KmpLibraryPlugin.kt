package plugins

import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations
import extensions.kotlinMultiplatformConfig
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            configureKotlin()
            configureAndroid()
            configureJvm()
            configureIos()
            configureDependencies()
        }
    }

    private fun Project.applyPlugins() {
        with(pluginManager) {
            apply(libs.plugins.kotlin.multiplatform.get().pluginId)
            apply(libs.plugins.kotlin.multiplatformAndroidLibrary.get().pluginId)
        }
    }

    private fun Project.configureKotlin() {
        kotlinMultiplatformConfig {
            jvmToolchain(libs.versions.javaVersion.get().toInt())

            compilerOptions {
                freeCompilerArgs.addAll(
                    "-Xcontext-parameters",
                    "-Xexpect-actual-classes",
                )
            }
        }
    }

    private fun Project.configureAndroid() {
        androidLibraryConfig {
            compileSdk = libs.versions.android.compileSdk.get().toInt()
            minSdk = libs.versions.android.minSdk.get().toInt()

            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
            }
        }
    }

    private fun Project.configureJvm() {
        kotlinMultiplatformConfig {
            jvm()
        }
    }

    private fun Project.configureIos() {
        kotlinMultiplatformConfig {
            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }
    }

    private fun Project.configureDependencies() {
        commonMainDependencies {
            implementations(
                libs.koin.core,
                libs.kotlinx.coroutines.core,
                libs.kotlinx.datetime,
                libs.napier,
            )
        }

        androidMainDependencies {
            implementations(
                libs.koin.android,
                libs.kotlinx.coroutines.android,
            )
        }

        commonTestDependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
