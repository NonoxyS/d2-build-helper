import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.apis
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.implementations
import extensions.iosMainDependencies
import java.util.Properties

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
    alias(libs.plugins.buildConfig)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.network"
}

commonMainDependencies {
    apis(libs.ktor.core)
    implementations(
        libs.ktor.logging,
        libs.ktor.contentNegotiation,
        libs.ktor.serializationJson,
        projects.shared.common,
        projects.shared.coreDomain,
    )
}

androidMainDependencies {
    apis(libs.ktor.engine.okhttp)
}

iosMainDependencies {
    apis(libs.ktor.engine.darwin)
}

commonTestDependencies {
    implementations(kotlin("test"))
}

buildConfig {
    packageName = "dev.nonoxy.d2buildhelper.core.network"
    useKotlinOutput { internalVisibility = false }

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    val d2bhApiKey = localProperties.getProperty("D2BH_API_KEY")
        ?: error("Register a d2bh-backend API key and place it in local.properties as `D2BH_API_KEY`")
    require(d2bhApiKey.isNotBlank()) { "D2BH_API_KEY in local.properties is blank" }

    buildConfigField("String", "D2BH_API_KEY", "\"$d2bhApiKey\"")
}

// AGP 9 lint host-test tasks read generated BuildConfig outputs without declaring the dependency.
tasks.matching { it.name == "lintAnalyzeAndroidHostTest" || it.name == "generateAndroidHostTestLintModel" }
    .configureEach {
        dependsOn("generateAndroidHostTestBuildConfig", "generateTestBuildConfig")
    }
