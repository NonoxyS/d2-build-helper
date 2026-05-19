package extensions

import org.gradle.api.Project

internal enum class FeatureModuleType {
    API,
    IMPL,
    PRESENTATION,
    UI;

    val actualName: String = name.lowercase()
}

internal val Project.isApiModule
    get() = name.lowercase() == FeatureModuleType.API.actualName

internal val Project.isImplModule
    get() = name.lowercase() == FeatureModuleType.IMPL.actualName

internal val Project.isPresentationModule
    get() = name.lowercase() == FeatureModuleType.PRESENTATION.actualName

internal val Project.isUiModule
    get() = name.lowercase() == FeatureModuleType.UI.actualName

internal fun Project.getApiModule(): Project? = parent
    ?.childProjects
    ?.values
    ?.firstOrNull { it.isApiModule }

internal fun Project.getPresentationModule(): Project? = parent
    ?.childProjects
    ?.values
    ?.firstOrNull { it.isPresentationModule }

internal fun <T> T.asList(): List<T> = listOf(this)
