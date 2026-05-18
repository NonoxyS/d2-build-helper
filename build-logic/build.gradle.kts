plugins {
    `kotlin-dsl`
}

dependencies {
    // Workaround for version catalog working inside precompiled scripts
    // Issue: https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    compileOnly(libs.gradleplugin.kotlin)
    compileOnly(libs.gradleplugin.android)
    compileOnly(libs.gradleplugin.composeCompiler)
    compileOnly(libs.gradleplugin.composeMultiplatform)
}

gradlePlugin {
    plugins {
        register("JsonSerialization") {
            id = "json-serialization"
            implementationClass = "plugins.JsonSerializationPlugin"
        }
        register("KmpLibrary") {
            id = "kmp-library"
            implementationClass = "plugins.KmpLibraryPlugin"
        }
        register("ComposeMultiplatformSetup") {
            id = "compose-multiplatform-setup"
            implementationClass = "plugins.ComposeMultiplatformSetupPlugin"
        }
        register("AndroidApplicationSetup") {
            id = "android-application-setup"
            implementationClass = "plugins.AndroidApplicationSetupPlugin"
        }
    }
}
