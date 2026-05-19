package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal fun Project.androidAppConfig(configure: ApplicationExtension.() -> Unit) {
    extensions.configure<ApplicationExtension>(configure)
}

internal fun Project.androidKotlinConfig(configure: KotlinAndroidProjectExtension.() -> Unit) {
    extensions.configure<KotlinAndroidProjectExtension>(configure)
}

internal fun Project.kotlinJvmCompilerOptions(configure: KotlinJvmCompilerOptions.() -> Unit) {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions(configure)
    }
}

internal fun Project.kotlinMultiplatformConfig(configure: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(configure)
}

fun Project.androidLibraryConfig(configure: KotlinMultiplatformAndroidLibraryTarget.() -> Unit) {
    kotlinMultiplatformConfig {
        extensions.configure<KotlinMultiplatformAndroidLibraryTarget>(configure)
    }
}

internal fun Project.composeCompilerConfig(configure: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure<ComposeCompilerGradlePluginExtension>(configure)
}
