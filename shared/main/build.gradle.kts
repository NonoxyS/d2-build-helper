import extensions.androidLibraryConfig
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
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

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.common)
            implementation(projects.shared.coreDomain)
            implementation(projects.shared.commonResources)
            implementation(projects.shared.corePresentation)
            implementation(projects.shared.coreNavigation)
            implementation(projects.shared.commonUi)
            implementation(projects.shared.coreNetwork)
            implementation(projects.shared.coreStorage)
            implementation(projects.shared.coreResources)
            implementation(projects.shared.featureGuides.api)
            implementation(projects.shared.featureGuides.impl)
            implementation(projects.shared.featureGuides.presentation)
            implementation(projects.shared.featureGuides.ui)

            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.androidx.lifecycle.viewmodelCompose)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        commonTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.nonoxy.d2buildhelper.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}
