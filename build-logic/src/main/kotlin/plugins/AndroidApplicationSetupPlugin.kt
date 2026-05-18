package plugins

import extensions.androidAppConfig
import extensions.androidKotlinConfig
import extensions.kotlinJvmCompilerOptions
import extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class AndroidApplicationSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.android.application.get().pluginId)
                apply("org.jetbrains.kotlin.android")
                apply(libs.plugins.compose.asProvider().get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
            }

            androidAppConfig {
                compileSdk = libs.versions.android.compileSdk.get().toInt()

                defaultConfig {
                    minSdk = libs.versions.android.minSdk.get().toInt()
                    targetSdk = libs.versions.android.targetSdk.get().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    compose = true
                }
            }

            androidKotlinConfig {
                jvmToolchain(17)
            }

            kotlinJvmCompilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }
}
