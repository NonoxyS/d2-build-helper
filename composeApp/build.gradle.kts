import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import utils.AppVersion
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.apollo)
    id("json-serialization")
}

kotlin {
    jvmToolchain(17)

    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.apollo.runtime)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.compose.navigation)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.supabase.storage)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "dev.nonoxy.d2buildhelper"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        applicationId = "dev.nonoxy.d2buildhelper.androidApp"
        versionCode = AppVersion.getVersionCode(project).get()
        versionName = AppVersion.getVersionName(project).get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        // enables a Compose tooling support in the AndroidStudio
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
