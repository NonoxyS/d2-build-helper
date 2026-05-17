package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

internal fun DependencyHandler.implementation(dependency: Any): Dependency? =
    add("implementation", dependency)

internal fun DependencyHandler.debugImplementation(dependency: Any): Dependency? =
    add("debugImplementation", dependency)

internal fun DependencyHandler.api(dependency: Any) {
    add("api", dependency)
}

internal fun Project.dependencies(dependencies: DependencyHandlerScope.() -> Unit) {
    dependencies(dependencies)
}

fun DependencyHandlerScope.implementations(vararg dependencies: Any) {
    dependencies.forEach(::implementation)
}

fun DependencyHandlerScope.apis(vararg dependencies: Any) {
    dependencies.forEach(::api)
}

fun KotlinDependencyHandler.implementations(vararg dependencies: Any) {
    dependencies.forEach(::implementation)
}

fun KotlinDependencyHandler.apis(vararg dependencies: Any) {
    dependencies.forEach(::api)
}
