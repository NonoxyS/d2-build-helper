# Multi-Module Split + moko-resources Migration — Design

**Branch:** `refactor/multi-module-and-moko-resources` (off `develop-cmp`)
**PR target:** `develop-cmp`
**Status:** Spec — awaiting plan
**Date:** 2026-05-18

## Context

The project currently lives in a single `:composeApp` Gradle module. Two roadmap items remain after the MVIKotlin migration (`develop-cmp@d16954d`):

1. **Multi-module split** along the KMMTemplate playbook — per-feature `{api, impl, presentation, ui}` + shared `core-*` / `common*` modules + a separate Android-only application module. Resolves the AGP 9 + KMP `com.android.application` deprecation that today is masked by `android.builtInKotlin=false` and `android.newDsl=false`.
2. **moko-resources migration** — replace `composeResources/values/strings.xml` and `composeResources/files/constants/*.json` with moko-resources `MR.strings.*` / `MR.files.*` in a dedicated resources module.

Both are bundled into one branch and one PR (user decision; flagged as risky, accepted).

## Goals

- 15 modules organised per KMMTemplate playbook, with strict dependency rules.
- All resources (strings + JSON constants) consolidated in one `:common-resources` module via moko-resources.
- AGP `com.android.application` plugin isolated in an Android-only `:androidApp` module, allowing removal of legacy compatibility flags from `gradle.properties`.
- No behaviour changes — same Compose Multiplatform app behaves identically before/after on Android, JVM/Desktop, and iOS.
- Each migration phase compiles and passes detekt + Android lint + JVM tests + iOS simulator tests in isolation (checkpointable via git bisect).

## Non-goals

- Implementing the empty `DetailGuide` feature stub.
- Adding new features, tests beyond regression coverage, or refactors unrelated to module boundaries.
- Renaming `:composeApp` itself (kept as the KMP shell name to minimise iOS Xcode project churn).
- Localisation expansion (only English/Russian strings already present; structure supports adding locales later through moko-resources).

## Module Graph

15 modules total:

```
:androidApp                          [com.android.application — Android entry point]
:composeApp                          [KMP shell — App.kt + NavHost + theme + jvmMain entry + iOS framework]

:common                              [coRunCatching, ResultExtensions, OneTimeEvent, TimeConverter,
                                       Mapper<I,O>, CoroutineDispatchers + Impl]
:common-ui                           [LocalImageLoader, shared compose helpers]
:common-resources                    [moko-resources only — strings.xml + JSON files; no Kotlin code]

:core-domain                         [Hero, Item, Ability, ImageResources — app-wide pure models]
:core-navigation                     [AppScreens sealed routes, LocalNavHost]
:core-network                        [Apollo client + GraphQL queries + Stratz schema, Ktor expect/actual engines]
:core-presentation                   [BaseViewModel<S,L>, BaseExecutor, BaseIosViewModel, CoreMVIKotlinModule]
:core-storage                        [Supabase client]
:core-resources                      [ResourcesRepository + Impl + datasources — reads MR.files for JSON,
                                       builds Supabase URLs for icons]

:feature-guides:api                  [GuidesStore + Intent + State + Label + Guide domain model]
:feature-guides:impl                 [GuidesStoreFactory, Executor, Reducer, FeatureGuidesImplModule (Koin),
                                       GuidesRepository(Impl), GuidesApi/DataSource + DTO/mappers]
:feature-guides:presentation        [GuidesViewModel, UiGuidesState, UiGuidesLabel, mappers]
:feature-guides:ui                   [GuidesScreen + sub-views]
```

### Dependency rules (architectural invariants)

1. **`:feature-A:impl` does not depend on `:feature-B:*`.** Cross-feature reuse hoists to a `:core-*` module.
2. **`:feature-X:api` exports only contracts** — Store interface, Intent/State/Label, public domain models. Depends only on `:core-domain` and Kotlin stdlib.
3. **`:feature-X:impl` depends on its own `:api` + `:core-*` modules.** No cross-feature impl-to-impl wiring.
4. **`:feature-X:presentation` depends on its `:api` + `:core-presentation`, not on `:impl`.** Store instances arrive via Koin.
5. **`:feature-X:ui` depends on its `:presentation` + `:common-ui` + `:common-resources`** (for `MR.strings.*`).
6. **`:core-*` modules are single (no api/impl split).** Hygiene at runtime is enforced by Koin (consumers see interfaces, never impl classes). api/impl split is reserved for feature boundaries where plug-and-play matters.
7. **`:composeApp` (shell) is the only Composition Root.** It depends on all `:*:impl` modules and aggregates Koin modules in `initKoin()`. No other module composes the DI graph.

### Top-level dependency arrows

```
:androidApp ──▶ :composeApp

:composeApp ──▶ :common-ui, :common-resources, :core-navigation, :core-presentation,
                :feature-guides:ui, :feature-guides:presentation,
                :feature-guides:impl, :core-resources
                (impl modules are pulled only to wire Koin in initKoin)

:feature-guides:ui          ──▶ :feature-guides:presentation, :common-ui, :common-resources
:feature-guides:presentation ──▶ :feature-guides:api, :core-presentation, :common
:feature-guides:impl        ──▶ :feature-guides:api, :core-domain, :core-network, :core-storage,
                                 :core-resources, :common
:feature-guides:api         ──▶ :core-domain

:core-resources             ──▶ :core-domain, :core-network, :core-storage, :common, :common-resources
:core-presentation          ──▶ :common (+ MVIKotlin, moko-mvvm, Napier libs)
:core-navigation            ──▶ compose-navigation (no internal deps)
:common-ui                  ──▶ :core-navigation, Coil libs
:core-network               ──▶ apollo runtime, ktor (engine per-platform)
:core-storage               ──▶ supabase storage
:core-domain                ──▶ (none — pure Kotlin)
:common                     ──▶ (none — kotlinx.coroutines core only)
:common-resources           ──▶ (none — resources only)
```

## Convention Plugins (`build-logic`)

| Plugin id | Applied to | Responsibilities |
|---|---|---|
| `kmp-library` (new) | All KMP modules (shared + feature non-Android-only) | Apply `kotlin("multiplatform")` + `com.android.library`. Java 17 toolchain. Targets: `androidTarget()`, `jvm()`, `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`. Android namespace derived from `project.path` → `dev.nonoxy.d2buildhelper.<path-segments>`. minSdk/compileSdk/targetSdk from `libs.versions.toml`. Default `commonMain` dep: `kotlinx-coroutines-core`. Default `commonTest` deps: `kotlin-test`, `kotlinx-coroutines-test`. |
| `compose-multiplatform-setup` (new) | Modules with `@Composable` code: `:common-ui`, `:feature-guides:ui`, `:composeApp`. Not applied to `:feature-guides:presentation` (UI models + mappers + VM are pure Kotlin/Flow) or `:core-presentation` (BaseViewModel is Flow-based, no Compose dep). | Apply `org.jetbrains.compose` + `org.jetbrains.kotlin.plugin.compose`. Add `compose.runtime`, `compose.foundation`, `compose.ui`, `compose.materialIconsExtended` to `commonMain`. Add `compose.ui.tooling` / `compose.ui.tooling.preview` to `androidMain`. Sits on top of `kmp-library`. |
| `android-application-setup` (new) | `:androidApp` only | Apply `com.android.application` + `kotlin("android")` + `org.jetbrains.kotlin.plugin.compose`. Java 17. compileSdk/minSdk/targetSdk from version catalog. `buildFeatures.compose = true`. `applicationId = "dev.nonoxy.d2buildhelper.androidApp"`. `versionCode`/`versionName` set inline in `:androidApp/build.gradle.kts` via existing `AppVersion` helper. |
| `json-serialization` (existing — unchanged) | `:core-resources` | Applies `kotlinx-serialization` plugin and adds `kotlinx-serialization-json` dependency. Auto-detects KMP vs Android-only via existing logic. |

**Apollo/buildConfig wiring after split:**
- `:core-network/build.gradle.kts` applies `com.apollographql.apollo` and `com.github.gmazzo.buildconfig`. Apollo service `api` keeps `packageName = "dev.nonoxy.d2buildhelper.graphql"`. buildConfig generates `STRATZ_API_KEY`, `API_BASE_URL` in package `dev.nonoxy.d2buildhelper.core.network`.
- `:core-storage/build.gradle.kts` applies `com.github.gmazzo.buildconfig`. Generates `SUPABASE_BASE_URL`, `SUPABASE_API_KEY`, `STORAGE_HERO_ICONS_FOLDER_URL`, `STORAGE_ITEM_ICONS_FOLDER_URL`, `STORAGE_ABILITY_ICONS_FOLDER_URL`, `STORAGE_ADDITIONAL_ICONS_FOLDER_URL` in package `dev.nonoxy.d2buildhelper.core.storage`.
- Both read `local.properties` via `rootProject.file("local.properties")` — same pattern as today, just relocated.

## DI Strategy

Koin module factories, one per `:*:impl` (and per relevant `:core-*` for infrastructure):

| Module fun | Lives in | Registers |
|---|---|---|
| `commonModule()` | `:common` | `CoroutineDispatchers` → `CoroutineDispatchersImpl` |
| `coreNetworkModule()` | `:core-network` | `ApolloClient` singleton (interceptor with `BuildConfig.STRATZ_API_KEY`); per-platform `HttpClient` via expect/actual factory |
| `coreStorageModule()` | `:core-storage` | Supabase client singleton |
| `coreMVIKotlinModule()` | `:core-presentation` | `StoreFactory` → `LoggingStoreFactory(DefaultStoreFactory)` with Napier logger sink |
| `coreResourcesModule()` | `:core-resources` | `ResourcesRepository` → `ResourcesRepositoryImpl`, `ConstantResourcesDataSource`, `ImageResourcesApi`, `ImageResourcesDataSource` |
| `featureGuidesImplModule()` | `:feature-guides:impl` | `GuidesRepository` → `GuidesRepositoryImpl`, `GuidesApi`, `GuidesDataSource`, `GuidesStoreFactory`, factory for `GuidesStore` via `GuidesStoreFactory.create()`, `viewModelOf(::GuidesViewModel)` |

`initKoin(appDeclaration: KoinAppDeclaration? = null)` lives in `:composeApp/.../core/di/Koin.kt` and aggregates:

```kotlin
val allModules = listOf(
    commonModule(),
    coreMVIKotlinModule(),
    coreNetworkModule(),
    coreStorageModule(),
    coreResourcesModule(),
    featureGuidesImplModule(),
)
```

`AppModule.kt` is removed — it was the legacy aggregator and is replaced by `allModules` composition. ViewModel resolution in Compose remains `koinViewModel<GuidesViewModel>()`.

## moko-resources Strategy

`:common-resources` is the single point of resource ownership.

- Build script: `multiplatformResources { resourcesPackage.set("dev.nonoxy.d2buildhelper.common.resources"); resourcesClassName.set("MR") }`.
- Contents:
  - `src/commonMain/moko-resources/base/strings.xml` — `all_heroes`, `hero_filter`, `error_loading`, `retry`, `app_name`.
  - `src/commonMain/moko-resources/base/files/constant_heroes.json`, `constant_items.json`, `constant_abilities.json` (renamed from `heroes.json` etc. to satisfy moko naming).

**Replacements:**

```kotlin
// before
import dev.nonoxy.d2buildhelper.composeResources.Res
import dev.nonoxy.d2buildhelper.composeResources.all_heroes
import org.jetbrains.compose.resources.stringResource
Text(stringResource(Res.string.all_heroes))

// after
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
Text(stringResource(MR.strings.all_heroes))
```

```kotlin
// before (ConstantResourcesDataSource)
val bytes = Res.readBytes("files/constants/heroes.json")

// after
val text = MR.files.constant_heroes.readText()
// json.decodeFromString<List<HeroDto>>(text)
```

**Removed after migration:**
- `composeApp/src/commonMain/composeResources/` directory.
- `implementation(libs.compose.resources)` from all modules — no `Res.*` references remain.
- `implementation(libs.compose.ui.tooling.preview)` stays only in modules with `@Preview` annotations (current usage: `HeroFilterDialogView` — moves to `:feature-guides:ui`).

**Version constraint:** `dev.icerock.moko:moko-resources:0.24.x` (latest stable supporting Compose Multiplatform 1.10.x). Pinned version goes into `libs.versions.toml`.

## Migration Phases (one branch, ~5 commits)

Each phase ends green: `./gradlew detekt :composeApp:jvmTest :androidApp:lintDebug :composeApp:iosSimulatorArm64Test` (paths adjust per phase).

### Phase 0 — build-logic

Single commit.

- Add `kmp-library`, `compose-multiplatform-setup`, `android-application-setup` plugins under `build-logic/src/main/kotlin/plugins/`.
- Register them in `build-logic/build.gradle.kts` `gradlePlugin { plugins { ... } }`.
- Existing `json-serialization` plugin unchanged.
- `:composeApp` not modified — convention plugins are registered but unused.
- Verify: `./gradlew build` still succeeds as before.

### Phase 1 — Shared modules

One or two commits depending on diff size.

- Create modules bottom-up: `:common`, `:core-domain`, `:core-navigation`, `:core-presentation`, `:common-ui`, `:core-network`, `:core-storage`, `:common-resources`, `:core-resources`.
- Apply convention plugins (`kmp-library`, `compose-multiplatform-setup` where Compose code exists, `json-serialization` on `:core-resources`).
- Apply moko-resources plugin in `:common-resources`. Migrate `strings.xml` + JSON files into `src/commonMain/moko-resources/base/`. Rename JSON files to satisfy moko naming (`heroes.json` → `constant_heroes.json`, etc.).
- Move code from `:composeApp/.../core/`, `common/`, `navigation/`, `base/` into the new modules per the package mapping below.
- Apollo plugin moves to `:core-network/build.gradle.kts`. buildConfig moves to `:core-network` (Stratz) and `:core-storage` (Supabase). Generated `BuildConfig` symbols in code switch imports to new packages.
- Inside `:composeApp`, feature Guides code stays for now but imports rewrite to point at new shared modules.
- `Res.string.*` → `MR.strings.*` rewrites in Guides UI code (still in `:composeApp`).
- `Res.readBytes(...)` → `MR.files.*.readText()` in `ConstantResourcesDataSource` (already in `:core-resources`).
- Delete `composeApp/src/commonMain/composeResources/` directory.
- Update `settings.gradle.kts` with new `include(...)` lines.
- Verify: detekt + lint + JVM tests + iOS simulator test green.

### Phase 2 — Guides feature split

Single commit.

- Create `:feature-guides:api`, `:feature-guides:impl`, `:feature-guides:presentation`, `:feature-guides:ui`.
- Move code from `:composeApp/features/guides/*`:
  - `Guide` domain model, `GuidesStore` (interface + Intent + State + Label) → `:feature-guides:api`.
  - `GuidesStoreFactory`, `GuidesExecutor`, `GuidesReducer`, `FeatureGuidesImplModule`, `GuidesRepository(Impl)`, `GuidesApi/DataSource`, DTO + mappers → `:feature-guides:impl`.
  - `GuidesViewModel`, `UiGuidesState`, `UiGuidesLabel`, `UiGuidesStateMapper`, `UiGuidesLabelMapper` → `:feature-guides:presentation`.
  - `GuidesScreen` + `GuidesView` + all `views/*` → `:feature-guides:ui`.
- `:composeApp` depends on all four guide submodules.
- `AppModule.kt` removed; `Koin.kt` uses the new `allModules` list.
- Verify: full build green on three targets, app behaves the same in run.

### Phase 3 — Android application split

Single commit.

- Create `:androidApp` module with `android-application-setup` plugin applied.
- Move `composeApp/src/androidMain/kotlin/.../AndroidApp.kt`, `MainActivity.kt`, `composeApp/src/androidMain/AndroidManifest.xml`, `composeApp/src/androidMain/res/` → `:androidApp/src/main/`.
- `:composeApp` drops `com.android.application` plugin, switches to `kmp-library` (the `com.android.library` part of kmp-library plugin handles the Android library target).
- `:composeApp/build.gradle.kts` retains the desktop entry block: `compose.desktop { application { mainClass = "MainKt"; ... } }` and the iOS framework block.
- Remove `android.builtInKotlin=false` and `android.newDsl=false` from `gradle.properties` (no longer needed once `com.android.application` is isolated).
- Update `iosApp/iosApp.xcodeproj` framework path if needed (Xcode references `Pods-iosApp` or framework search paths — verify and adjust).
- Verify: `./gradlew :androidApp:assembleDebug`, `./gradlew :composeApp:run` (desktop), `./gradlew :composeApp:iosSimulatorArm64Test` (iOS) all succeed. `./gradlew :androidApp:lintDebug` succeeds.

### Phase 4 — Documentation + CI + cleanup

Single commit.

- Rewrite `CLAUDE.md` Architecture section to describe 15 modules. Update Build Commands (`:androidApp:assembleDebug`, `:androidApp:lintDebug`). Update Stack ("Multi-module project, 15 modules"). Add moko-resources version. Update Key Gotchas (remove `android.builtInKotlin=false` note; replace composeResources note with moko + `:common-resources`; add cross-feature dependency rule).
- Rewrite `.claude/rules/mobile-overview.mdc`, `mobile-architecture.mdc`, `mobile-data-layer.mdc`, `mobile-network.mdc`, `mobile-resources.mdc`, `mobile-compose.mdc`, `mobile-error-handling.mdc` for new structure. Mark `[x] Multi-module split` and `[x] moko-resources migration` in `mobile-roadmap.mdc`.
- Update `.github/workflows/*.yml`: Detekt task targets `./gradlew detekt` (multi-module aware via root config), Android lint target switches from `:composeApp:lintDebug` to `:androidApp:lintDebug`.
- Regenerate `linters/detekt/baseline.xml` via `./gradlew detektBaseline`.
- Final verify: all CI checks green on PR.

## Package Mapping

Files migrate from `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/` to new modules:

| Old location | New module | New package root |
|---|---|---|
| `base/LocalImageLoader.kt` | `:common-ui` | `dev.nonoxy.d2buildhelper.common.ui` |
| `common/coroutines/*` | `:common` | `dev.nonoxy.d2buildhelper.common.coroutines` |
| `common/extensions/*` | `:common` | `dev.nonoxy.d2buildhelper.common.extensions` |
| `common/mappers/Mapper.kt` | `:common` | `dev.nonoxy.d2buildhelper.common.mappers` |
| `common/utils/*` | `:common` | `dev.nonoxy.d2buildhelper.common.utils` |
| `core/data/api/guides/*` | `:feature-guides:impl` | `dev.nonoxy.d2buildhelper.feature.guides.impl.data.api` |
| `core/data/api/resources/image/*` | `:core-resources` | `dev.nonoxy.d2buildhelper.core.resources.data.api.image` |
| `core/data/local/resources/constants/*` | `:core-resources` | `dev.nonoxy.d2buildhelper.core.resources.data.local.constants` |
| `core/data/repository/guides/*` | `:feature-guides:impl` | `dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository` |
| `core/data/repository/resources/*` | `:core-resources` | `dev.nonoxy.d2buildhelper.core.resources.data.repository` |
| `core/di/AppModule.kt` | *removed* | — (replaced by `allModules` list in `:composeApp/.../Koin.kt`) |
| `core/di/Koin.kt` | `:composeApp` | `dev.nonoxy.d2buildhelper.app.di` |
| `core/graphql/*` (generated) | `:core-network` | `dev.nonoxy.d2buildhelper.graphql` (Apollo generates here) |
| `core/mvikotlin/BaseExecutor.kt` | `:core-presentation` | `dev.nonoxy.d2buildhelper.core.presentation.mvikotlin` |
| `core/mvikotlin/di/CoreMVIKotlinModule.kt` | `:core-presentation` | `dev.nonoxy.d2buildhelper.core.presentation.di` |
| `core/presentation/viewmodel/BaseViewModel.kt` | `:core-presentation` | `dev.nonoxy.d2buildhelper.core.presentation.viewmodel` |
| `core/presentation/viewmodel/BaseIosViewModel.kt` | `:core-presentation` | `dev.nonoxy.d2buildhelper.core.presentation.viewmodel` |
| `features/guides/api/store/GuidesStore.kt` | `:feature-guides:api` | `dev.nonoxy.d2buildhelper.feature.guides.api.store` |
| `features/guides/domain/models/Guide.kt` | `:feature-guides:api` | `dev.nonoxy.d2buildhelper.feature.guides.api.domain` |
| `features/guides/domain/models/{Hero,Item,Ability,ImageResources}.kt` | `:core-domain` | `dev.nonoxy.d2buildhelper.core.domain` |
| `features/guides/impl/*` | `:feature-guides:impl` | `dev.nonoxy.d2buildhelper.feature.guides.impl.{di,domain}` |
| `features/guides/presentation/{models,mappers,*ViewModel.kt}` | `:feature-guides:presentation` | `dev.nonoxy.d2buildhelper.feature.guides.presentation` |
| `features/guides/presentation/ui/*` | `:feature-guides:ui` | `dev.nonoxy.d2buildhelper.feature.guides.ui` |
| `navigation/AppScreens.kt` | `:core-navigation` | `dev.nonoxy.d2buildhelper.core.navigation` |
| `navigation/LocalNavHost.kt` | `:core-navigation` | `dev.nonoxy.d2buildhelper.core.navigation` |
| `theme/*` | `:composeApp` | `dev.nonoxy.d2buildhelper.app.theme` |
| `App.kt` | `:composeApp` | `dev.nonoxy.d2buildhelper.app` |

Android-specific entry files:

| Old location | New module |
|---|---|
| `composeApp/src/androidMain/kotlin/.../AndroidApp.kt` | `:androidApp/src/main/kotlin/` |
| `composeApp/src/androidMain/kotlin/.../MainActivity.kt` | `:androidApp/src/main/kotlin/` |
| `composeApp/src/androidMain/AndroidManifest.xml` | `:androidApp/src/main/AndroidManifest.xml` |
| `composeApp/src/androidMain/res/` | `:androidApp/src/main/res/` |

Desktop and iOS entries remain in `:composeApp`:

- `composeApp/src/jvmMain/kotlin/main.kt` — unchanged location.
- `composeApp/src/iosMain/kotlin/.../MainViewController.kt` — unchanged location.

## Risks

1. **iOS framework path.** `iosApp/iosApp.xcodeproj` references the `ComposeApp.framework` produced by `:composeApp`. If the framework's umbrella exports need adjustment (Kotlin/Native may need explicit `export(project(...))` for transitively-used types), Phase 3 picks this up.
2. **Multi-module buildConfig.** `local.properties` is read from `rootProject.file("local.properties")` in both `:core-network` and `:core-storage`. Standard pattern; the only failure mode is missing keys, caught by existing `require(...)` calls that move to the respective modules.
3. **Apollo schema package.** `packageName = "dev.nonoxy.d2buildhelper.graphql"` stays the same; generated symbols in `:core-network` are accessible to `:feature-guides:impl` via build dependency on `:core-network`.
4. **moko-resources + Compose Multiplatform 1.10 compatibility.** Need to pin a moko-resources version that explicitly supports CMP 1.10.1 and Kotlin 2.3.10. To verify when adding to version catalog.
5. **JSON file rename.** moko-resources requires lowercase-snake-case resource names matching `[a-z][a-z0-9_]*`. Current `heroes.json`/`items.json`/`abilities.json` become `constant_heroes.json`/`constant_items.json`/`constant_abilities.json`. `ConstantResourcesDataSource` updates its file-key references to match the new MR.files names.
6. **CI memory.** Gradle daemon JVM args (`-Xmx4g`) may need a small bump for 15-module parallel builds on free-tier GitHub Actions. Monitor first PR CI run; adjust if OOMs surface.
7. **Detekt baseline regeneration.** The baseline references file paths and signatures in `:composeApp`. After split, all entries become stale. Plan: delete the old baseline, regenerate via `./gradlew detektBaseline` at the end of Phase 4, commit fresh baseline.

## Testing Strategy

Existing tests survive the move:
- `composeApp/src/commonTest/kotlin/.../core/data/repository/guides/GuidesRepositoryTest.kt` → `:feature-guides:impl/src/commonTest/...`.
- `composeApp/src/commonTest/kotlin/.../features/guides/impl/domain/GuidesExecutorTest.kt` → `:feature-guides:impl/src/commonTest/...`.

No new tests added in this PR (per scope rules — no new behaviour). After the migration, future PRs add per-module test coverage.

## Verification Per Phase

| Phase | Required green checks |
|---|---|
| 0 | `./gradlew build` (sanity — convention plugins compile) |
| 1 | `./gradlew detekt :composeApp:jvmTest :composeApp:iosSimulatorArm64Test :composeApp:lintDebug` |
| 2 | Same as 1 (run from root, all modules covered) |
| 3 | `./gradlew detekt :androidApp:assembleDebug :androidApp:lintDebug :composeApp:run :composeApp:jvmTest :composeApp:iosSimulatorArm64Test` |
| 4 | All Phase 3 checks + CI workflow on the PR returns green |

## Rollback Considerations

Phases 0–2 do not change behaviour and can be reverted independently as commits.

Phase 3 mutates `gradle.properties` (removes flags), Xcode project, and the Android plugin location. A revert here may need to:
- Restore `android.builtInKotlin=false` and `android.newDsl=false` in `gradle.properties`.
- Revert `:androidApp` module deletion.
- Re-add `com.android.application` plugin to `:composeApp`.

The user-facing app behaviour does not change in any phase; rollback risk is build-system only.

## AGP 9 + KMP DSL Cleanup (Follow-up Phase)

Added 2026-05-19. Closes the temporary workaround introduced when AGP 9.0 shipped without a working KMP path for `com.android.library`.

### Goal

Drop the legacy DSL bypass flags and migrate `:core-*` / `:feature-*` modules to AGP 9's first-class KMP plugin `com.android.kotlin.multiplatform.library`.

### Files

- `gradle.properties` — remove:
  - `android.builtInKotlin=false`
  - `android.newDsl=false`
  - explanatory comment that justified them.
- `build-logic/src/main/kotlin/plugins/KmpLibraryPlugin.kt` — replace `apply(libs.plugins.android.library.get().pluginId)` with `apply(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)`; remove the explicit `androidTarget { compilerOptions { ... } }` block (the new plugin owns the android compilation).
- `build-logic/src/main/kotlin/plugins/AndroidApplicationSetupPlugin.kt` — drop the explicit `apply(libs.plugins.kotlin.android.get().pluginId)`. AGP 9 + the application plugin pull in Kotlin automatically once `android.builtInKotlin` is no longer disabled.
- `build-logic/src/main/kotlin/extensions/ProjectExtensions.kt` — change `androidConfig` from `LibraryExtension` (com.android.build.gradle) to `KotlinMultiplatformAndroidLibraryExtension` (`com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension`). Confirm `androidAppConfig` keeps using `ApplicationExtension` from `com.android.build.api.dsl`; if a deprecation surfaces, follow the upgrade hint.

### Expected DSL shape after migration

```kotlin
// KmpLibraryPlugin.kt
kotlinMultiplatformConfig {
    jvmToolchain(JAVA_VERSION)

    androidLibrary {
        namespace = derivedNamespace(target)
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilations.configureEach { kotlinSourceSets.configureEach { /* shared compilerOptions if needed */ } }
    }
    jvm()
    iosX64(); iosArm64(); iosSimulatorArm64()
}
```

(`androidLibrary { ... }` is a KMP-level DSL block exposed by the new plugin — replaces the project-level `androidConfig { }` extension.)

### Verification

Same matrix as the multi-module migration:

```
./gradlew detekt :androidApp:assembleDebug :androidApp:lintDebug \
  :composeApp:jvmTest :composeApp:iosSimulatorArm64Test :feature-guides:impl:jvmTest
```

iOS simulator test pass is the load-bearing check — the previous reason for the bypass was that `com.android.library` produced an Android-only configuration that confused the KMP iOS compilation.

### Risks

- 12 KMP modules switch their Android plugin at once. A bug in `KmpLibraryPlugin` blasts everything.
- `compileSdk`/`minSdk` plumbing moves from `defaultConfig { }` (legacy library DSL) to top-level `androidLibrary { }` (new KMP DSL). Property names may differ — confirm against AGP 9 release notes.
- `:core-network` and `:core-storage` declare custom `buildConfigField(...)`. The new DSL exposes `buildConfig { defaults { ... } }` (or similar). Migrate these inline; keep them functional.
- Detekt baseline may regenerate slightly because of moved/renamed plugin classes.

### Rollback

If the migration fails, restore the two `android.*` flags and revert `KmpLibraryPlugin.kt` / `ProjectExtensions.kt`. No code outside `build-logic/` and `gradle.properties` should be touched by this phase, which keeps rollback contained.

### Out of Scope (for this phase)

- Removing `kotlin.native.cacheKind=none`. Unrelated workaround for Supabase storage-kt; revisit when SDKs move.

## Out of Scope

- `DetailGuide` feature implementation.
- Multi-locale translation files.
- Per-module unit test backfill (future PRs).
- Convention plugin for moko-resources (only one module uses it; inline application is sufficient).
- Renaming the project root or Gradle root project name.
