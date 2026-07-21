rootProject.name = "Dota-2-Build-Helper"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.google.com")
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven("https://maven.google.com")
    }
}

includeBuild("build-logic")

// Android specific modules
include(":android:app")

// Shared common and core modules
include(":shared:main")

include(":shared:common")
include(":shared:common-ui")
include(":shared:common-resources")

include(":shared:core-domain")
include(":shared:core-match:domain")
include(":shared:core-match:presentation")
include(":shared:core-mvikotlin")
include(":shared:core-presentation")
include(":shared:core-navigation")
include(":shared:core-network")
include(":shared:core-storage")
include(":shared:core-resources")

// Shared feature modules
include(":shared:feature-guides:api")
include(":shared:feature-guides:impl")
include(":shared:feature-guides:presentation")
include(":shared:feature-guides:ui")

include(":shared:feature-guide-details:api")
include(":shared:feature-guide-details:impl")
include(":shared:feature-guide-details:presentation")
include(":shared:feature-guide-details:ui")

if (System.getenv("IS_CI") == null) {
    providers.exec {
        commandLine("git", "config", "core.hooksPath", ".githooks")
        workingDir = rootDir
    }.result.get()
}
