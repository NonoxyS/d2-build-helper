import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.iosMainDependencies
import java.util.Properties

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.conventionPlugin.jsonSerialization)
    alias(libs.plugins.apollo)
    alias(libs.plugins.buildConfig)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.network"
}

commonMainDependencies {
    api(libs.apollo.runtime)
    api(libs.ktor.core)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.serializationJson)
    implementation(projects.shared.common)
    implementation(projects.shared.coreDomain)
}

androidMainDependencies {
    api(libs.ktor.engine.okhttp)
}

iosMainDependencies {
    api(libs.ktor.engine.darwin)
}

apollo {
    service("api") {
        packageName.set("dev.nonoxy.d2buildhelper.graphql")
    }
}

buildConfig {
    packageName = "dev.nonoxy.d2buildhelper.core.network"
    useKotlinOutput { internalVisibility = false }

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    val stratzBaseUrl = "https://api.stratz.com/graphql"
    val stratzApiKey = localProperties.getProperty("STRATZ_API_KEY")
        ?: error("Register your api key from stratz.com/api and place it in local.properties as `STRATZ_API_KEY`")
    require(stratzApiKey.isNotBlank()) { "STRATZ_API_KEY in local.properties is blank" }

    val d2bhApiKey = localProperties.getProperty("D2BH_API_KEY")
        ?: error("Register a d2bh-backend API key and place it in local.properties as `D2BH_API_KEY`")
    require(d2bhApiKey.isNotBlank()) { "D2BH_API_KEY in local.properties is blank" }
    val d2bhEnvironment = localProperties.getProperty("D2BH_ENVIRONMENT") ?: "prod"

    buildConfigField("String", "API_BASE_URL", "\"$stratzBaseUrl\"")
    buildConfigField("String", "STRATZ_API_KEY", "\"$stratzApiKey\"")
    buildConfigField("String", "D2BH_API_KEY", "\"$d2bhApiKey\"")
    buildConfigField("String", "D2BH_ENVIRONMENT", "\"$d2bhEnvironment\"")
}
