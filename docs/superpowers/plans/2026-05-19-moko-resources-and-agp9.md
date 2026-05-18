# moko-resources + AGP9 DSL Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the two follow-up items from the multi-module refactor — finish the moko-resources migration that was deferred from PR #4, and remove the AGP 9 + KMP legacy DSL bypass (`android.builtInKotlin=false`, `android.newDsl=false`).

**Architecture:** Two independent phases, applied as additional commits on `refactor/multi-module-and-moko-resources` (same PR #4). Phase A reshapes `:common-resources` from Compose Resources to moko-resources and rewrites 7 consumer sites. Phase B migrates `KmpLibraryPlugin` from `com.android.library` to `com.android.kotlin.multiplatform.library` and drops the bypass flags. Phase C refreshes docs and the detekt baseline.

**Tech Stack:** moko-resources 0.24.5 (`dev.icerock.moko:resources`, `:resources-compose`, plugin `dev.icerock.mobile.multiplatform-resources`). AGP 9.0 KMP plugin `com.android.kotlin.multiplatform.library`. All entries already exist in `gradle/libs.versions.toml`.

---

## File Structure Overview

### Phase A — files touched

| Action | File |
|---|---|
| Modify | `gradle/libs.versions.toml` (verify moko entries present) |
| Modify | `common-resources/build.gradle.kts` (compose-resources → moko-resources) |
| Move | `common-resources/src/commonMain/composeResources/values/strings.xml` → `.../moko-resources/base/strings.xml` |
| Move + rename | `common-resources/src/commonMain/composeResources/files/constants/{heroes,items,abilities}.json` → `.../moko-resources/base/files/constant_{heroes,items,abilities}.json` |
| Move + rename | `common-resources/src/commonMain/composeResources/font/noto_sans_{regular,bold}.ttf` → `.../moko-resources/base/fonts/NotoSans-{Regular,Bold}.ttf` |
| Delete | `common-resources/src/commonMain/composeResources/` directory (empty after moves) |
| Modify | `core-resources/src/commonMain/kotlin/.../ConstantResourcesDataSource.kt` |
| Modify | `common-ui/src/commonMain/kotlin/.../theme/Type.kt` |
| Modify | `feature-guides/ui/src/commonMain/kotlin/.../views/HeroFilterDialogView.kt` |
| Modify | `feature-guides/ui/src/commonMain/kotlin/.../views/GuidesErrorView.kt` |
| Modify | `feature-guides/ui/src/commonMain/kotlin/.../views/GuidesTopBarView.kt` |
| Modify | `composeApp/src/jvmMain/kotlin/main.kt` |
| Modify | Build scripts that still pull `libs.compose.resources` |

### Phase B — files touched

| Action | File |
|---|---|
| Modify | `gradle.properties` (drop `android.builtInKotlin=false`, `android.newDsl=false`) |
| Modify | `build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt` |
| Modify | `build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt` |
| Modify | `build-logic/src/main/kotlin/extensions/ProjectExtensions.kt` |

### Phase C — files touched

| Action | File |
|---|---|
| Modify | `.claude/rules/mobile-roadmap.mdc` |
| Modify | `.claude/rules/mobile-resources.mdc` |
| Modify | `CLAUDE.md` (Architecture, Stack, Key Gotchas) |
| Regenerate | `linters/detekt/baseline.xml` |

---

## Phase A — moko-resources Migration

### Task A.1: Verify moko-resources entries in libs.versions.toml

**Files:**
- Read: `gradle/libs.versions.toml`

- [ ] **Step 1: Confirm versions**

Run: `grep -nE "(moko-resources|android-kotlin-multiplatform)" gradle/libs.versions.toml`

Expected output contains lines:
```
moko-resources = "0.24.5"
moko-resources = { module = "dev.icerock.moko:resources", version.ref = "moko-resources" }
moko-resources-compose = { module = "dev.icerock.moko:resources-compose", version.ref = "moko-resources" }
moko-resources = { id = "dev.icerock.mobile.multiplatform-resources", version.ref = "moko-resources" }
```

If any line is missing, add it before proceeding. No commit yet.

---

### Task A.2: Reshape `:common-resources` build script

**Files:**
- Modify: `common-resources/build.gradle.kts`

- [ ] **Step 1: Rewrite the module's build script**

```kotlin
import dev.icerock.gradle.MRVisibility

plugins {
    id("kmp-library")
    id("compose-multiplatform-setup")
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
    resourcesVisibility.set(MRVisibility.Public)
}
```

Rationale:
- `compose-multiplatform-setup` stays — Compose APIs still consume the generated `MR` via `moko-resources-compose`.
- `compose-resources` dependency is **dropped**; moko replaces it.
- `resourcesPackage` must match the consumer imports (`dev.nonoxy.d2buildhelper.common.resources.MR`).

- [ ] **Step 2: Do not commit yet** — assets must be moved before the module can sync. Continue with A.3.

---

### Task A.3: Move assets into moko layout

**Files:**
- Move: `common-resources/src/commonMain/composeResources/` → `common-resources/src/commonMain/moko-resources/base/`
- Rename within `base/files/`: `heroes.json` → `constant_heroes.json`, `items.json` → `constant_items.json`, `abilities.json` → `constant_abilities.json`
- Rename within `base/fonts/`: `noto_sans_regular.ttf` → `NotoSans-Regular.ttf`, `noto_sans_bold.ttf` → `NotoSans-Bold.ttf`
- Move: existing `values/strings.xml` → `base/strings.xml` (no per-locale split needed)

- [ ] **Step 1: Run the moves**

```bash
mkdir -p common-resources/src/commonMain/moko-resources/base/files
mkdir -p common-resources/src/commonMain/moko-resources/base/fonts

git mv common-resources/src/commonMain/composeResources/values/strings.xml \
       common-resources/src/commonMain/moko-resources/base/strings.xml

git mv common-resources/src/commonMain/composeResources/files/constants/heroes.json \
       common-resources/src/commonMain/moko-resources/base/files/constant_heroes.json
git mv common-resources/src/commonMain/composeResources/files/constants/items.json \
       common-resources/src/commonMain/moko-resources/base/files/constant_items.json
git mv common-resources/src/commonMain/composeResources/files/constants/abilities.json \
       common-resources/src/commonMain/moko-resources/base/files/constant_abilities.json

git mv common-resources/src/commonMain/composeResources/font/noto_sans_regular.ttf \
       common-resources/src/commonMain/moko-resources/base/fonts/NotoSans-Regular.ttf
git mv common-resources/src/commonMain/composeResources/font/noto_sans_bold.ttf \
       common-resources/src/commonMain/moko-resources/base/fonts/NotoSans-Bold.ttf

rmdir common-resources/src/commonMain/composeResources/files/constants
rmdir common-resources/src/commonMain/composeResources/files
rmdir common-resources/src/commonMain/composeResources/values
rmdir common-resources/src/commonMain/composeResources/font
rmdir common-resources/src/commonMain/composeResources
```

- [ ] **Step 2: Verify strings.xml content is unchanged**

Run: `cat common-resources/src/commonMain/moko-resources/base/strings.xml`

Expected: `<string name="all_heroes">...`, `<string name="app_name">...`, `<string name="error_loading">...`, `<string name="hero_filter">...`, `<string name="retry">...`.

- [ ] **Step 3: Sync gradle to generate MR (no compile yet)**

Run: `./gradlew :common-resources:generateMRcommonMain --no-daemon 2>&1 | tail -50`

Expected: `BUILD SUCCESSFUL`. Inspect generated MR:

```bash
find common-resources/build -type f -name "MR.kt" -o -name "Strings.kt" -o -name "Fonts.kt" | head
```

Note the exact font-family member name (`MR.fonts.NotoSans.regular`, `MR.fonts.NotoSans.bold`) — needed for A.5.

If `generateMRcommonMain` fails, do NOT proceed — diagnose and fix the build script in A.2 before continuing.

---

### Task A.4: Migrate `ConstantResourcesDataSource` to MR.files

**Files:**
- Modify: `core-resources/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/resources/data/local/constants/ConstantResourcesDataSource.kt`
- Modify: `core-resources/build.gradle.kts` (add moko dependency, drop compose-resources if present)

- [ ] **Step 1: Update DataSource to read via MR.files**

Replace the existing implementation with:

```kotlin
package dev.nonoxy.d2buildhelper.core.resources.data.local.constants

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models.ItemDto
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class ConstantResourcesDataSource(
    private val dispatchers: CoroutineDispatchers,
) : ConstantResources {
    private val json = Json { coerceInputValues = true }

    override suspend fun getHeroConstants(): Result<List<HeroDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = { json.decodeFromString(MR.files.constant_heroes.readText()) },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getHeroConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getItemConstants(): Result<List<ItemDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = { json.decodeFromString(MR.files.constant_items.readText()) },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getItemConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getAbilityConstants(): Result<List<AbilityDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = { json.decodeFromString(MR.files.constant_abilities.readText()) },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getAbilityConstants failed")
                throwable.wrapResultFailure()
            },
        )
    }
}
```

Notes:
- `MR.files.constant_heroes.readText()` is a `suspend fun` on `FileResource` from moko-resources 0.24.x. If the generated API exposes a different reader (e.g. `readText(context)`), adapt — confirm via the generated `MR.kt` from Task A.3.
- The `withContext(dispatchers.io)` wrapper stays.
- Drop `@OptIn(ExperimentalResourceApi::class)` — moko's API is stable, no opt-in needed.
- Drop the `private companion object` path constants — they no longer apply.
- Type parameters `List<HeroDto>`, etc., are inferred by Kotlin from the function return type. If the compiler complains, restore the explicit `<List<HeroDto>>` form.

- [ ] **Step 2: Confirm `:core-resources` depends on `:common-resources`'s exported moko library**

Open `core-resources/build.gradle.kts` and ensure it has `api(projects.commonResources)` (already present from Phase 1 of the multi-module split). No new explicit moko dep is needed — it transits through `api(libs.moko.resources)` exported by `:common-resources`.

If `core-resources/build.gradle.kts` still has a leftover `implementation(libs.compose.resources)`, remove it.

- [ ] **Step 3: Compile just this module**

Run: `./gradlew :core-resources:compileKotlinJvm 2>&1 | tail -30`

Expected: `BUILD SUCCESSFUL`.

---

### Task A.5: Migrate `Type.kt` fonts to MR.fonts

**Files:**
- Modify: `common-ui/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/ui/theme/Type.kt`

- [ ] **Step 1: Replace the Compose-Resources `Font(...)` calls with moko's fontFamilyResource**

moko-resources-compose 0.24.x exposes `fontFamilyResource(FontResource)`. Generated names per A.3: `MR.fonts.NotoSans.regular` (FontResource), `MR.fonts.NotoSans.bold` (FontResource).

Rewrite `NotoSansFontFamily()`:

```kotlin
package dev.nonoxy.d2buildhelper.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.asFont
import dev.nonoxy.d2buildhelper.common.resources.MR

@Composable
fun NotoSansFontFamily(): FontFamily {
    val regular = MR.fonts.NotoSans.regular.asFont(weight = FontWeight.Normal) ?: return FontFamily.Default
    val bold = MR.fonts.NotoSans.bold.asFont(weight = FontWeight.Bold) ?: return FontFamily(regular)
    return FontFamily(regular, bold)
}
```

Notes / fallbacks:
- `FontResource.asFont(weight)` is a Composable extension from `moko-resources-compose` 0.24.x that returns `androidx.compose.ui.text.font.Font?` (nullable on platforms where the font cannot be loaded — primarily iOS prior to font-bundling fix).
- If `asFont` is not the symbol name in 0.24.5, check the generated `MR.fonts` artifact and the imported package — alternatives in this version include `fontResource(...)` returning `Font` (non-null). Use whatever exists; do **not** invent.
- `FontFamily.Default` fallback is acceptable behaviour: the screen still renders system text if iOS cannot load bundled fonts.

`D2BuildHelperTypography()` and `LocalD2BuildHelperTypography` stay unchanged.

- [ ] **Step 2: Compile**

Run: `./gradlew :common-ui:compileKotlinJvm 2>&1 | tail -30`

Expected: `BUILD SUCCESSFUL`. If the API name is wrong, the compile error will identify the symbol — adapt to the actual generated API.

---

### Task A.6: Migrate the 4 `:feature-guides:ui` consumers

**Files:**
- Modify: `feature-guides/ui/src/commonMain/kotlin/.../views/HeroFilterDialogView.kt`
- Modify: `feature-guides/ui/src/commonMain/kotlin/.../views/GuidesErrorView.kt`
- Modify: `feature-guides/ui/src/commonMain/kotlin/.../views/GuidesTopBarView.kt`

(Note: only 3 files use strings — `HeroFilterDialogView`, `GuidesErrorView`, `GuidesTopBarView`. `GuidesErrorView` uses two strings.)

- [ ] **Step 1: Replace imports**

In each file remove:
```kotlin
import dev.nonoxy.d2buildhelper.common.resources.Res
import dev.nonoxy.d2buildhelper.common.resources.<name>
import org.jetbrains.compose.resources.stringResource
```

Add:
```kotlin
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
```

- [ ] **Step 2: Replace call sites**

- `stringResource(Res.string.all_heroes)` → `stringResource(MR.strings.all_heroes)`
- `stringResource(Res.string.error_loading)` → `stringResource(MR.strings.error_loading)`
- `stringResource(Res.string.retry)` → `stringResource(MR.strings.retry)`
- `stringResource(Res.string.hero_filter)` → `stringResource(MR.strings.hero_filter)`

`@Preview` annotation in `HeroFilterDialogView` stays — it comes from `org.jetbrains.compose.ui.tooling.preview.Preview` (unrelated to resources).

- [ ] **Step 3: Compile**

Run: `./gradlew :feature-guides:ui:compileKotlinJvm 2>&1 | tail -30`

Expected: `BUILD SUCCESSFUL`.

---

### Task A.7: Migrate `composeApp/jvmMain/main.kt`

**Files:**
- Modify: `composeApp/src/jvmMain/kotlin/main.kt`

- [ ] **Step 1: Replace imports + call site**

Remove:
```kotlin
import dev.nonoxy.d2buildhelper.common.resources.Res
import dev.nonoxy.d2buildhelper.common.resources.app_name
import org.jetbrains.compose.resources.stringResource
```

Add:
```kotlin
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
```

Replace:
- `title = stringResource(Res.string.app_name)` → `title = stringResource(MR.strings.app_name)`

- [ ] **Step 2: Compile**

Run: `./gradlew :composeApp:compileKotlinJvm 2>&1 | tail -30`

Expected: `BUILD SUCCESSFUL`.

---

### Task A.8: Drop `compose-resources` deps and stale plugin block

**Files:**
- Modify: every `build.gradle.kts` containing `libs.compose.resources` or a `compose.resources { ... }` block (other than what A.2 already removed)

- [ ] **Step 1: Find leftovers**

Run:
```bash
grep -rn -E "(libs\.compose\.resources|compose\.resources \{)" --include="*.kts"
```

For each match: remove the dependency line and any `compose.resources { ... }` configuration block. `:common-resources/build.gradle.kts` should be the only module still referencing Compose Resources — and that was already removed in A.2.

`libs.compose.ui.tooling.preview` is unrelated to resources — keep wherever `@Preview` is used.

- [ ] **Step 2: Confirm no `Res.` references remain in source**

Run:
```bash
grep -rn -E "(Res\.string|Res\.readBytes|Res\.files|Res\.font|stringResource\(Res\.|import dev\.nonoxy\.d2buildhelper\.common\.resources\.Res)" --include="*.kt" --include="*.kts" | grep -v "/build/"
```

Expected: empty output. Anything matched here is a missed migration — fix it before moving on.

---

### Task A.9: Phase A verification + commit

- [ ] **Step 1: Full verification matrix**

Run all six commands sequentially (any failure → stop and fix):
```bash
./gradlew detekt
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:lintDebug
./gradlew :composeApp:jvmTest
./gradlew :composeApp:iosSimulatorArm64Test
./gradlew :feature-guides:impl:jvmTest
```

Expected: `BUILD SUCCESSFUL` for each.

- [ ] **Step 2: Smoke-check generated MR completeness**

Run:
```bash
find common-resources/build/generated/moko -name "MR.kt" -exec cat {} \;
```

Expected: `object MR { object strings { ... }; object files { ... }; object fonts { ... } }` with the 5 strings, 3 files, 1 NotoSans family with 2 weights.

- [ ] **Step 3: Commit**

```bash
git add common-resources core-resources common-ui feature-guides composeApp
git commit -m "refactor: migrate :common-resources from Compose Resources to moko-resources"
```

If there are unstaged `build.gradle.kts` changes in other modules from A.8, include them in the same commit.

---

## Phase B — AGP9 + KMP DSL Cleanup

### Task B.1: Migrate `KmpLibraryPlugin` to `com.android.kotlin.multiplatform.library`

**Files:**
- Modify: `build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt`

- [ ] **Step 1: Rewrite the plugin**

```kotlin
package plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
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
                apply(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
            }

            kotlinMultiplatformConfig {
                jvmToolchain(JAVA_VERSION)

                androidLibrary {
                    namespace = derivedNamespace(target)
                    compileSdk = libs.versions.android.compileSdk.get().toInt()
                    minSdk = libs.versions.android.minSdk.get().toInt()
                    compilations.configureEach {
                        // jvmTarget is owned by the new KMP android compilation; default = 17 from toolchain
                    }
                }

                jvm()

                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }

            commonMainDependencies {
                implementation(libs.kotlinx.coroutines.core)
            }

            commonTestDependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
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

Notes:
- `androidLibrary { }` is exposed via the new plugin on the `KotlinMultiplatformExtension`. The old `androidConfig { android { } }` extension is no longer used here.
- Java toolchain (17) drives jvmTarget through KGP; no explicit `jvmTarget.set(...)` block is needed.

- [ ] **Step 2: Adjust `kotlinMultiplatformConfig`'s `androidLibrary` access path**

If `androidLibrary { }` cannot be resolved inside `kotlinMultiplatformConfig`, add an explicit helper or use the qualified `kotlin { androidLibrary { ... } }` form from the new plugin. Check the AGP 9 KMP plugin docs and adapt — do not paper over with explicit casts.

---

### Task B.2: Update `ProjectExtensions.kt` to new DSL types

**Files:**
- Modify: `build-logic/src/main/kotlin/extensions/ProjectExtensions.kt`

- [ ] **Step 1: Migrate `androidConfig` extension**

Current state:
```kotlin
internal fun Project.androidConfig(action: com.android.build.gradle.LibraryExtension.() -> Unit) {
    extensions.configure(com.android.build.gradle.LibraryExtension::class.java, action)
}
```

The new KMP plugin no longer registers a `LibraryExtension` — it registers a `KotlinMultiplatformAndroidLibraryExtension` accessed via the kotlin extension. Decide: either (a) delete `androidConfig` (callers move to the `androidLibrary { }` block inside `kotlinMultiplatformConfig`), or (b) reimplement it as:

```kotlin
internal fun Project.androidLibraryConfig(
    action: com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension.() -> Unit,
) {
    extensions.configure(
        org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java,
    ) {
        androidLibrary(action)
    }
}
```

Recommendation: option (a) — keep config inside the plugin itself (B.1 already does this) and **delete** `androidConfig` from `ProjectExtensions.kt`. Simpler, fewer call sites.

Keep `androidAppConfig` unchanged — `:androidApp` still uses `com.android.application` and `ApplicationExtension`.

- [ ] **Step 2: Compile build-logic**

Run: `./gradlew :build-logic:build 2>&1 | tail -20`

Expected: `BUILD SUCCESSFUL`. (If composite builds aren't built directly, instead run any Gradle task on the main build — e.g. `./gradlew help` — and watch for build-logic compilation errors.)

---

### Task B.3: Drop the standalone Kotlin Android plugin

**Files:**
- Modify: `build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt`

- [ ] **Step 1: Remove the explicit kotlin-android `apply` line**

Find the line:
```kotlin
apply(libs.plugins.kotlin.android.get().pluginId)
```

…and remove it. AGP 9, once `android.builtInKotlin=true` (the default after B.4), wires Kotlin support into `com.android.application` automatically.

Do not remove `apply(libs.plugins.compose.asProvider().get().pluginId)` and the compose-compiler line — those are still needed for `:androidApp`.

If a removal of `libs.plugins.kotlin.android` reference appears unused elsewhere, also remove the alias from `gradle/libs.versions.toml` — but only if `grep -rn "kotlin\.android" --include='*.kts' --include='*.kt' build-logic/` returns nothing else.

---

### Task B.4: Remove bypass flags from gradle.properties

**Files:**
- Modify: `gradle.properties`

- [ ] **Step 1: Strip the bypass lines + their comment**

Remove the block:
```properties
# applies com.android.library to KMP modules; the new DSL is incompatible with KMP until
# the build-logic migrates to com.android.kotlin.multiplatform.library.
android.builtInKotlin=false
android.newDsl=false
```

Keep:
- `kotlin.code.style=official`
- `kotlin.native.ignoreDisabledTargets=true`
- `kotlin.native.cacheKind=none` (separate workaround, out of scope)
- `android.useAndroidX=true`
- `android.nonTransitiveRClass=true`

---

### Task B.5: Phase B verification + commit

- [ ] **Step 1: Full verification matrix**

```bash
./gradlew --stop                        # drop daemons that cached the old DSL
./gradlew detekt
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:lintDebug
./gradlew :composeApp:jvmTest
./gradlew :composeApp:iosSimulatorArm64Test
./gradlew :feature-guides:impl:jvmTest
```

Expected: `BUILD SUCCESSFUL` for each.

- [ ] **Step 2: Commit**

```bash
git add gradle.properties build-logic
git commit -m "build: migrate KMP modules to com.android.kotlin.multiplatform.library (AGP 9)"
```

---

## Phase C — Docs + Detekt Baseline Refresh

### Task C.1: Update `mobile-roadmap.mdc`

**Files:**
- Modify: `.claude/rules/mobile-roadmap.mdc`

- [ ] **Step 1: Move completed items into the "Completed" list**

In the "Migration Order" / "Remaining work" sections:
- Mark **moko-resources migration** done with today's date (2026-05-19), one-line summary.
- Mark **AGP 9 + KMP DSL cleanup** done with today's date, one-line summary.
- Keep only the **iOS Kotlin/Native cache restore** item as remaining work.

Update the "Implication for Today" section: drop the line "`Res.string.*` references still work" and replace with "`MR.strings.*`, `MR.files.*`, `MR.fonts.*` via moko-resources". Drop the line "Suggestions involving `MR.strings`...are out of scope".

---

### Task C.2: Update `mobile-resources.mdc` and other rules that reference Compose Resources

**Files:**
- Modify: `.claude/rules/mobile-resources.mdc`
- Modify: `.claude/rules/mobile-overview.mdc` (if it still describes `composeResources/`)

- [ ] **Step 1: Replace Compose Resources guidance with moko-resources guidance**

Cover:
- Asset layout: `:common-resources/src/commonMain/moko-resources/base/{strings.xml, files/, fonts/}`
- API: `MR.strings.<name>`, `MR.files.<name>.readText()`, `MR.fonts.<Family>.<style>`
- `stringResource(MR.strings.x)` imports come from `dev.icerock.moko.resources.compose`
- File naming constraints: moko requires identifier-safe basenames (`constant_heroes.json`, `NotoSans-Regular.ttf`)
- Where the plugin is applied: only `:common-resources`

---

### Task C.3: Update `CLAUDE.md`

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: Update the Stack / Architecture / Key Gotchas sections**

- In **Stack**: "Resources: moko-resources 0.24.5 in `:common-resources` (generated `MR` in package `dev.nonoxy.d2buildhelper.common.resources`)" — replace the Compose Resources mention.
- In **Architecture**: in the `:common-resources` row of the modules table, change "KMP + Compose Resources" → "KMP + moko-resources".
- In **Key Gotchas**:
  - Drop the "AGP 9 + KMP via `com.android.library` requires legacy DSL flags..." gotcha. Replace with: "AGP 9 KMP support comes from `com.android.kotlin.multiplatform.library` (applied by the `kmp-library` convention plugin)."
  - Add: "All resources go through `:common-resources/src/commonMain/moko-resources/base/`. JSON / font basenames must be identifier-safe (`constant_heroes.json`, `NotoSans-Regular.ttf`)."

---

### Task C.4: Regenerate detekt baseline

- [ ] **Step 1: Re-run baseline generation**

```bash
./gradlew detektBaseline 2>&1 | tail -30
./gradlew detekt 2>&1 | tail -30
```

Expected: detekt passes. Diff `linters/detekt/baseline.xml` — any entries that disappeared are fine; new entries warrant a closer look (do not add ad-hoc suppressions for issues created by this PR).

- [ ] **Step 2: Commit docs + baseline**

```bash
git add CLAUDE.md .claude/rules linters/detekt/baseline.xml
git commit -m "docs: mark moko + AGP9 follow-ups complete; refresh detekt baseline"
```

---

### Task C.5: Push and update PR #4

- [ ] **Step 1: Push**

```bash
git push origin refactor/multi-module-and-moko-resources
```

- [ ] **Step 2: Update PR description**

Use `gh pr edit 4 --body "..."` to extend the existing PR body with two new sections:
- **moko-resources migration** — bullet list of touched modules + new asset layout.
- **AGP 9 DSL cleanup** — bullet list summarizing the plugin swap and dropped flags.

Keep the existing Test Plan checkboxes; append "moko strings appear correctly on Android, Desktop, and iOS" and "iOS framework builds with the new KMP Android library plugin".

- [ ] **Step 3: Confirm CI**

```bash
gh pr checks 4
```

All checks pass (`Detekt`, `Android Lint`, `SwiftLint + SwiftFormat`) — or wait until they do before declaring done.

---

## Test Plan

After the entire PR (Phase A + B + C) lands:

- [ ] `./gradlew detekt` — green.
- [ ] `./gradlew :androidApp:assembleDebug` — APK builds.
- [ ] `./gradlew :androidApp:lintDebug` — lint clean.
- [ ] `./gradlew :composeApp:jvmTest` — green.
- [ ] `./gradlew :composeApp:iosSimulatorArm64Test` — green.
- [ ] `./gradlew :feature-guides:impl:jvmTest` — green.
- [ ] Android APK installs and launches; "All heroes", "Hero filter", "Retry", error message render correctly.
- [ ] `./gradlew :composeApp:run` — desktop launches; window title is `app_name`; NotoSans font visible.
- [ ] iOS simulator build runs from Xcode; strings render; fonts visible.
- [ ] No `Res.*` references remain (`grep` returns empty for source, build/ caches excluded).
- [ ] `android.builtInKotlin` / `android.newDsl` no longer appear in `gradle.properties`.
