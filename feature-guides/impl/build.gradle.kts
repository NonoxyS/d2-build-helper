plugins {
    id("kmp-feature-setup")
    id("json-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.coreNetwork)
            implementation(projects.coreResources)
        }
    }
}
