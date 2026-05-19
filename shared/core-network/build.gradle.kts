import extensions.androidLibraryConfig
import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.iosMainDependencies
import java.util.Properties

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.apollo)
    alias(libs.plugins.buildConfig)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.network"
}

commonMainDependencies {
    api(libs.apollo.runtime)
}

androidMainDependencies {
    implementation(libs.ktor.client.okhttp)
}

iosMainDependencies {
    implementation(libs.ktor.client.darwin)
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

    buildConfigField("String", "API_BASE_URL", "\"$stratzBaseUrl\"")
    buildConfigField("String", "STRATZ_API_KEY", "\"$stratzApiKey\"")
}
