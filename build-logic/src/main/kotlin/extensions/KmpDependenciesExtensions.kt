package extensions

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.commonMainDependencies(dependencies: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.commonMain.dependencies(dependencies)
    }
}

fun Project.androidMainDependencies(dependencies: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.androidMain.dependencies(dependencies)
    }
}

fun Project.iosMainDependencies(dependencies: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.iosMain.dependencies(dependencies)
    }
}

fun Project.commonTestDependencies(dependencies: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.commonTest.dependencies(dependencies)
    }
}
