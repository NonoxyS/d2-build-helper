package plugins

import extensions.androidConfig
import extensions.commonMainDependencies
import extensions.composeCompilerConfig
import extensions.debugImplementation
import extensions.implementations
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class ComposeMultiplatformSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()

            if (pluginManager.hasPlugin(libs.plugins.androidLibrary.get().pluginId)) {
                androidConfig {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            composeCompilerConfig {
                reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
            }

            commonMainDependencies {
                implementations(
                    libs.compose.multiplatform.runtime,
                    libs.compose.multiplatform.uiToolingPreview
                )
            }

            dependencies {
                // Workaround now instead of debugImplementation before
                // https://developer.android.com/kotlin/multiplatform/plugin#compose-preview-dependencies
                if (project.configurations.names.contains("androidRuntimeClasspath")) {
                    "androidRuntimeClasspath"(libs.compose.multiplatform.uiTooling)
                } else {
                    debugImplementation(libs.compose.multiplatform.uiTooling)
                }
            }
        }
    }

    private fun Project.applyPlugins() {
        with(pluginManager) {
            apply(libs.plugins.compose.compiler.get().pluginId)
            apply(libs.plugins.compose.multiplatform.get().pluginId)
            apply(libs.plugins.conventionPlugin.jsonSerialization.get().pluginId)
        }
    }
}

val <T : KotlinDependencyHandler> T.composeBundle
    get() = listOf(
        project.libs.compose.multiplatform.runtime,
        project.libs.compose.multiplatform.foundation,
        project.libs.compose.multiplatform.ui,
        project.libs.compose.multiplatform.material3,
    ).toTypedArray()

val <T : Project> T.composeBundle
    get() = listOf(
        project.libs.compose.multiplatform.runtime,
        project.libs.compose.multiplatform.foundation,
        project.libs.compose.multiplatform.ui,
        project.libs.compose.multiplatform.material3,
    ).toTypedArray()
