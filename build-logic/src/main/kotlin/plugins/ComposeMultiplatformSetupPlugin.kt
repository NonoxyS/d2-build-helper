package plugins

import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeMultiplatformSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.compose.asProvider().get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
            }

            commonMainDependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui)
            }

            androidMainDependencies {
                implementation(libs.compose.ui.tooling)
            }
        }
    }
}
