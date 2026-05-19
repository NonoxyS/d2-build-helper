package plugins

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

            composeCompilerConfig {
                reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
            }

            commonMainDependencies {
                implementations(
                    libs.compose.runtime,
                    libs.compose.ui.tooling.preview,
                )
            }

            dependencies {
                // Workaround now instead of debugImplementation before
                // https://developer.android.com/kotlin/multiplatform/plugin#compose-preview-dependencies
                if (project.configurations.names.contains("androidRuntimeClasspath")) {
                    "androidRuntimeClasspath"(libs.compose.ui.tooling)
                } else {
                    debugImplementation(libs.compose.ui.tooling)
                }
            }
        }
    }

    private fun Project.applyPlugins() {
        with(pluginManager) {
            apply(libs.plugins.compose.compiler.get().pluginId)
            apply(libs.plugins.compose.multiplatform.get().pluginId)
        }
    }
}

val <T : KotlinDependencyHandler> T.composeBundle
    get() = listOf(
        project.libs.compose.runtime,
        project.libs.compose.foundation,
        project.libs.compose.ui,
        project.libs.compose.material3,
    ).toTypedArray()

val <T : Project> T.composeBundle
    get() = listOf(
        project.libs.compose.runtime,
        project.libs.compose.foundation,
        project.libs.compose.ui,
        project.libs.compose.material3,
    ).toTypedArray()
