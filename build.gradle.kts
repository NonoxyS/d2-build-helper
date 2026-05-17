plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.buildConfig).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.apollo).apply(false)

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
