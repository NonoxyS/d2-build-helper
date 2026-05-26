package plugins

import extensions.apis
import extensions.asList
import extensions.commonMainDependencies
import extensions.getApiModule
import extensions.getPresentationModule
import extensions.implementations
import extensions.isApiModule
import extensions.isPresentationModule
import extensions.isUiModule
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpFeatureSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            configureFeatureModuleDependencies()
        }
    }

    private fun Project.applyPlugins() {
        with(pluginManager) {
            apply(libs.plugins.conventionPlugin.kmpLibrary.get().pluginId)

            if (project.isApiModule || project.isPresentationModule) {
                // Need for inferring stability
                apply(libs.plugins.compose.compiler.get().pluginId)
            }
        }
    }

    private fun Project.configureFeatureModuleDependencies() {

        // Need for inferring stability of public models
        val composeRuntimeApiPresentationModuleDependencies = listOf(
            libs.compose.multiplatform.runtime
        ).takeIf { project.isApiModule || project.isPresentationModule }

        val implModuleDependencies = when (project.isApiModule) {
            true -> null
            false -> listOfNotNull(
                project(":shared:common"),
                project.getApiModule()
            )
        }

        val presentationModuleDependencies = listOf(
            project(":shared:core-presentation"),
            libs.koin.core.viewmodel,
        ).takeIf { project.isPresentationModule }

        val uiModuleDependencies = listOf(
            project(":shared:core-navigation"),
            project(":shared:common-resources"),
            project(":shared:common-ui"),
            libs.koin.composeMultiplatform.viewmodel,
            libs.moko.resources.compose,
            *composeBundle
        ).plus(
            project
                .getPresentationModule()
                ?.asList()
                .orEmpty()
        ).takeIf { project.isUiModule }

        val commonDependencies = listOf(
            project(":shared:core-domain"),
        )

        val nonUiModuleDependencies = when (project.isUiModule) {
            true -> null
            false -> project(":shared:core-mvikotlin").asList()
        }

        commonMainDependencies {
            implementations(
                *commonDependencies.toTypedArray(),
                *implModuleDependencies?.toTypedArray().orEmpty(),
                *uiModuleDependencies?.toTypedArray().orEmpty(),
                *composeRuntimeApiPresentationModuleDependencies?.toTypedArray().orEmpty(),
                *nonUiModuleDependencies?.toTypedArray().orEmpty()
            )

            apis(*presentationModuleDependencies?.toTypedArray().orEmpty())
        }
    }
}
