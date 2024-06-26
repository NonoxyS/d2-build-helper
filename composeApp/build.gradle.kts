import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.apollo)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_1_8}")
                }
            }
        }
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                debugImplementation(libs.androidx.testManifest)
                implementation(libs.androidx.junit4)
            }
        }
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.apollo.runtime)

            implementation(libs.compose.viewmodel)
            implementation(libs.compose.navigation)

            implementation(libs.supabase.storage)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.cio)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

    }
}

android {
    namespace = "dev.nonoxy.d2buildhelper"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "dev.nonoxy.d2buildhelper.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    //https://developer.android.com/studio/test/gradle-managed-devices
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices.devices {
            maybeCreate<ManagedVirtualDevice>("pixel5").apply {
                device = "Pixel 5"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        //enables a Compose tooling support in the AndroidStudio
        compose = true
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

buildConfig {
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())

    val supabaseBaseUrl = "https://ojxuhaplumzopsbihjkf.supabase.co"
    val supabaseApiKey = localProperties.getProperty("SUPABASE_API_KEY")
    val stratzBaseUrl = "https://api.stratz.com/graphql"
    val stratzApiKey = localProperties.getProperty("STRATZ_API_KEY")

    require(supabaseApiKey.isNotBlank()) {
        "Register your api key from supabase.com and place it in local.properties as `SUPABASE_API_KEY`" +
                "and configure Storage and Database"
    }

    require(stratzApiKey.isNotBlank()) {
        "Register your api key from stratz.com/api and place it in local.properties as `STRATZ_API_KEY`"
    }

    buildConfigField(
        "String",
        "STORAGE_HERO_ICONS_FOLDER_URL",
        "\"$supabaseBaseUrl/storage/v1/object/public/d2bh_images/hero_icons/\""
    )
    buildConfigField(
        "String",
        "STORAGE_ITEM_ICONS_FOLDER_URL",
        "\"$supabaseBaseUrl/storage/v1/object/public/d2bh_images/item_icons/\""
    )
    buildConfigField(
        "String",
        "STORAGE_ABILITY_ICONS_FOLDER_URL",
        "\"$supabaseBaseUrl/storage/v1/object/public/d2bh_images/ability_icons/\""
    )
    buildConfigField(
        "String",
        "STORAGE_ADDITIONAL_ICONS_FOLDER_URL",
        "\"$supabaseBaseUrl/storage/v1/object/public/d2bh_images/additional_icons/\""
    )
    buildConfigField("String", "SUPABASE_BASE_URL", "\"$supabaseBaseUrl\"")
    buildConfigField("String", "SUPABASE_API_KEY", "\"$supabaseApiKey\"")
    buildConfigField("String", "API_BASE_URL", "\"$stratzBaseUrl\"")
    buildConfigField("String", "STRATZ_API_KEY", "\"$stratzApiKey\"")
}

apollo {
    service("api") {
        // GraphQL configuration here.
        // https://www.apollographql.com/docs/kotlin/advanced/plugin-configuration/
        packageName.set("dev.nonoxy.d2buildhelper.graphql")
    }
}
