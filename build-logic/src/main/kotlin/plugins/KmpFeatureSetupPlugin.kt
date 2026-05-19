package plugins

import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.getApiModule
import extensions.getPresentationModule
import extensions.isApiModule
import extensions.isImplModule
import extensions.isPresentationModule
import extensions.isUiModule
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpFeatureSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            wireDependencies()
        }
    }

    private fun Project.applyPlugins() {
        with(pluginManager) {
            apply("kmp-library")
            if (isUiModule) {
                apply("compose-multiplatform-setup")
            }
        }
    }

    private fun Project.wireDependencies() {
        when {
            isApiModule -> wireApiModule()
            isImplModule -> wireImplModule()
            isPresentationModule -> wirePresentationModule()
            isUiModule -> wireUiModule()
            else -> error(
                "kmp-feature-setup applied to '$path' but module name must be one of: " +
                    "api, impl, presentation, ui",
            )
        }
    }

    private fun Project.wireApiModule() {
        commonMainDependencies {
            api(project(":core-domain"))
            api(libs.mvikotlin.core)
        }
    }

    private fun Project.wireImplModule() {
        val featureApi = requireSibling(getApiModule(), kind = "api")
        commonMainDependencies {
            api(featureApi)
            implementation(project(":common"))
            implementation(project(":core-domain"))
            implementation(project(":core-mvikotlin"))
            implementation(libs.koin.core)
            implementation(libs.napier)
        }
        commonTestDependencies {
            implementation(libs.mvikotlin.coroutines)
        }
    }

    private fun Project.wirePresentationModule() {
        val featureApi = requireSibling(getApiModule(), kind = "api")
        commonMainDependencies {
            api(featureApi)
            api(project(":core-presentation"))
            implementation(project(":core-domain"))
            implementation(project(":common"))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
    }

    private fun Project.wireUiModule() {
        val featurePresentation = requireSibling(getPresentationModule(), kind = "presentation")
        commonMainDependencies {
            api(featurePresentation)
            implementation(project(":common"))
            implementation(project(":common-resources"))
            implementation(project(":common-ui"))
            implementation(project(":core-domain"))
            implementation(project(":core-navigation"))
            implementation(libs.compose.material3)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMainDependencies {
            implementation(libs.compose.ui.tooling)
        }
    }

    private fun Project.requireSibling(sibling: Project?, kind: String): Project = sibling
        ?: error(
            "kmp-feature-setup on '$path': sibling :$kind submodule not found under '${parent?.path}'.",
        )
}
