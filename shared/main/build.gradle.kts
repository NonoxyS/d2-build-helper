import extensions.androidLibraryConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.composeMultiplatformSetup)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.main"
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}

commonMainDependencies {
    implementation(projects.shared.common)
    implementation(projects.shared.coreDomain)
    implementation(projects.shared.commonResources)
    implementation(projects.shared.corePresentation)
    implementation(projects.shared.coreNavigation)
    implementation(projects.shared.commonUi)
    implementation(projects.shared.coreNetwork)
    implementation(projects.shared.coreResources)
    implementation(projects.shared.featureGuides.api)
    implementation(projects.shared.featureGuides.impl)
    implementation(projects.shared.featureGuides.presentation)
    implementation(projects.shared.featureGuides.ui)

    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.androidx.lifecycle.viewmodelCompose)

    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
}

commonTestDependencies {
    implementation(libs.compose.ui.test)
}
