plugins {
    // Android
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false

    // Kotlin
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatformAndroidLibrary) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // Compose
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false

    // Other
    alias(libs.plugins.buildConfig) apply false
    alias(libs.plugins.apollo) apply false
    alias(libs.plugins.moko.resources) apply false

    // Convention plugins
    alias(libs.plugins.conventionPlugin.kmpLibrary) apply false
    alias(libs.plugins.conventionPlugin.kmpFeatureSetup) apply false
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup) apply false
    alias(libs.plugins.conventionPlugin.jsonSerialization) apply false
    alias(libs.plugins.conventionPlugin.androidApplicationSetup) apply false

    alias(libs.plugins.detekt)
}

apply(from = "$rootDir/linters/androidLint/lintConfiguration.gradle")

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose.kode)
    detektPlugins(libs.detekt.compose.twitter)
}

detekt {
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true
    autoCorrect = false
    description = "Detekt with formatting."
    baseline = file("$rootDir/linters/detekt/baseline.xml")
    config.setFrom(files("$rootDir/linters/detekt/config.yml"))
}

fun SourceTask.setupDetektFolders() {
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/.gradle/**")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    setupDetektFolders()

    reports {
        html {
            required.set(true)
            outputLocation.set(file("$rootDir/build/reports/detekt/detekt.html"))
        }
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    setupDetektFolders()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
