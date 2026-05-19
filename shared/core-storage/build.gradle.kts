import extensions.androidLibraryConfig
import java.util.Properties

plugins {
    alias(libs.plugins.conventionPlugin.kmpLibrary)
    alias(libs.plugins.buildConfig)
}

androidLibraryConfig {
    namespace = "dev.nonoxy.d2buildhelper.core.storage"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.supabase.storage)
        }
    }
}

buildConfig {
    packageName = "dev.nonoxy.d2buildhelper.core.storage"
    useKotlinOutput { internalVisibility = false }

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    val supabaseBaseUrl = "https://ojxuhaplumzopsbihjkf.supabase.co"
    val supabaseApiKey = localProperties.getProperty("SUPABASE_API_KEY")
        ?: error("Register your api key from supabase.com and place it in local.properties as `SUPABASE_API_KEY`")

    require(supabaseApiKey.isNotBlank()) { "SUPABASE_API_KEY in local.properties is blank" }

    buildConfigField("String", "SUPABASE_BASE_URL", "\"$supabaseBaseUrl\"")
    buildConfigField("String", "SUPABASE_API_KEY", "\"$supabaseApiKey\"")
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
}
