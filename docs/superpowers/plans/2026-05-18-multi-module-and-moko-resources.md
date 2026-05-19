# Multi-Module Split + moko-resources Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate the single `:composeApp` module to a 15-module structure (KMMTemplate playbook) and consolidate all resources into `:common-resources` using moko-resources.

**Architecture:** Five phases, one commit per phase. Each phase ends green: Android assemble + JVM tests + iOS simulator tests + detekt + Android lint. Phase 0 sets up build-logic. Phase 1 creates 9 shared modules. Phase 2 splits Guides feature into 4 sub-modules. Phase 3 isolates `com.android.application` into `:androidApp`. Phase 4 updates docs/CI/baseline.

**Tech Stack:** Kotlin 2.3.10, AGP 9.0.0, Compose Multiplatform 1.10.1, MVIKotlin 4.4.0, Koin 4.1.x, Apollo Kotlin 4.3.1, Supabase 3.1.0, Ktor 3.3.3, Napier 2.7.1, moko-mvvm 0.16.1, moko-resources (new — 0.24+ compatible with CMP 1.10).

**Spec:** `docs/superpowers/specs/2026-05-18-multi-module-and-moko-resources-design.md` — review before starting.

---

## Module Inventory

The final state has 15 modules. New modules created in this plan:

| Module | Phase | Notes |
|---|---|---|
| `:common` | 1 | Pure Kotlin/coroutines helpers |
| `:common-ui` | 1 | LocalImageLoader |
| `:common-resources` | 1 | moko-resources only; no Kotlin code |
| `:core-domain` | 1 | Hero, Item, Ability, ImageResources |
| `:core-navigation` | 1 | AppScreens + LocalNavHost |
| `:core-network` | 1 | Apollo + Ktor + buildConfig (Stratz) |
| `:core-presentation` | 1 | BaseViewModel, BaseExecutor, MVIKotlin Koin module |
| `:core-storage` | 1 | Supabase + buildConfig (Supabase) |
| `:core-resources` | 1 | ResourcesRepository + datasources |
| `:feature-guides:api` | 2 | GuidesStore contract + Guide model |
| `:feature-guides:impl` | 2 | Store factory/executor/reducer + repository |
| `:feature-guides:presentation` | 2 | GuidesViewModel + UI models + mappers |
| `:feature-guides:ui` | 2 | GuidesScreen + sub-views |
| `:androidApp` | 3 | com.android.application — Android entry only |
| `:composeApp` | existing | After phase 3: KMP shell (no `com.android.application`) |

---

## File Structure Overview

Each module has the conventional KMP layout:

```
<module>/
├── build.gradle.kts
└── src/
    ├── commonMain/kotlin/...
    ├── commonTest/kotlin/...
    ├── androidMain/kotlin/...   (where actuals or Android-specific deps live)
    ├── jvmMain/kotlin/...        (where JVM actuals live)
    └── iosMain/kotlin/...        (where iOS actuals live)
```

Convention plugin files (Phase 0):

```
build-logic/src/main/kotlin/plugins/
├── KmpLibraryPlugin.kt
├── ComposeMultiplatformSetupPlugin.kt
└── AndroidApplicationSetupPlugin.kt
```

---

## Phase 0 — build-logic Convention Plugins

**Goal:** Register three convention plugins and add moko-resources to the version catalog. No module structure changes; `:composeApp` builds unchanged.

### Task 0.1: Add moko-resources to version catalog

**Files:**
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Add moko-resources version**

Insert in the `[versions]` block, after the `moko-mvvm = "0.16.1"` line:

```toml
moko-resources = "0.24.5"
```

(0.24.5 is the latest stable supporting CMP 1.10. If a newer version supporting CMP 1.10.1 + Kotlin 2.3.10 exists when executing, prefer the latest patch on 0.24.x or the next major that explicitly lists CMP 1.10 support.)

- [ ] **Step 2: Add moko-resources libraries**

Insert in the `[libraries]` block after the `moko-mvvm-flow` line:

```toml
moko-resources = { module = "dev.icerock.moko:resources", version.ref = "moko-resources" }
moko-resources-compose = { module = "dev.icerock.moko:resources-compose", version.ref = "moko-resources" }
```

- [ ] **Step 3: Add moko-resources gradle plugin**

Insert in the `[plugins]` block after the `apollo = ...` line:

```toml
moko-resources = { id = "dev.icerock.mobile.multiplatform-resources", version.ref = "moko-resources" }
```

- [ ] **Step 4: Verify catalog parses**

Run: `./gradlew help`
Expected: BUILD SUCCESSFUL, no catalog errors.

- [ ] **Step 5: Stage only — do not commit yet (combined with 0.2/0.3/0.4)**

```bash
git add gradle/libs.versions.toml
```

### Task 0.2: Create `kmp-library` convention plugin

**Files:**
- Create: `build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt`
- Modify: `build-logic/build.gradle.kts`

- [ ] **Step 1: Create plugin source file**

Path: `build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt`

```kotlin
package plugins

import com.android.build.gradle.LibraryExtension
import extensions.androidConfig
import extensions.commonMainDependencies
import extensions.commonTestDependencies
import extensions.kotlinMultiplatformConfig
import extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.multiplatform.get().pluginId)
                apply(libs.plugins.android.library.get().pluginId)
            }

            kotlinMultiplatformConfig {
                jvmToolchain(JAVA_VERSION)

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

                listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { ios ->
                    ios.binaries.framework {
                        baseName = project.name.replace("-", "_").replace(":", "_")
                        isStatic = true
                    }
                }
            }

            commonMainDependencies {
                implementation(libs.kotlinx.coroutines.core)
            }

            commonTestDependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }

            androidConfig {
                namespace = derivedNamespace(target)
                compileSdk = libs.versions.android.compileSdk.get().toInt()
                defaultConfig {
                    minSdk = libs.versions.android.minSdk.get().toInt()
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }

    private fun derivedNamespace(project: Project): String {
        val segments = project.path
            .removePrefix(":")
            .split(":", "-")
            .map { it.replace(Regex("[^A-Za-z0-9]"), "") }
            .filter { it.isNotEmpty() }
        return (listOf("dev", "nonoxy", "d2buildhelper") + segments).joinToString(".")
    }

    companion object {
        private const val JAVA_VERSION = 17
    }
}
```

- [ ] **Step 2: Add `android-library` plugin alias to version catalog**

Edit `gradle/libs.versions.toml` `[plugins]` block, add after `android-application`:

```toml
android-library = { id = "com.android.library", version.ref = "agp" }
```

- [ ] **Step 3: Register plugin in `build-logic/build.gradle.kts`**

Edit `build-logic/build.gradle.kts`, in the `gradlePlugin { plugins { ... } }` block, add:

```kotlin
register("KmpLibrary") {
    id = "kmp-library"
    implementationClass = "plugins.KmpLibraryPlugin"
}
```

- [ ] **Step 4: Compile build-logic to verify**

Run: `./gradlew :build-logic:compileKotlin`
Expected: BUILD SUCCESSFUL.

### Task 0.3: Create `compose-multiplatform-setup` convention plugin

**Files:**
- Create: `build-logic/src/main/kotlin/plugins/ComposeMultiplatformSetupPlugin.kt`
- Modify: `build-logic/build.gradle.kts`

- [ ] **Step 1: Create plugin source file**

Path: `build-logic/src/main/kotlin/plugins/ComposeMultiplatformSetupPlugin.kt`

```kotlin
package plugins

import extensions.androidMainDependencies
import extensions.commonMainDependencies
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeMultiplatformSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.compose.get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
            }

            commonMainDependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui)
            }

            androidMainDependencies {
                implementation(libs.compose.ui.tooling)
            }
        }
    }
}
```

- [ ] **Step 2: Register plugin in `build-logic/build.gradle.kts`**

In `gradlePlugin { plugins { ... } }` add:

```kotlin
register("ComposeMultiplatformSetup") {
    id = "compose-multiplatform-setup"
    implementationClass = "plugins.ComposeMultiplatformSetupPlugin"
}
```

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :build-logic:compileKotlin`
Expected: BUILD SUCCESSFUL.

### Task 0.4: Create `android-application-setup` convention plugin

**Files:**
- Create: `build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt`
- Modify: `build-logic/build.gradle.kts`

- [ ] **Step 1: Create plugin source file**

Path: `build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt`

```kotlin
package plugins

import extensions.androidAppConfig
import extensions.androidKotlinConfig
import extensions.kotlinJvmCompilerOptions
import extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class AndroidApplicationSetupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.android.application.get().pluginId)
                apply("org.jetbrains.kotlin.android")
                apply(libs.plugins.compose.get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
            }

            androidAppConfig {
                compileSdk = libs.versions.android.compileSdk.get().toInt()

                defaultConfig {
                    minSdk = libs.versions.android.minSdk.get().toInt()
                    targetSdk = libs.versions.android.targetSdk.get().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    compose = true
                }
            }

            androidKotlinConfig {
                jvmToolchain(17)
            }

            kotlinJvmCompilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }
}
```

- [ ] **Step 2: Register plugin in `build-logic/build.gradle.kts`**

In `gradlePlugin { plugins { ... } }` add:

```kotlin
register("AndroidApplicationSetup") {
    id = "android-application-setup"
    implementationClass = "plugins.AndroidApplicationSetupPlugin"
}
```

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :build-logic:compileKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Verify root build still works**

Run: `./gradlew help`
Expected: BUILD SUCCESSFUL — convention plugins registered but no module applies them yet.

### Task 0.5: Commit Phase 0

- [ ] **Step 1: Stage files**

```bash
git add gradle/libs.versions.toml \
        build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt \
        build-logic/src/main/kotlin/plugins/ComposeMultiplatformSetupPlugin.kt \
        build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt \
        build-logic/build.gradle.kts
```

- [ ] **Step 2: Commit**

```bash
git commit -m "$(cat <<'EOF'
build: register kmp-library, compose-multiplatform-setup, android-application-setup convention plugins

Adds moko-resources to libs catalog. Convention plugins centralise KMP/Android
setup ahead of the multi-module split; no module applies them yet, so
:composeApp still builds as before.
EOF
)"
```

- [ ] **Step 3: Verify full build still passes**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

---

## Phase 1 — Shared Modules + moko-resources

**Goal:** Create 9 new shared modules (`:common`, `:common-ui`, `:common-resources`, `:core-domain`, `:core-navigation`, `:core-network`, `:core-presentation`, `:core-storage`, `:core-resources`) and migrate corresponding code out of `:composeApp`. Feature Guides code stays in `:composeApp` for this phase but imports rewrite to point at new modules. moko-resources lives in `:common-resources`.

**Verification at end of phase:** `./gradlew detekt :composeApp:assembleDebug :composeApp:jvmTest :composeApp:lintDebug :composeApp:iosSimulatorArm64Test`

### Task 1.1: Create `:common` module

**Files:**
- Create: `common/build.gradle.kts`
- Move from `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/*` to `common/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/*`
- Modify: `settings.gradle.kts`

- [ ] **Step 1: Add module include**

In `settings.gradle.kts`, after `include(":composeApp")`, add:

```kotlin
include(":common")
```

- [ ] **Step 2: Create `common/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
}
```

That's it — `kmp-library` brings everything needed. `:common` has no further deps.

- [ ] **Step 3: Move source files**

```bash
mkdir -p common/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common common/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/
```

Now `common/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/` contains: `coroutines/`, `extensions/`, `mappers/`, `utils/`.

- [ ] **Step 4: Add `:common` dependency in `:composeApp`**

Edit `composeApp/build.gradle.kts`, in `commonMain.dependencies { ... }`, add:

```kotlin
implementation(projects.common)
```

(Typesafe project accessor — already enabled in `settings.gradle.kts` via `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`.)

- [ ] **Step 5: Verify build**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL. (All `dev.nonoxy.d2buildhelper.common.*` imports resolve via the new module.)

- [ ] **Step 6: Stage but don't commit yet (combined with later phase-1 tasks)**

```bash
git add settings.gradle.kts common/ composeApp/build.gradle.kts
```

### Task 1.2: Create `:core-domain` module

**Files:**
- Create: `core-domain/build.gradle.kts`
- Move 4 files from `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/` to `core-domain/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/domain/`
- Modify: `settings.gradle.kts`, `composeApp/build.gradle.kts`

- [ ] **Step 1: Add include**

In `settings.gradle.kts`:

```kotlin
include(":core-domain")
```

- [ ] **Step 2: Create `core-domain/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
}
```

- [ ] **Step 3: Move and rename packages**

The following files are app-wide domain models, moved out of `features/guides/domain/models/` to a new home in `:core-domain`. **Note:** `Guide.kt` stays in features/guides (it's feature-specific) and moves later in Phase 2.

Move `Hero.kt`, `Item.kt`, `Ability.kt`, `ImageResources.kt`:

```bash
mkdir -p core-domain/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/domain
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/Hero.kt \
        composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/Item.kt \
        composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/Ability.kt \
        composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/ImageResources.kt \
        core-domain/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/domain/
```

- [ ] **Step 4: Rewrite `package` declarations in moved files**

Each of the four files: change `package dev.nonoxy.d2buildhelper.features.guides.domain.models` to `package dev.nonoxy.d2buildhelper.core.domain`.

Use sed (one-shot — verify with git diff after):

```bash
for f in core-domain/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/domain/{Hero,Item,Ability,ImageResources}.kt; do
  sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models$|package dev.nonoxy.d2buildhelper.core.domain|' "$f"
done
```

- [ ] **Step 5: Rewrite imports across the project**

Find all files importing the old model paths and update them.

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models\.\(Hero\|Item\|Ability\|ImageResources\)" composeApp/ | while read f; do
  sed -i '' 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models\.\(Hero\|Item\|Ability\|ImageResources\)|dev.nonoxy.d2buildhelper.core.domain.\1|g' "$f"
done
```

Verify with: `grep -rn "features.guides.domain.models" composeApp/` — should show only `Guide.kt` references remaining (other than the package declaration of Guide.kt itself, which stays).

- [ ] **Step 6: Add dependency in `:composeApp`**

```kotlin
implementation(projects.coreDomain)
```

- [ ] **Step 7: Verify build**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 8: Stage**

```bash
git add settings.gradle.kts core-domain/ composeApp/
```

### Task 1.3: Create `:common-resources` module (moko-resources)

**Files:**
- Create: `common-resources/build.gradle.kts`
- Create: `common-resources/src/commonMain/moko-resources/base/strings.xml`
- Create: `common-resources/src/commonMain/moko-resources/base/files/constant_heroes.json`, `constant_items.json`, `constant_abilities.json`
- Delete: `composeApp/src/commonMain/composeResources/`
- Modify: `settings.gradle.kts`

- [ ] **Step 1: Add include**

```kotlin
include(":common-resources")
```

- [ ] **Step 2: Create `common-resources/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    alias(libs.plugins.moko.resources)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.moko.resources)
            api(libs.moko.resources.compose)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("dev.nonoxy.d2buildhelper.common.resources")
    resourcesClassName.set("MR")
}
```

`api()` on moko libs so consumers (e.g., `:feature-guides:ui`) automatically get `stringResource`/`MR` without redeclaring.

- [ ] **Step 3: Move strings.xml**

```bash
mkdir -p common-resources/src/commonMain/moko-resources/base
git mv composeApp/src/commonMain/composeResources/values/strings.xml \
        common-resources/src/commonMain/moko-resources/base/strings.xml
```

Verify content (no edits needed — strings stay the same; moko reads the same Android-style XML format):

```xml
<resources>
    <string name="all_heroes">Все герои</string>
    <string name="hero_filter">Фильтр героев</string>
    <string name="app_name">D2 Build Helper</string>
    <string name="error_loading">Failed to load</string>
    <string name="retry">Retry</string>
</resources>
```

- [ ] **Step 4: Move and rename JSON files**

```bash
mkdir -p common-resources/src/commonMain/moko-resources/base/files
git mv composeApp/src/commonMain/composeResources/files/constants/heroes.json \
        common-resources/src/commonMain/moko-resources/base/files/constant_heroes.json
git mv composeApp/src/commonMain/composeResources/files/constants/items.json \
        common-resources/src/commonMain/moko-resources/base/files/constant_items.json
git mv composeApp/src/commonMain/composeResources/files/constants/abilities.json \
        common-resources/src/commonMain/moko-resources/base/files/constant_abilities.json
```

- [ ] **Step 5: Delete leftover composeResources directory**

```bash
git rm -r composeApp/src/commonMain/composeResources/
```

If the `font/` subdirectory contained fonts, decide their fate now. Check first:

```bash
find composeApp/src/commonMain/composeResources/font/ -type f 2>/dev/null
```

If fonts exist and are loaded by code, move them into `common-resources/src/commonMain/moko-resources/base/fonts/` and update the loading code in Phase 2 task that touches typography. If no fonts or unused, deletion is fine.

- [ ] **Step 6: Add dependency in `:composeApp`**

In `composeApp/build.gradle.kts` `commonMain.dependencies`:

```kotlin
implementation(projects.commonResources)
```

Also remove the line `implementation(libs.compose.resources)` from `:composeApp/build.gradle.kts` (no longer needed; moko-resources replaces it). And remove `implementation(libs.compose.ui.tooling.preview)` from `commonMain.dependencies` — Preview annotations live in androidMain only; verify by checking remaining `@Preview` usages: `grep -rn "@Preview" composeApp/src/`.

If `@Preview` survives in commonMain code that moves to `:feature-guides:ui` later, the import switches to a moko-compatible alternative or stays as the Compose Multiplatform tooling preview in `:feature-guides:ui` androidMain.

- [ ] **Step 7: Rewrite Res.string usage to MR.strings — Guides UI**

In each of these files inside `:composeApp` Guides UI code, replace the import block and reference:

`composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/HeroFilterDialogView.kt`:
- Remove: `import dota_2_build_helper.composeapp.generated.resources.Res`
- Remove: `import dota_2_build_helper.composeapp.generated.resources.hero_filter`
- Remove: `import org.jetbrains.compose.resources.stringResource`
- Add: `import dev.icerock.moko.resources.compose.stringResource`
- Add: `import dev.nonoxy.d2buildhelper.common.resources.MR`
- Replace: `stringResource(Res.string.hero_filter)` → `stringResource(MR.strings.hero_filter)`

`composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/GuidesErrorView.kt`:
- Same import swap
- Replace: `Res.string.error_loading` → `MR.strings.error_loading`
- Replace: `Res.string.retry` → `MR.strings.retry`

`composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/GuidesTopBarView.kt`:
- Same import swap
- Replace: `Res.string.all_heroes` → `MR.strings.all_heroes`

- [ ] **Step 8: Rewrite Res.string usage to MR.strings — jvmMain**

`composeApp/src/jvmMain/kotlin/main.kt`:
- Remove: `import dota_2_build_helper.composeapp.generated.resources.Res`
- Remove: `import dota_2_build_helper.composeapp.generated.resources.app_name`
- Remove: `import org.jetbrains.compose.resources.stringResource`
- Add: `import dev.icerock.moko.resources.compose.stringResource`
- Add: `import dev.nonoxy.d2buildhelper.common.resources.MR`
- Replace: `stringResource(Res.string.app_name)` → `stringResource(MR.strings.app_name)`

- [ ] **Step 9: Move ConstantResourcesDataSource readBytes call**

The constant data source still lives in `:composeApp` after this task (moves in Task 1.9 to `:core-resources`). Update it now to use moko since the JSON files have moved:

`composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/ConstantResourcesDataSource.kt`:

Replace `Res.readBytes("files/constants/heroes.json")` with `MR.files.constant_heroes.readText().encodeToByteArray()`. Similarly for items and abilities. Add imports:

```kotlin
import dev.nonoxy.d2buildhelper.common.resources.MR
```

Remove the import of `dota_2_build_helper.composeapp.generated.resources.Res`.

Note: moko 0.24 `FileResource.readText()` is a suspend extension that uses the platform's default file loading. Since `ConstantResourcesDataSource` already runs on `Dispatchers.IO`, this is a drop-in replacement. Verify by running unit tests after Task 1.9.

- [ ] **Step 10: Verify build**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

If moko-resources plugin reports missing iOS framework export, add to `common-resources/build.gradle.kts` inside `kotlin { ... }`:

```kotlin
targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
    binaries.framework {
        export(libs.moko.resources)
    }
}
```

- [ ] **Step 11: Stage**

```bash
git add settings.gradle.kts common-resources/ composeApp/
```

### Task 1.4: Create `:core-presentation` module

**Files:**
- Create: `core-presentation/build.gradle.kts`
- Move: `composeApp/.../core/mvikotlin/BaseExecutor.kt`, `composeApp/.../core/mvikotlin/di/CoreMVIKotlinModule.kt`, `composeApp/.../core/presentation/viewmodel/BaseViewModel.kt`, `composeApp/.../core/presentation/viewmodel/BaseIosViewModel.kt`

- [ ] **Step 1: Add include**

```kotlin
include(":core-presentation")
```

- [ ] **Step 2: Create `core-presentation/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.mvikotlin.core)
            api(libs.mvikotlin.main)
            api(libs.mvikotlin.logging)
            api(libs.mvikotlin.coroutines)
            api(libs.moko.mvvm.flow)
            api(libs.napier)
            api(libs.koin.core)
            implementation(projects.common)
        }
    }
}
```

The MVIKotlin and moko-mvvm types appear in `BaseViewModel` public API, so they need `api()` to leak through.

- [ ] **Step 3: Move files**

```bash
mkdir -p core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/{mvikotlin,viewmodel,di}
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/mvikotlin/BaseExecutor.kt \
        core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/mvikotlin/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/mvikotlin/di/CoreMVIKotlinModule.kt \
        core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/di/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/BaseViewModel.kt \
        core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/BaseIosViewModel.kt \
        core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/
```

- [ ] **Step 4: Rewrite package declarations**

`BaseExecutor.kt`: `package dev.nonoxy.d2buildhelper.core.mvikotlin` → `package dev.nonoxy.d2buildhelper.core.presentation.mvikotlin`
`CoreMVIKotlinModule.kt`: `package dev.nonoxy.d2buildhelper.core.mvikotlin.di` → `package dev.nonoxy.d2buildhelper.core.presentation.di`
`BaseViewModel.kt` and `BaseIosViewModel.kt`: package stays `dev.nonoxy.d2buildhelper.core.presentation.viewmodel`.

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.mvikotlin$|package dev.nonoxy.d2buildhelper.core.presentation.mvikotlin|' core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/mvikotlin/BaseExecutor.kt
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.mvikotlin\.di$|package dev.nonoxy.d2buildhelper.core.presentation.di|' core-presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/di/CoreMVIKotlinModule.kt
```

- [ ] **Step 5: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.core\.mvikotlin" composeApp/ | while read f; do
  sed -i '' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.mvikotlin\.BaseExecutor|dev.nonoxy.d2buildhelper.core.presentation.mvikotlin.BaseExecutor|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.mvikotlin\.di\.CoreMVIKotlinModule|dev.nonoxy.d2buildhelper.core.presentation.di.CoreMVIKotlinModule|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.mvikotlin\.di\.coreMVIKotlinModule|dev.nonoxy.d2buildhelper.core.presentation.di.coreMVIKotlinModule|g' \
    "$f"
done
```

(If the Koin module function is named differently than `coreMVIKotlinModule`, adjust the sed accordingly. Check the file content first.)

- [ ] **Step 6: Add dependency**

In `composeApp/build.gradle.kts`:

```kotlin
implementation(projects.corePresentation)
```

Remove from `:composeApp/build.gradle.kts` `commonMain.dependencies`: `mvikotlin.*`, `moko.mvvm.flow`, `napier` lines — they now transit through `:core-presentation`. Keep them only if a non-presentation file in `:composeApp` directly imports them.

- [ ] **Step 7: Verify**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 8: Stage**

```bash
git add settings.gradle.kts core-presentation/ composeApp/
```

### Task 1.5: Create `:core-navigation` module

**Files:**
- Create: `core-navigation/build.gradle.kts`
- Move: `composeApp/.../navigation/AppScreens.kt`, `LocalNavHost.kt`

- [ ] **Step 1: Add include**

```kotlin
include(":core-navigation")
```

- [ ] **Step 2: Create `core-navigation/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.navigation)
        }
    }
}
```

`api()` on compose-navigation so `NavHostController` types leak through to consumers.

- [ ] **Step 3: Move files**

```bash
mkdir -p core-navigation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/navigation
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/navigation/AppScreens.kt \
        core-navigation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/navigation/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/navigation/LocalNavHost.kt \
        core-navigation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/navigation/
```

- [ ] **Step 4: Rewrite package declarations**

Change `package dev.nonoxy.d2buildhelper.navigation` to `package dev.nonoxy.d2buildhelper.core.navigation` in both files.

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.navigation$|package dev.nonoxy.d2buildhelper.core.navigation|' \
    core-navigation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/navigation/AppScreens.kt \
    core-navigation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/navigation/LocalNavHost.kt
```

- [ ] **Step 5: Rewrite imports across project**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.navigation\." composeApp/ | while read f; do
  sed -i '' 's|dev\.nonoxy\.d2buildhelper\.navigation\.|dev.nonoxy.d2buildhelper.core.navigation.|g' "$f"
done
```

- [ ] **Step 6: Add dependency**

```kotlin
implementation(projects.coreNavigation)
```

Remove `implementation(libs.compose.navigation)` from `:composeApp/build.gradle.kts` `commonMain.dependencies` (transits through `:core-navigation`).

- [ ] **Step 7: Verify and stage**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

```bash
git add settings.gradle.kts core-navigation/ composeApp/
```

### Task 1.6: Create `:common-ui` module

**Files:**
- Create: `common-ui/build.gradle.kts`
- Move: `composeApp/.../base/LocalImageLoader.kt`

- [ ] **Step 1: Add include**

```kotlin
include(":common-ui")
```

- [ ] **Step 2: Create `common-ui/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coil)
            api(libs.coil.network.ktor)
        }
    }
}
```

- [ ] **Step 3: Move file**

```bash
mkdir -p common-ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/ui
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/base/LocalImageLoader.kt \
        common-ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/ui/
rmdir composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/base 2>/dev/null
```

- [ ] **Step 4: Rewrite package declaration**

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.base$|package dev.nonoxy.d2buildhelper.common.ui|' \
    common-ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/ui/LocalImageLoader.kt
```

- [ ] **Step 5: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.base\.LocalImageLoader" composeApp/ | while read f; do
  sed -i '' 's|dev\.nonoxy\.d2buildhelper\.base\.LocalImageLoader|dev.nonoxy.d2buildhelper.common.ui.LocalImageLoader|g' "$f"
done
```

- [ ] **Step 6: Add dependency**

```kotlin
implementation(projects.commonUi)
```

Remove `implementation(libs.coil)` and `implementation(libs.coil.network.ktor)` from `:composeApp/build.gradle.kts` `commonMain.dependencies`.

- [ ] **Step 7: Verify and stage**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

```bash
git add settings.gradle.kts common-ui/ composeApp/
```

### Task 1.7: Create `:core-network` module (Apollo + Ktor + buildConfig)

**Files:**
- Create: `core-network/build.gradle.kts`
- Move: `composeApp/.../core/graphql/` Apollo schema + queries (already in `core/graphql/`, will move to module)
- Modify: `composeApp/build.gradle.kts` (remove Apollo plugin + part of buildConfig)

- [ ] **Step 1: Add include**

```kotlin
include(":core-network")
```

- [ ] **Step 2: Create `core-network/build.gradle.kts`**

```kotlin
import java.util.Properties

plugins {
    id("kmp-library")
    alias(libs.plugins.apollo)
    alias(libs.plugins.buildConfig)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.apollo.runtime)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

apollo {
    service("api") {
        packageName.set("dev.nonoxy.d2buildhelper.graphql")
    }
}

buildConfig {
    packageName = "dev.nonoxy.d2buildhelper.core.network"

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
```

- [ ] **Step 3: Move Apollo schema and GraphQL queries**

The Apollo plugin reads `*.graphqls` schema and `*.graphql` operations from `src/commonMain/graphql/`. Verify by inspecting current location:

```bash
find composeApp -name "*.graphqls" -o -name "*.graphql"
```

If they live under `composeApp/src/commonMain/graphql/`, move them:

```bash
mkdir -p core-network/src/commonMain/graphql/dev/nonoxy/d2buildhelper/graphql
git mv composeApp/src/commonMain/graphql/* core-network/src/commonMain/graphql/
```

Adjust subdirectory based on Apollo packageName layout.

- [ ] **Step 4: Move generated Apollo code? No — it regenerates per module**

Generated GraphQL Kotlin code is produced at build time. After plugin moves to `:core-network`, the generated package `dev.nonoxy.d2buildhelper.graphql` is produced there. Other modules import these classes by depending on `:core-network`.

- [ ] **Step 5: Remove Apollo + BuildConfig from `:composeApp/build.gradle.kts`**

In `composeApp/build.gradle.kts`:
- Remove from `plugins { ... }`: `alias(libs.plugins.apollo)`. **Keep** `alias(libs.plugins.buildConfig)` for now — `:composeApp` still reads Supabase keys until Task 1.8.
- From `buildConfig { ... }`, remove the Stratz fields (`API_BASE_URL`, `STRATZ_API_KEY`) and the `stratzApiKey` reading. Keep the Supabase fields.
- Remove the entire `apollo { service("api") { ... } }` block.
- Remove `implementation(libs.apollo.runtime)` from `commonMain.dependencies`.
- Remove `implementation(libs.ktor.client.okhttp)` from androidMain and jvmMain (now in `:core-network`).
- Remove `implementation(libs.ktor.client.darwin)` from iosMain.

- [ ] **Step 6: Add dependency**

In `:composeApp/build.gradle.kts`:

```kotlin
implementation(projects.coreNetwork)
```

- [ ] **Step 7: Rewrite BuildConfig imports for Stratz keys**

```bash
grep -rl "import dev\.nonoxy\.d2buildhelper\.BuildConfig" composeApp/src/ | while read f; do
  # Only rewrite if file references STRATZ_API_KEY or API_BASE_URL — for now, just add new import inline.
  echo "Inspect: $f"
done
```

Manually inspect files printed above. For Stratz-related usage, change:

```kotlin
import dev.nonoxy.d2buildhelper.BuildConfig
// ...
BuildConfig.STRATZ_API_KEY
BuildConfig.API_BASE_URL
```

to:

```kotlin
import dev.nonoxy.d2buildhelper.core.network.BuildConfig
// ...
BuildConfig.STRATZ_API_KEY
BuildConfig.API_BASE_URL
```

Likely files: `core/di/AppModule.kt` (Apollo client creation), `core/data/api/guides/GuidesApi.kt` if it reads from BuildConfig.

- [ ] **Step 8: Verify**

Run: `./gradlew :core-network:generateApolloSources :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL. Apollo generates classes in `:core-network`'s build dir; `:composeApp` consumes them through the project dependency.

- [ ] **Step 9: Stage**

```bash
git add settings.gradle.kts core-network/ composeApp/
```

### Task 1.8: Create `:core-storage` module (Supabase + buildConfig)

**Files:**
- Create: `core-storage/build.gradle.kts`
- Modify: `composeApp/build.gradle.kts` (remove Supabase BuildConfig fields)

- [ ] **Step 1: Add include**

```kotlin
include(":core-storage")
```

- [ ] **Step 2: Create `core-storage/build.gradle.kts`**

```kotlin
import java.util.Properties

plugins {
    id("kmp-library")
    alias(libs.plugins.buildConfig)
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
```

- [ ] **Step 3: Remove Supabase BuildConfig from `:composeApp`**

In `composeApp/build.gradle.kts`:
- Remove `alias(libs.plugins.buildConfig)` from `plugins { ... }`.
- Remove the entire `buildConfig { ... }` block.
- Remove `implementation(libs.supabase.storage)` from `commonMain.dependencies`.

- [ ] **Step 4: Add dependency**

```kotlin
implementation(projects.coreStorage)
```

- [ ] **Step 5: Rewrite BuildConfig imports for Supabase keys**

For every file importing `dev.nonoxy.d2buildhelper.BuildConfig` and referencing `SUPABASE_*` or `STORAGE_*_FOLDER_URL`, change the import to `dev.nonoxy.d2buildhelper.core.storage.BuildConfig`.

```bash
grep -rl "BuildConfig\.\(SUPABASE\|STORAGE\)" composeApp/src/ | while read f; do
  sed -i '' 's|import dev\.nonoxy\.d2buildhelper\.BuildConfig|import dev.nonoxy.d2buildhelper.core.storage.BuildConfig|g' "$f"
done
```

(Note: a file that uses **both** Stratz and Supabase BuildConfig keys will need fully-qualified references. Look at `AppModule.kt`. Inspect with `grep -rn "BuildConfig\." composeApp/src/` and rewrite manually if needed.)

- [ ] **Step 6: Verify**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 7: Stage**

```bash
git add settings.gradle.kts core-storage/ composeApp/
```

### Task 1.9: Create `:core-resources` module

**Files:**
- Create: `core-resources/build.gradle.kts`
- Move: `composeApp/.../core/data/repository/resources/ResourcesRepository.kt`, `ResourcesRepositoryImpl.kt`, `composeApp/.../core/data/api/resources/image/*`, `composeApp/.../core/data/local/resources/constants/*`

- [ ] **Step 1: Add include**

```kotlin
include(":core-resources")
```

- [ ] **Step 2: Create `core-resources/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    id("json-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)
            implementation(projects.common)
            implementation(projects.commonResources)
            implementation(projects.coreNetwork)
            implementation(projects.coreStorage)
            implementation(libs.koin.core)
        }
    }
}
```

- [ ] **Step 3: Move source files**

```bash
mkdir -p core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/{data/api/image,data/local/constants,data/repository}

# Repository
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/resources/ResourcesRepository.kt \
        composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/resources/ResourcesRepositoryImpl.kt \
        core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/repository/

# Image data
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/* \
        core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/api/image/

# Constants data
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/* \
        core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/local/constants/
```

(Note: `models/` subdirectories under `local/constants/` contain DTOs. They move along with the data sources.)

- [ ] **Step 4: Rewrite package declarations**

```bash
# Repository
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.repository\.resources$|package dev.nonoxy.d2buildhelper.core.resources.data.repository|' \
    core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/repository/*.kt

# Image API
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.api\.resources\.image$|package dev.nonoxy.d2buildhelper.core.resources.data.api.image|' \
    core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/api/image/*.kt

# Constants
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.local\.resources\.constants$|package dev.nonoxy.d2buildhelper.core.resources.data.local.constants|' \
    core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/local/constants/*.kt

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.local\.resources\.constants\.models$|package dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models|' \
    core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/local/constants/models/*.kt
```

- [ ] **Step 5: Add `coreResourcesModule()` Koin module**

Create new file: `core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/di/CoreResourcesModule.kt`

```kotlin
package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.api.image.ImageResourcesApi
import dev.nonoxy.d2buildhelper.core.resources.data.api.image.ImageResourcesDataSource
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.ConstantResourcesDataSource
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepositoryImpl
import org.koin.dsl.module

fun coreResourcesModule() = module {
    single { ImageResourcesDataSource(get()) }
    single<ImageResourcesApi> { ImageResourcesDataSource(get()) }
    single { ConstantResourcesDataSource(get()) }
    single<ResourcesRepository> { ResourcesRepositoryImpl(get(), get(), get()) }
}
```

Adjust constructor arity to match actual class signatures (verify by checking the files moved in Step 3). The exact bindings depend on which interface/impl the existing `AppModule.kt` declares — replicate that here.

- [ ] **Step 6: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.core\.data\.\(repository\.resources\|api\.resources\.image\|local\.resources\.constants\)" composeApp/ | while read f; do
  sed -i '' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.data\.repository\.resources\.|dev.nonoxy.d2buildhelper.core.resources.data.repository.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.data\.api\.resources\.image\.|dev.nonoxy.d2buildhelper.core.resources.data.api.image.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.data\.local\.resources\.constants\.|dev.nonoxy.d2buildhelper.core.resources.data.local.constants.|g' \
    "$f"
done
```

- [ ] **Step 7: Update `AppModule.kt`**

In `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt`:
- Remove all bindings for `ResourcesRepository`, `ResourcesRepositoryImpl`, `ImageResourcesDataSource`, `ImageResourcesApi`, `ConstantResourcesDataSource` from the module.
- Add include of the new module: `coreResourcesModule()` (it's a function that returns `Module`; you `+` it into the aggregated list or use Koin's `includes` DSL — match the pattern AppModule already uses).

Inspect AppModule first to choose the right wiring.

- [ ] **Step 8: Add dependency**

```kotlin
implementation(projects.coreResources)
```

Remove from `:composeApp/build.gradle.kts` any imports it no longer needs directly (e.g., `libs.supabase.storage` already removed; nothing else specific to resources data should remain).

- [ ] **Step 9: Verify**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 10: Stage**

```bash
git add settings.gradle.kts core-resources/ composeApp/
```

### Task 1.10: Phase 1 final verification + commit

- [ ] **Step 1: Detekt**

Run: `./gradlew detekt`
Expected: BUILD SUCCESSFUL. (If new modules raise violations not in baseline, regenerate baseline only at Phase 4. For now, accept failures only if they're "FinalNewline" or "UnstableCollections" related to moved files — these clear up in Phase 4 baseline regeneration. If unrelated violations appear, fix them.)

- [ ] **Step 2: Android lint**

Run: `./gradlew :composeApp:lintDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: JVM tests**

Run: `./gradlew :composeApp:jvmTest`
Expected: BUILD SUCCESSFUL, all tests pass.

- [ ] **Step 4: iOS simulator test**

Run: `./gradlew :composeApp:iosSimulatorArm64Test`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Run app on JVM**

Run: `./gradlew :composeApp:run`
Expected: App window opens, Guides screen loads heroes successfully (hits Stratz API), images load (hits Supabase). Close window.

- [ ] **Step 6: Commit Phase 1**

```bash
git commit -m "$(cat <<'EOF'
refactor: extract shared modules and migrate resources to moko-resources

Creates :common, :common-ui, :common-resources, :core-domain, :core-navigation,
:core-network, :core-presentation, :core-storage, :core-resources. Moves
corresponding code out of :composeApp. Strings and JSON constants migrate from
composeResources to :common-resources via moko-resources. Apollo and Supabase
buildConfig configuration relocate to :core-network and :core-storage. The
Guides feature code remains inside :composeApp for now and is split into
sub-modules in Phase 2.
EOF
)"
```

---

## Phase 2 — Guides Feature Split

**Goal:** Split `features/guides/` from `:composeApp` into 4 modules (`:feature-guides:api`, `:impl`, `:presentation`, `:ui`). `:composeApp` depends on all four.

**Verification:** Same as Phase 1 — detekt + assembleDebug + jvmTest + iOS simulator test + manual JVM run.

### Task 2.1: Create `:feature-guides:api` module

**Files:**
- Create: `feature-guides/api/build.gradle.kts`
- Move: `composeApp/.../features/guides/api/store/GuidesStore.kt`, `composeApp/.../features/guides/domain/models/Guide.kt`

- [ ] **Step 1: Add include**

In `settings.gradle.kts`:

```kotlin
include(":feature-guides:api")
```

- [ ] **Step 2: Create `feature-guides/api/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)
            api(libs.mvikotlin.core)
        }
    }
}
```

`api(mvikotlin.core)` because `GuidesStore` extends `Store<Intent, State, Label>` — that interface is part of the public contract.

- [ ] **Step 3: Move files**

```bash
mkdir -p feature-guides/api/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/api/{store,domain}
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/api/store/GuidesStore.kt \
        feature-guides/api/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/api/store/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/Guide.kt \
        feature-guides/api/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/api/domain/
```

- [ ] **Step 4: Rewrite package declarations**

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.api\.store$|package dev.nonoxy.d2buildhelper.feature.guides.api.store|' \
    feature-guides/api/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/api/store/GuidesStore.kt

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models$|package dev.nonoxy.d2buildhelper.feature.guides.api.domain|' \
    feature-guides/api/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/api/domain/Guide.kt
```

- [ ] **Step 5: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.features\.guides\.api\.store" composeApp/ | while read f; do
  sed -i '' 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.api\.store|dev.nonoxy.d2buildhelper.feature.guides.api.store|g' "$f"
done

grep -rl "dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models\.Guide" composeApp/ | while read f; do
  sed -i '' 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.domain\.models\.Guide|dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide|g' "$f"
done
```

- [ ] **Step 6: Add dependency in `:composeApp`**

```kotlin
implementation(projects.featureGuides.api)
```

- [ ] **Step 7: Verify and stage**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

```bash
git add settings.gradle.kts feature-guides/api/ composeApp/
```

### Task 2.2: Create `:feature-guides:impl` module

**Files:**
- Create: `feature-guides/impl/build.gradle.kts`
- Move: `composeApp/.../features/guides/impl/*`, `composeApp/.../core/data/api/guides/*`, `composeApp/.../core/data/repository/guides/*`, related tests in `commonTest/`

- [ ] **Step 1: Add include**

```kotlin
include(":feature-guides:impl")
```

- [ ] **Step 2: Create `feature-guides/impl/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    id("json-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.api)
            implementation(projects.common)
            implementation(projects.coreDomain)
            implementation(projects.coreNetwork)
            implementation(projects.corePresentation)
            implementation(projects.coreResources)
            implementation(projects.coreStorage)
            implementation(libs.koin.core)
            implementation(libs.napier)
        }
    }
}
```

- [ ] **Step 3: Move source files**

```bash
mkdir -p feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/{di,domain,data/api,data/repository}

git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/di/* \
        feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/di/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/* \
        feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/domain/

# Data layer (currently under core/data/, moves into feature)
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/* \
        feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/api/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/guides/* \
        feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/repository/
```

- [ ] **Step 4: Move tests**

```bash
mkdir -p feature-guides/impl/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/{domain,data/repository}

# GuidesExecutor test
git mv composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/GuidesExecutorTest.kt \
        feature-guides/impl/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/domain/

# GuidesRepository test (currently under core/data/repository/guides/)
git mv composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/guides/GuidesRepositoryTest.kt \
        feature-guides/impl/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/repository/
```

(Adjust paths if the tests have different exact locations — verify with `find composeApp/src/commonTest -name "Guides*"`.)

- [ ] **Step 5: Rewrite package declarations**

```bash
# DI
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.impl\.di$|package dev.nonoxy.d2buildhelper.feature.guides.impl.di|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/di/*.kt

# Domain (Store factory/executor/reducer)
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.impl\.domain$|package dev.nonoxy.d2buildhelper.feature.guides.impl.domain|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/domain/*.kt

# Data API
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.api\.guides$|package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/api/*.kt 2>/dev/null

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.api\.guides\.models$|package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/api/models/*.kt 2>/dev/null

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.api\.guides\.mappers$|package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.mappers|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/api/mappers/*.kt 2>/dev/null

# Data Repository
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.repository\.guides$|package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository|' \
    feature-guides/impl/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/repository/*.kt 2>/dev/null

# Tests
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.impl\.domain$|package dev.nonoxy.d2buildhelper.feature.guides.impl.domain|' \
    feature-guides/impl/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/domain/*.kt 2>/dev/null

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.core\.data\.repository\.guides$|package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository|' \
    feature-guides/impl/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/feature/guides/impl/data/repository/*.kt 2>/dev/null
```

- [ ] **Step 6: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.\(features\.guides\.impl\|core\.data\.\(api\|repository\)\.guides\)" composeApp/ feature-guides/ | while read f; do
  sed -i '' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.impl\.di\.|dev.nonoxy.d2buildhelper.feature.guides.impl.di.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.impl\.domain\.|dev.nonoxy.d2buildhelper.feature.guides.impl.domain.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.data\.api\.guides\.|dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.core\.data\.repository\.guides\.|dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository.|g' \
    "$f"
done
```

- [ ] **Step 7: Update `AppModule.kt`**

In `composeApp/.../core/di/AppModule.kt`: remove all Guides-specific bindings (repository, datasource, api, store factory) — they now live in `FeatureGuidesImplModule`. Ensure `featureGuidesImplModule()` is included in the aggregate `allModules` list.

- [ ] **Step 8: Add dependency**

In `:composeApp/build.gradle.kts`:

```kotlin
implementation(projects.featureGuides.impl)
```

- [ ] **Step 9: Verify**

Run: `./gradlew :composeApp:assembleDebug :feature-guides:impl:jvmTest`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 10: Stage**

```bash
git add settings.gradle.kts feature-guides/impl/ composeApp/
```

### Task 2.3: Create `:feature-guides:presentation` module

**Files:**
- Create: `feature-guides/presentation/build.gradle.kts`
- Move: `composeApp/.../features/guides/presentation/GuidesViewModel.kt`, `models/`, `mappers/`

- [ ] **Step 1: Add include**

```kotlin
include(":feature-guides:presentation")
```

- [ ] **Step 2: Create `feature-guides/presentation/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.api)
            api(projects.corePresentation)
            implementation(projects.coreDomain)
            implementation(projects.common)
            implementation(libs.koin.core)
        }
    }
}
```

No compose plugin — presentation layer is Flow/pure data only.

- [ ] **Step 3: Move source files**

```bash
mkdir -p feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/{models,mappers}

git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/GuidesViewModel.kt \
        feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/* \
        feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/models/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/mappers/* \
        feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/mappers/
```

- [ ] **Step 4: Rewrite packages**

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation$|package dev.nonoxy.d2buildhelper.feature.guides.presentation|' \
    feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/GuidesViewModel.kt

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.models$|package dev.nonoxy.d2buildhelper.feature.guides.presentation.models|' \
    feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/models/*.kt

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.mappers$|package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers|' \
    feature-guides/presentation/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/presentation/mappers/*.kt
```

- [ ] **Step 5: Rewrite imports**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.\(models\|mappers\|GuidesViewModel\)" composeApp/ feature-guides/ | while read f; do
  sed -i '' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.models\.|dev.nonoxy.d2buildhelper.feature.guides.presentation.models.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.mappers\.|dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.GuidesViewModel|dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel|g' \
    "$f"
done
```

- [ ] **Step 6: Update GuidesViewModel binding in `:feature-guides:impl` `FeatureGuidesImplModule`**

`viewModelOf(::GuidesViewModel)` import path updates automatically via Step 5 sed.

- [ ] **Step 7: Add dependency**

In `:composeApp/build.gradle.kts`:

```kotlin
implementation(projects.featureGuides.presentation)
```

- [ ] **Step 8: Verify and stage**

Run: `./gradlew :composeApp:assembleDebug`

```bash
git add settings.gradle.kts feature-guides/presentation/ composeApp/
```

### Task 2.4: Create `:feature-guides:ui` module

**Files:**
- Create: `feature-guides/ui/build.gradle.kts`
- Move: `composeApp/.../features/guides/presentation/ui/*` (GuidesScreen + GuidesView + views/)

- [ ] **Step 1: Add include**

```kotlin
include(":feature-guides:ui")
```

- [ ] **Step 2: Create `feature-guides/ui/build.gradle.kts`**

```kotlin
plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.featureGuides.presentation)
            implementation(projects.commonResources)
            implementation(projects.commonUi)
            implementation(projects.coreDomain)
            implementation(projects.coreNavigation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources) // for stringResource interop if still referenced — likely removable; verify
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
        }
    }
}
```

After verifying that no `Res.string.*` references remain (Task 1.3 covered them), drop `implementation(libs.compose.resources)` if unused.

- [ ] **Step 3: Move source files**

```bash
mkdir -p feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/views

git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/GuidesScreen.kt \
        feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/GuidesView.kt \
        feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/* \
        feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/views/
```

If `@Preview` annotations live in androidMain-specific files, move those too. Inspect with `find composeApp/src/androidMain -name "*Guide*"`.

- [ ] **Step 4: Rewrite packages**

```bash
sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.ui$|package dev.nonoxy.d2buildhelper.feature.guides.ui|' \
    feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/*.kt

sed -i '' 's|^package dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.ui\.views$|package dev.nonoxy.d2buildhelper.feature.guides.ui.views|' \
    feature-guides/ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/feature/guides/ui/views/*.kt
```

- [ ] **Step 5: Rewrite imports project-wide**

```bash
grep -rl "dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.ui" composeApp/ feature-guides/ | while read f; do
  sed -i '' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.ui\.views\.|dev.nonoxy.d2buildhelper.feature.guides.ui.views.|g' \
    -e 's|dev\.nonoxy\.d2buildhelper\.features\.guides\.presentation\.ui\.|dev.nonoxy.d2buildhelper.feature.guides.ui.|g' \
    "$f"
done
```

In `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/App.kt`, the line `import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.GuidesScreen` becomes `import dev.nonoxy.d2buildhelper.feature.guides.ui.GuidesScreen` automatically via sed.

- [ ] **Step 6: Add dependency in `:composeApp`**

```kotlin
implementation(projects.featureGuides.ui)
```

- [ ] **Step 7: Remove now-empty `composeApp/.../features/guides/` directories**

```bash
find composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides -type d -empty -delete
```

If `features/guides/` is now empty after Phase 2, the `detailGuide/` stub remains at `features/detailGuide/DetailGuideScreen.kt` (untouched per non-goals).

- [ ] **Step 8: Verify**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

Run: `./gradlew :composeApp:run`
Expected: App opens, Guides screen renders correctly.

- [ ] **Step 9: Stage**

```bash
git add settings.gradle.kts feature-guides/ui/ composeApp/
```

### Task 2.5: Phase 2 final verification + commit

- [ ] **Step 1: Full verification**

Run: `./gradlew detekt :composeApp:assembleDebug :composeApp:jvmTest :composeApp:lintDebug :composeApp:iosSimulatorArm64Test :feature-guides:impl:jvmTest`
Expected: BUILD SUCCESSFUL on all.

- [ ] **Step 2: Manual smoke test**

Run: `./gradlew :composeApp:run`. Verify: Guides loads, hero filter works, retry button works, dialog opens/closes, no UI regression.

- [ ] **Step 3: Commit Phase 2**

```bash
git commit -m "$(cat <<'EOF'
refactor: split Guides feature into :feature-guides:{api,impl,presentation,ui}

Moves all features/guides/ code out of :composeApp into four sub-modules.
:api owns the Store contract and Guide domain model. :impl owns Store
factory/executor/reducer, repository, datasources, and the Koin module.
:presentation owns the ViewModel + Ui models + mappers. :ui owns
GuidesScreen and sub-views. :composeApp depends on all four; AppModule
aggregates featureGuidesImplModule().
EOF
)"
```

---

## Phase 3 — Android Application Split + `:composeApp` Shell

**Goal:** Move `com.android.application` plugin + Android entry files into a new `:androidApp` module. `:composeApp` becomes a pure KMP shell (Android library + JVM + iOS targets only). Remove legacy `android.builtInKotlin=false`/`android.newDsl=false` flags.

### Task 3.1: Create `:androidApp` module

**Files:**
- Create: `androidApp/build.gradle.kts`
- Create: `androidApp/src/main/AndroidManifest.xml`
- Move from `composeApp/src/androidMain/`: `AndroidManifest.xml`, `kotlin/.../App.android.kt` (split: keep `openUrl` actual in composeApp, move `AndroidApp` class + `AppActivity` to androidApp), `res/` if present

- [ ] **Step 1: Add include**

```kotlin
include(":androidApp")
```

- [ ] **Step 2: Create `androidApp/build.gradle.kts`**

```kotlin
import utils.AppVersion

plugins {
    id("android-application-setup")
}

android {
    namespace = "dev.nonoxy.d2buildhelper.android"

    defaultConfig {
        applicationId = "dev.nonoxy.d2buildhelper.androidApp"
        versionCode = AppVersion.getVersionCode(project).get()
        versionName = AppVersion.getVersionName(project).get()
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/main/AndroidManifest.xml")
        res.srcDirs("src/main/res")
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activityCompose)
    implementation(libs.koin.android)
    implementation(libs.napier)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
}
```

`utils.AppVersion` is the existing build-logic helper; it's already on the build-logic classpath.

- [ ] **Step 3: Move AndroidManifest.xml**

```bash
mkdir -p androidApp/src/main
git mv composeApp/src/androidMain/AndroidManifest.xml androidApp/src/main/AndroidManifest.xml
```

Edit `androidApp/src/main/AndroidManifest.xml`: ensure the `android:name=".AndroidApp"` and `android:name=".AppActivity"` references match the new package after the class move (next steps). The current manifest uses relative names (`.AndroidApp`, `.AppActivity`), which resolve against the `applicationId` (`dev.nonoxy.d2buildhelper.androidApp`). After the move, AndroidApp will be at `dev.nonoxy.d2buildhelper.android.AndroidApp`. Either:
- Use full names: `android:name="dev.nonoxy.d2buildhelper.android.AndroidApp"`, `android:name="dev.nonoxy.d2buildhelper.android.AppActivity"`, OR
- Put the classes in the `applicationId` package (`dev.nonoxy.d2buildhelper.androidApp`) and keep relative names.

Choose the first option (full names) — keeps the package matching the `namespace` consistent.

- [ ] **Step 4: Split `App.android.kt`**

Current `composeApp/src/androidMain/kotlin/dev/nonoxy/d2buildhelper/App.android.kt` contains:
- `AndroidApp : Application` — moves to `:androidApp`
- `AppActivity : ComponentActivity` — moves to `:androidApp`
- `openUrl(String?)` actual — **stays** in `:composeApp/androidMain` (the expect is in `App.kt` in `:composeApp/commonMain`)

Create new file `androidApp/src/main/kotlin/dev/nonoxy/d2buildhelper/android/AndroidApp.kt`:

```kotlin
package dev.nonoxy.d2buildhelper.android

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.nonoxy.d2buildhelper.App
import dev.nonoxy.d2buildhelper.app.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {

    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        initKoin { androidContext(this@AndroidApp) }
    }
}

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}
```

(Adjust the import for `initKoin` if its actual package is different — likely `dev.nonoxy.d2buildhelper.core.di.initKoin` until Phase 4 reorganizes naming. Verify with `grep -rn "fun initKoin" composeApp/`.)

- [ ] **Step 5: Slim down `composeApp/src/androidMain/kotlin/dev/nonoxy/d2buildhelper/App.android.kt`**

Replace the file contents with **only** the `openUrl` actual:

```kotlin
package dev.nonoxy.d2buildhelper

import android.content.Intent
import android.net.Uri

internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    // AndroidApp.INSTANCE no longer accessible here. Switch to ProcessLifecycleOwner or remove openUrl if unused.
}
```

**Problem:** `AndroidApp.INSTANCE.startActivity(intent)` is gone (the class moved). Two options:

A. **Drop `openUrl` if unused** — check usages: `grep -rn "openUrl" composeApp/ feature-guides/`. If only declared but not called, delete the `expect`/`actual` triplet across all platforms.

B. **Refactor to take a Context** — change `openUrl` to receive a Context from the call site. Requires changing the expect signature in commonMain.

Choose A if openUrl has zero call sites in the codebase. Otherwise, escalate to a tiny follow-up:

```kotlin
internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val context = androidx.startup.AppInitializer.getInstance(/* application context */)
    // ... requires Context provider; defer real fix to a follow-up commit if unused right now.
}
```

Make the call: if `openUrl` is unused (likely — the iOS actual is commented out, suggesting it's a placeholder), **delete** the expect in `App.kt` and all three actuals.

- [ ] **Step 6: Move `res/` if present**

```bash
ls composeApp/src/androidMain/res 2>/dev/null
```

If non-empty, move:

```bash
git mv composeApp/src/androidMain/res androidApp/src/main/res
```

- [ ] **Step 7: Strip `com.android.application` from `:composeApp`**

In `composeApp/build.gradle.kts`:
- Remove from `plugins { ... }`: `alias(libs.plugins.android.application)`.
- Add: `id("kmp-library")` instead.
- Add: `id("compose-multiplatform-setup")` (composeApp shell still has Compose UI).
- Remove the entire `android { ... }` block — kmp-library handles the Android library configuration. Override only what's specific (namespace if needed).
- Remove `compose.desktop { application { ... } }` block? **No** — keep it; desktop entry lives here.
- Remove from `commonMain.dependencies`: `androidx.activityCompose`, `koin-android` (Android-specific; move to `:androidApp`).
- Move from `androidMain.dependencies` to `:androidApp/build.gradle.kts`: items only needed for the Android Application (already done in `:androidApp` deps).

Final shape of `composeApp/build.gradle.kts` should now look approximately like:

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.napier)

            implementation(projects.common)
            implementation(projects.commonResources)
            implementation(projects.commonUi)
            implementation(projects.coreDomain)
            implementation(projects.coreNavigation)
            implementation(projects.corePresentation)
            implementation(projects.coreNetwork)
            implementation(projects.coreStorage)
            implementation(projects.coreResources)
            implementation(projects.featureGuides.api)
            implementation(projects.featureGuides.impl)
            implementation(projects.featureGuides.presentation)
            implementation(projects.featureGuides.ui)
        }

        commonTest.dependencies {
            // existing test deps
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
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
```

(Compose ui tooling and tooling preview for `androidMain` of `:composeApp` — drop if no `@Preview` survives in `:composeApp/androidMain`.)

- [ ] **Step 8: Remove legacy AGP flags**

In `gradle.properties`:
- Remove `android.builtInKotlin=false`
- Remove `android.newDsl=false`

- [ ] **Step 9: Update `:composeApp/.../core/di/Koin.kt` reference (if needed)**

Verify the `initKoin` function signature. AndroidApp calls `initKoin { androidContext(...) }`. The function should accept `KoinAppDeclaration? = null` — same as today.

- [ ] **Step 10: Verify Android build**

Run: `./gradlew :androidApp:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 11: Verify desktop and iOS builds**

Run: `./gradlew :composeApp:run`
Expected: App opens, behaves as before.

Run: `./gradlew :composeApp:iosSimulatorArm64Test`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 12: Stage**

```bash
git add settings.gradle.kts androidApp/ composeApp/ gradle.properties
```

### Task 3.2: Update Xcode iOS project if framework path changed

**Files:**
- Possibly modify: `iosApp/iosApp.xcodeproj/project.pbxproj`

- [ ] **Step 1: Locate framework path**

The iOS framework `ComposeApp` was built by `:composeApp` before and is still built by `:composeApp` (KMP shell). The Xcode project should reference the same output path. Verify by opening the project:

```bash
open iosApp/iosApp.xcodeproj
```

Build iOS target from Xcode. Expected: build succeeds.

If the build fails because the framework name or path changed, edit `iosApp/iosApp.xcodeproj/project.pbxproj` to point at the correct `composeApp.framework` path. The framework name is set by `binaries.framework { baseName = "ComposeApp" }` in `:composeApp/build.gradle.kts` (via `kmp-library` plugin — verify the plugin's generated `baseName` matches "ComposeApp"; if it generates `composeApp` instead, override in `:composeApp/build.gradle.kts`).

To override the baseName:

```kotlin
kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}
```

- [ ] **Step 2: Stage any Xcode project changes**

```bash
git add iosApp/
```

### Task 3.3: Phase 3 commit

- [ ] **Step 1: Final verification**

Run: `./gradlew detekt :androidApp:assembleDebug :androidApp:lintDebug :composeApp:jvmTest :composeApp:iosSimulatorArm64Test`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Manual run on each platform**

Android: `./gradlew :androidApp:installDebug` (if device/emulator connected) or open in Android Studio and run.
Desktop: `./gradlew :composeApp:run`
iOS: open `iosApp/iosApp.xcodeproj` in Xcode, run on simulator.

Expected: all three platforms launch and Guides screen loads.

- [ ] **Step 3: Commit Phase 3**

```bash
git commit -m "$(cat <<'EOF'
refactor: isolate com.android.application in :androidApp module

Moves AndroidApp + AppActivity + AndroidManifest into the new :androidApp
module so :composeApp becomes a pure KMP shell (Android library + JVM +
iOS targets). Removes android.builtInKotlin=false and android.newDsl=false
flags — they were only required because com.android.application sat in a
KMP module. The Xcode project continues to consume the ComposeApp.framework
from :composeApp without changes.
EOF
)"
```

---

## Phase 4 — Documentation + CI + Detekt Baseline

**Goal:** Bring all rule files, CLAUDE.md, README, CI workflows, and detekt baseline in line with the new module layout. No code changes — pure metadata/docs.

### Task 4.1: Update CLAUDE.md

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: Update Build Commands section**

Replace the `./gradlew :composeApp:assembleDebug` and `./gradlew :composeApp:lintDebug` lines:

```shell
./gradlew :androidApp:assembleDebug         # Android debug APK
./gradlew :composeApp:run                    # Desktop (JVM)
./gradlew :composeApp:iosSimulatorArm64Test
./gradlew :composeApp:jvmTest
./gradlew :androidApp:lintDebug              # Android Lint
./gradlew detekt                              # Static analysis (Kotlin)
```

- [ ] **Step 2: Update Stack — module structure**

Change "Single-module project (`:composeApp`) with `build-logic` composite build" to:

"Multi-module project (15 modules: `:androidApp`, `:composeApp` shell, `:common*` shared, `:core-*` infrastructure, `:feature-guides:{api,impl,presentation,ui}`) with `build-logic` composite build."

Add to the Stack list:
- "moko-resources X.Y.Z (replaces composeResources for strings + binary files)"

- [ ] **Step 3: Rewrite Architecture section**

Replace the ASCII tree under `composeApp/src/commonMain/...` with a module table:

```markdown
## Architecture

15 modules. Each KMP module applies `kmp-library` convention plugin; Compose-aware modules add `compose-multiplatform-setup`; `:androidApp` uses `android-application-setup`.

| Module | Type | Responsibility |
|---|---|---|
| `:androidApp` | Android-only | `com.android.application` entry. `AndroidApp`, `AppActivity`, manifest, Android resources. |
| `:composeApp` | KMP shell | `App.kt`, `D2BuildHelperTheme`, NavHost wiring, desktop `main.kt`, iOS framework. Composition Root for Koin (`initKoin`). |
| `:common` | KMP | `coRunCatching`, `ResultExtensions`, `OneTimeEvent`, `TimeConverter`, `Mapper`, `CoroutineDispatchers`. |
| `:common-ui` | KMP + Compose | `LocalImageLoader`. |
| `:common-resources` | KMP + moko | All strings.xml + JSON constants via moko-resources `MR`. No Kotlin code. |
| `:core-domain` | KMP | App-wide pure domain models: `Hero`, `Item`, `Ability`, `ImageResources`. |
| `:core-navigation` | KMP + Compose | `AppScreens` sealed routes, `LocalNavHost`. |
| `:core-network` | KMP | `ApolloClient` + GraphQL queries + Stratz schema + Ktor expect/actual engines + BuildConfig (Stratz). |
| `:core-presentation` | KMP | `BaseViewModel<S,L>`, `BaseExecutor`, `BaseIosViewModel`, `coreMVIKotlinModule()` (LoggingStoreFactory via Napier). |
| `:core-storage` | KMP | Supabase storage client + BuildConfig (Supabase). |
| `:core-resources` | KMP | `ResourcesRepository` + datasources reading `MR.files` (constants) and Supabase URLs (icons). |
| `:feature-guides:api` | KMP | `GuidesStore` contract (Intent/State/Label) + `Guide` domain model. |
| `:feature-guides:impl` | KMP | `GuidesStoreFactory`, `GuidesExecutor`, `GuidesReducer`, repository + datasources + DTO/mappers + `featureGuidesImplModule()` Koin module. |
| `:feature-guides:presentation` | KMP | `GuidesViewModel`, `UiGuidesState`, `UiGuidesLabel`, mappers. |
| `:feature-guides:ui` | KMP + Compose | `GuidesScreen` + sub-views. |
```

- [ ] **Step 4: Update Key Gotchas**

- Remove the gotcha about `android.builtInKotlin=false`/`android.newDsl=false` (no longer applies).
- Remove the gotcha about `composeResources/` — replace with: "All strings + binary resources live in `:common-resources` via moko-resources. Reference via `import dev.nonoxy.d2buildhelper.common.resources.MR` then `MR.strings.foo` / `MR.files.bar`."
- Add a new gotcha: "Cross-feature dependencies are not allowed. `:feature-A:impl` may depend on `:feature-B:api` only if both features genuinely share that contract — otherwise hoist the shared logic into a `:core-*` module."

- [ ] **Step 5: Stage (no commit yet)**

```bash
git add CLAUDE.md
```

### Task 4.2: Rewrite rule files

**Files:** All under `.claude/rules/`

- [ ] **Step 1: `mobile-overview.mdc`**

Replace the package layout section to describe the 15-module structure. List naming conventions: feature modules nested `:feature-X:api`/`:impl`/`:presentation`/`:ui`; shared modules flat `:common`, `:common-ui`, `:common-resources`, `:core-X` (no sub-paths). All package roots: `dev.nonoxy.d2buildhelper.<module-segment>...`.

- [ ] **Step 2: `mobile-architecture.mdc`**

Add a section "Module dependency invariants":

```markdown
## Module dependency invariants

- `:feature-A:impl` never depends on `:feature-B:*` (impl or api).
- `:feature-X:api` depends only on `:core-domain` and Kotlin stdlib.
- `:feature-X:impl` depends on its `:api` + relevant `:core-*` modules. No cross-feature impl-to-impl wiring.
- `:feature-X:presentation` depends on its `:api` + `:core-presentation`, not on `:impl`.
- `:feature-X:ui` depends on its `:presentation` + `:common-ui` + `:common-resources`.
- `:core-*` modules are single (no api/impl split). Runtime hiding is enforced by Koin (consumers see interfaces).
- `:composeApp` (KMP shell) is the only Composition Root — it depends on all `:*:impl` modules and aggregates Koin modules in `initKoin()`.
```

Update the MVIKotlin pattern section to map Store/Intent/State/Label/Reducer/Executor/Factory onto `:feature-X:api` (Store interface, Intent, State, Label) vs `:feature-X:impl` (StoreFactory, Executor, Reducer).

- [ ] **Step 3: `mobile-data-layer.mdc`**

Update repository pattern guidance: feature-specific repositories live in `:feature-X:impl`. App-wide repositories (`ResourcesRepository`) live in `:core-resources`. DTO → domain mapping stays in the repository implementation.

- [ ] **Step 4: `mobile-network.mdc`**

Apollo client and GraphQL operations live in `:core-network`. Supabase storage lives in `:core-storage`. buildConfig is configured in both — Stratz key in `:core-network`, Supabase key in `:core-storage`. References: `dev.nonoxy.d2buildhelper.core.network.BuildConfig` for Stratz, `dev.nonoxy.d2buildhelper.core.storage.BuildConfig` for Supabase.

- [ ] **Step 5: `mobile-resources.mdc`**

Rewrite to describe moko-resources in `:common-resources`. Path: `common-resources/src/commonMain/moko-resources/base/{strings.xml, files/*.json}`. Code reference pattern:

```kotlin
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
Text(stringResource(MR.strings.all_heroes))
```

For binary files:

```kotlin
val text = MR.files.constant_heroes.readText()
```

Adding a new string: edit `common-resources/src/commonMain/moko-resources/base/strings.xml`, rebuild, then reference `MR.strings.<name>`.

- [ ] **Step 6: `mobile-compose.mdc`**

Add: "Apply `compose-multiplatform-setup` plugin to any module with `@Composable` code. Do not apply to presentation modules that contain only Flow + pure data classes."

- [ ] **Step 7: `mobile-error-handling.mdc`**

Update Napier initialization path: now in `:androidApp/.../AndroidApp.onCreate`, `:composeApp/jvmMain/main.kt`, and `:composeApp/iosMain/.../MainViewController`.

- [ ] **Step 8: `mobile-roadmap.mdc`**

Mark complete:

```markdown
- [x] **Multi-module split.** Completed 2026-05-18. 15 modules: feature × {api,impl,presentation,ui} + 7 shared (`common`, `common-ui`, `common-resources`, `core-domain`, `core-navigation`, `core-network`, `core-presentation`, `core-storage`, `core-resources`) + `:androidApp` Android entry + `:composeApp` KMP shell.
- [x] **moko-resources migration.** Completed 2026-05-18. All strings and binary resources live in `:common-resources` via `MR.strings.*` / `MR.files.*`.
```

Remove the "Implication for Today" subsection lines referencing these as out-of-scope.

- [ ] **Step 9: `mobile-code-rules.mdc`**

No changes required — rules apply per-file regardless of module.

- [ ] **Step 10: Stage**

```bash
git add .claude/rules/
```

### Task 4.3: Update CI workflows

**Files:** All under `.github/workflows/`

- [ ] **Step 1: Locate workflow files**

```bash
ls .github/workflows/
```

- [ ] **Step 2: Update Android lint command**

In each workflow file referencing `:composeApp:lintDebug`, replace with `:androidApp:lintDebug`.

```bash
grep -rl "composeApp:lintDebug" .github/workflows/ | while read f; do
  sed -i '' 's|composeApp:lintDebug|androidApp:lintDebug|g' "$f"
done
```

- [ ] **Step 3: Verify detekt command**

`./gradlew detekt` without a project filter applies to all modules automatically (detekt plugin handles multi-module). No change needed unless the workflow restricts to `:composeApp:detekt`. Check:

```bash
grep -n "detekt" .github/workflows/*.yml
```

If a workflow uses `:composeApp:detekt`, change to plain `detekt` to cover all modules.

- [ ] **Step 4: Stage**

```bash
git add .github/workflows/
```

### Task 4.4: Regenerate detekt baseline

**Files:** `linters/detekt/baseline.xml`

- [ ] **Step 1: Delete old baseline**

```bash
rm linters/detekt/baseline.xml
```

- [ ] **Step 2: Regenerate**

Run: `./gradlew detektBaseline`
Expected: BUILD SUCCESSFUL. New baseline written to `linters/detekt/baseline.xml`.

- [ ] **Step 3: Verify detekt passes with new baseline**

Run: `./gradlew detekt`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Inspect baseline**

```bash
wc -l linters/detekt/baseline.xml
git diff --stat linters/detekt/baseline.xml
```

Sanity-check that the baseline contains entries from the new modules (e.g., `:core-resources`, `:feature-guides:impl`) and that paths no longer reference `composeApp/.../`.

- [ ] **Step 5: Stage**

```bash
git add linters/detekt/baseline.xml
```

### Task 4.5: Final commit and PR

- [ ] **Step 1: Full green-light verification**

```bash
./gradlew detekt :androidApp:assembleDebug :androidApp:lintDebug \
                 :composeApp:jvmTest :composeApp:iosSimulatorArm64Test \
                 :feature-guides:impl:jvmTest
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Commit Phase 4**

```bash
git commit -m "$(cat <<'EOF'
docs: update rules, CLAUDE.md, CI for the new 15-module layout

Rewrites the Architecture section in CLAUDE.md and the relevant rule files
to describe per-feature api/impl/presentation/ui modules plus the shared
common*/core-* modules. CI workflows now invoke :androidApp:lintDebug and
the multi-module detekt task. Regenerates the detekt baseline against the
new module paths.
EOF
)"
```

- [ ] **Step 3: Push branch and open PR**

```bash
git push -u origin refactor/multi-module-and-moko-resources

gh pr create --base develop-cmp \
  --title "refactor: multi-module split and moko-resources migration" \
  --body "$(cat <<'EOF'
## Summary

- Split the single `:composeApp` module into 15 modules following the KMMTemplate playbook: per-feature `{api, impl, presentation, ui}` + shared `common*`/`core-*` + Android-only `:androidApp`.
- Migrate all resources (strings + binary JSON constants) from compose-resources to `:common-resources` using moko-resources.
- Remove `android.builtInKotlin=false` and `android.newDsl=false` flags from `gradle.properties` (no longer needed once `com.android.application` lives in `:androidApp`).
- Convention plugins (`kmp-library`, `compose-multiplatform-setup`, `android-application-setup`) centralise per-module setup in `build-logic`.

Spec: `docs/superpowers/specs/2026-05-18-multi-module-and-moko-resources-design.md`
Plan: `docs/superpowers/plans/2026-05-18-multi-module-and-moko-resources.md`

## Test plan

- [ ] `./gradlew detekt` green
- [ ] `./gradlew :androidApp:assembleDebug` green
- [ ] `./gradlew :androidApp:lintDebug` green
- [ ] `./gradlew :composeApp:jvmTest` green
- [ ] `./gradlew :composeApp:iosSimulatorArm64Test` green
- [ ] `./gradlew :feature-guides:impl:jvmTest` green
- [ ] Manual: Android APK installs and Guides screen renders
- [ ] Manual: `./gradlew :composeApp:run` opens desktop window, Guides loads
- [ ] Manual: iOS simulator from Xcode launches and Guides loads
EOF
)"
```

---

## Self-Review Notes

This plan covers every section of the spec:
- **Module Graph (spec):** Tasks 1.1–1.9, 2.1–2.4, 3.1 create the 15 modules.
- **Convention Plugins (spec):** Tasks 0.2–0.4.
- **DI Strategy (spec):** Tasks 1.4, 1.9, 2.2 expose per-module Koin functions; Task 3.1 keeps `initKoin` aggregation in `:composeApp`.
- **moko-resources Strategy (spec):** Task 1.3 + reference updates in 1.3, 1.9.
- **Migration Phases (spec):** Plan phases 0–4 match spec phases 0–4.
- **Risks (spec):** Risk #1 (iOS framework) addressed in Task 3.2. Risk #4 (moko/CMP compat) noted in Task 0.1. Risk #5 (JSON rename) in Task 1.3. Risk #7 (detekt baseline) in Task 4.4.
- **Package Mapping (spec):** Each move task references exact source/destination per the spec's table.

**Verification anchors:** Each phase ends with the exact `./gradlew` command set that must pass before commit. Phase 1 expands moko + 9 modules into 10 tasks; Phase 2 expands feature-guides into 4 tasks + 1 verification task.

**Reuse caveat:** Where the plan uses `sed -i ''` (macOS BSD sed), Linux users would drop the empty string argument. The developer environment is documented as macOS Darwin 24.5, so `sed -i ''` is correct here.

**Open follow-ups (not in this PR):**
- DetailGuide feature implementation (untouched stub).
- Per-module unit test backfill.
- Multi-locale moko-resources files.

---

## Execution Handoff

**Plan complete and saved to `docs/superpowers/plans/2026-05-18-multi-module-and-moko-resources.md`.**

Two execution options:

1. **Subagent-Driven (recommended)** — fresh subagent per task, review between tasks, fast iteration. Each task is bounded enough to fit a subagent's context comfortably; review checkpoints catch import-rewrite mistakes early.
2. **Inline Execution** — execute tasks in this session using `executing-plans`, batch execution with checkpoints for review.

Which approach?
