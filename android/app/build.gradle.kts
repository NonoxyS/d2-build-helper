import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import extensions.implementations
import utils.AppVersion
import java.util.Locale

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xexpect-actual-classes",
        )
    }
}

android {
    namespace = "dev.nonoxy.d2buildhelper"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.nonoxy.d2buildhelper"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = AppVersion.getVersionCode(project).get()
        versionName = AppVersion.getVersionName(project).get()
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        // TODO: Create release keystore and uncomment this
        // register("release").configure {
        //     file("$rootDir/signing.properties").let { file ->
        //         if (!file.canRead()) error("signing.properties file read error")
        //
        //         val properties = Properties().apply {
        //             file.inputStream().use { stream -> load(stream) }
        //         }
        //
        //         storeFile = file("$rootDir/keystores/release.keystore.jks")
        //         storePassword = properties.getProperty("keystorePassword")
        //         keyAlias = properties.getProperty("keyAlias")
        //         keyPassword = properties.getProperty("keyPassword")
        //     }
        // }

        named("debug").configure {
            val DEBUG_STORE_PASSWORD: String by project
            val DEBUG_KEY_ALIAS: String by project

            storeFile = file("$rootDir/keystores/debug.keystore.jks")
            storePassword = DEBUG_STORE_PASSWORD
            keyAlias = DEBUG_KEY_ALIAS
            keyPassword = DEBUG_STORE_PASSWORD
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // TODO: After create release signing config change "debug" -> "release"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        register("dev") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-DEV"
            dimension = "environment"
        }

        register("prod") {
            dimension = "environment"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
}

dependencies {
    implementations(
        projects.shared.main,
        projects.shared.commonUi,

        libs.koin.android,
        libs.napier,
        libs.androidx.activity.compose,
        libs.compose.multiplatform.uiToolingPreview
    )

    debugImplementation(libs.compose.multiplatform.uiTooling)
}

androidComponents {
    onVariants(selector().all()) { variant ->
        val flavorName = variant.flavorName.orEmpty().capitalize()
        val buildTypeName = variant.buildType.orEmpty().capitalize()
        val versionName = AppVersion.getVersionName(project).get()

        // appDevRelease-1.0.0 -> app-dev-release-1.0.0
        val appName = "${project.name}-$flavorName-$buildTypeName-$versionName"
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase(Locale.getDefault())

        val outputAABTaskName = "sign${variant.name.capitalize()}Bundle"

        afterEvaluate {
            tasks.named<FinalizeBundleTask>(outputAABTaskName) {
                val parentFile = finalBundleFile.asFile.get().parentFile
                val finalFile = File(parentFile, "$appName.aab")
                finalBundleFile.set(finalFile)
            }
        }

        // Rename APK
        variant.outputs.forEach { output ->
            if (output is VariantOutputImpl) {
                output.outputFileName.set("$appName.apk")
            }
        }
    }
}
