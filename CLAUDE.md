# d2-build-helper

Dota 2 build helper — Compose Multiplatform app (Android, Desktop/JVM, iOS) that surfaces hero builds and stats from high-rating matches. Data sources: Stratz GraphQL API (via Apollo) and Supabase Storage (for icons). Local JSON for hero/item/ability constants.

## Build Commands

```shell
./gradlew :androidApp:assembleDebug         # Android debug APK
./gradlew :composeApp:run                    # Desktop (JVM)
./gradlew :composeApp:iosSimulatorArm64Test
./gradlew :composeApp:jvmTest
./gradlew :feature-guides:impl:jvmTest
./gradlew :androidApp:lintDebug              # Android Lint
./gradlew detekt                              # Static analysis (Kotlin, all modules)
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode.

## Stack

- Kotlin 2.3.10, Compose Multiplatform 1.10.1 (material3 1.9.0), AGP 9.0.0, Java target 17.
- Gradle 9.4.1.
- Apollo Kotlin 4.3.1 (Stratz GraphQL).
- Supabase 3.1.0 (storage only — for hero/item/ability icons).
- Ktor 3.3.3 (OkHttp on Android+JVM, Darwin on iOS).
- Coil 3.2.0 (image loading).
- Coroutines 1.10.2, kotlinx.serialization 1.10.0.
- Android: compileSdk/targetSdk 36, minSdk 26.
- Multi-module project (16 modules) with `build-logic` composite build hosting `kmp-library`, `kmp-feature-setup`, `compose-multiplatform-setup`, `android-application-setup`, and `json-serialization` convention plugins. `kmp-feature-setup` auto-wires per-submodule dependencies by name (`api`/`impl`/`presentation`/`ui`) — see `mobile-architecture.mdc#build-conventions`.
- DI: Koin 4.1.x. Per-module Koin module functions; `:composeApp`'s `core/di/AppModule.kt` only aggregates includes.
- MVIKotlin 4.4.0 (BaseExecutor + `coreMVIKotlinModule` in `:core-mvikotlin`, LoggingStoreFactory wired through Napier).
- Napier 2.7.1 (logger; `Napier.base(DebugAntilog(...))` on platform entry).
- moko-mvvm 0.16.1 (CFlow/CStateFlow for iOS contract on `BaseViewModel`).
- State management: MVIKotlin `Store` per feature + `BaseViewModel<State, Label>` (`bindAndStart` binds `store.states`/`store.labels` through mappers).
- Resources: moko-resources 0.26.1 in `:common-resources` (generated `MR` in package `dev.nonoxy.d2buildhelper.common.resources`). Assets under `src/commonMain/moko-resources/{base, files, fonts}/`.
- Navigation: `compose-navigation` 2.9.2 + `AppScreens` sealed routes + `LocalNavHost` CompositionLocal (in `:core-navigation`).

API keys come from `local.properties` (`SUPABASE_API_KEY`, `STRATZ_API_KEY`). `SUPABASE_*` is read by `:core-storage/build.gradle.kts`, `STRATZ_*` by `:core-network/build.gradle.kts`.

## Architecture

16 modules. Conventions: `:feature-*` modules nest their layers (`:feature-X:api`/`impl`/`presentation`/`ui`); `:core-*` and `:common*` are single. KMP layout per module: `src/{commonMain,commonTest,androidMain,jvmMain,iosMain}/kotlin/...`.

| Module | Type | Responsibility |
|---|---|---|
| `:androidApp` | Android-only | `com.android.application` entry. `AndroidApp`, `AppActivity`, manifest. Package `dev.nonoxy.d2buildhelper.android`. |
| `:composeApp` | KMP shell | `App.kt`, NavHost wiring, `jvmMain/main.kt` desktop entry, `iosMain/main.kt` iOS framework entry. Composition Root for Koin (`initKoin`). |
| `:common` | KMP | `coRunCatching`, `ResultExtensions`, `OneTimeEvent`, `TimeConverter`, `Mapper`, `CoroutineDispatchers`, `commonModule` Koin. |
| `:common-ui` | KMP + Compose | `LocalImageLoader`, `D2BuildHelperTheme` (+ platform `SystemAppearance` actuals). |
| `:common-resources` | KMP + moko-resources | Shared `strings.xml`, JSON constants and fonts under `moko-resources/{base, files, fonts}/`. `MR` accessor lives in package `dev.nonoxy.d2buildhelper.common.resources`. |
| `:core-domain` | KMP | App-wide pure domain models: `Hero`, `Item`, `Ability`, `ImageResources`. |
| `:core-navigation` | KMP + Compose | `AppScreens` sealed routes, `LocalNavHost`. |
| `:core-network` | KMP | `ApolloClient` + GraphQL queries + Stratz schema + buildConfig (Stratz) + per-platform Ktor engines, `coreNetworkModule` Koin. |
| `:core-mvikotlin` | KMP | `BaseExecutor`, `coreMVIKotlinModule` (binds `StoreFactory` to `LoggingStoreFactory(DefaultStoreFactory())` via Napier). Re-exports `mvikotlin-core/main/logging/coroutines`. |
| `:core-presentation` | KMP | `BaseViewModel<S,L>`, `BaseIosViewModel` (depends on `:core-mvikotlin` for `Store`/`BindingsBuilder`). |
| `:core-storage` | KMP | Supabase client + buildConfig (Supabase), `coreStorageModule` Koin. |
| `:core-resources` | KMP | `ResourcesRepository` + Impl + datasources (`ImageResourcesApi/DataSource`, `ConstantResourcesDataSource`), `coreResourcesModule` Koin. |
| `:feature-guides:api` | KMP | `GuidesStore` contract (Intent/State/Label) + `Guide`, `PlayerStats`, `ItemPurchase`, `MatchPlayerPosition`. |
| `:feature-guides:impl` | KMP | `GuidesStoreFactory`, `GuidesExecutor`, `GuidesReducer`, `GuidesRepository(Impl)`, `GuidesApi/DataSource` + DTO/mappers, `featureGuidesImplModule` Koin. |
| `:feature-guides:presentation` | KMP | `GuidesViewModel`, `UiGuidesState`, `UiGuidesLabel`, mapper interfaces + impls, `featureGuidesPresentationModule` Koin. |
| `:feature-guides:ui` | KMP + Compose | `GuidesScreen` + sub-views. |

### Dependency invariants

- `:feature-A:impl` never depends on `:feature-B:*`. Cross-feature reuse hoists to a `:core-*` module.
- `:feature-X:api` exports only contracts (Store interface, Intent/State/Label, public domain models). Depends only on `:core-domain` + kotlin stdlib + MVIKotlin core.
- `:feature-X:impl` depends on its own `:api` + relevant `:core-*` modules.
- `:feature-X:presentation` depends on its `:api` + `:core-presentation`, not on `:impl`.
- `:feature-X:ui` depends on its `:presentation` + `:common-ui` + `:common-resources`.
- `:core-*` modules are single (no api/impl split). Runtime hiding of impls is enforced by Koin.
- `:composeApp` is the only Composition Root. It aggregates all Koin modules via `appModule { includes(...) }` and is the sole module pulling `:*:impl` builds into the link graph.

## Rules (`.claude/rules/`)

| File | Description |
| --- | --- |
| `mobile-overview.mdc` | Stack, module layout, naming conventions |
| `mobile-architecture.mdc` | MVIKotlin Store/Executor/Reducer, `BaseViewModel<State, Label>`, Koin per-module modules, Compose Navigation, dependency invariants |
| `mobile-compose.mdc` | Recomposition optimization, composable splitting, Previews, local-mirror text inputs |
| `mobile-data-layer.mdc` | DTO / domain split, `suspend fun (): Result<T>` repositories, mappers, repository cache, `CoroutineDispatchers` |
| `mobile-network.mdc` | Apollo (GraphQL) in `:core-network`, Supabase Storage in `:core-storage`, BuildConfig keys per-module |
| `mobile-resources.mdc` | `:common-resources` (moko-resources 0.26.x — `MR.strings/files/fonts`) |
| `mobile-error-handling.mdc` | `coRunCatching` in suspend, `Result<T>` surfacing, Napier logging in repos/executors |
| `mobile-code-rules.mdc` | Access modifiers, member ordering, NPE-safety |
| `mobile-roadmap.mdc` | Remaining migrations (moko-resources, AGP9+KMP DSL cleanup) |

## Key Gotchas

- In suspend code, use `coRunCatching { ... }` (not bare `try/catch` and not plain `runCatching` — the latter swallows `CancellationException`). Plain `runCatching` is only OK in non-suspend paths.
- All repositories return `suspend fun (): Result<T>`. There is no `RequestResult` or UseCase layer — DTO→domain mapping lives in the repository.
- Loading is `Store.State.isLoading: Boolean`, not a `Result` variant. Errors are `Store.State.isError: Boolean` (and optional `Label`s for one-shot UI side effects).
- `CoroutineDispatchers` is the only way to obtain dispatchers in commonMain — inject it. `common/coroutines/Dispatchers.kt` contains a load-bearing `import kotlinx.coroutines.IO` (guarded by `@file:Suppress("UnusedImport")`) needed for Kotlin/Native — do not remove it.
- `BaseViewModel.onCleared()` must call `store.dispose()` (Store does not auto-dispose with the VM) — see `GuidesViewModel`.
- `Napier.base(DebugAntilog(...))` is called once per platform entry, before `initKoin(...)`. The call appends antilogs — if an entry can be re-created (test scenarios), wrap with `Napier.takeLogarithm()` first to avoid duplicate sinks.
- Feature DI: per-module Koin module functions (`commonModule`, `coreNetworkModule`, `coreStorageModule`, `coreResourcesModule`, `coreMVIKotlinModule`, `featureGuidesImplModule`, `featureGuidesPresentationModule`). `:composeApp/core/di/AppModule.kt` only aggregates via `includes(...)` — no per-class bindings live in shell. Use `koinViewModel<T>()` in composables.
- `initKoin()` is called from each platform entry point (`:androidApp/AndroidApp.onCreate`, `:composeApp/jvmMain/main.kt` before `application{}`, and the iOS `MainViewController` factory). It's idempotent — safe to call from re-created entry points.
- Apollo config lives in `:core-network/build.gradle.kts` under `apollo { service("api") { ... } }`. buildConfig for Stratz keys also there. Supabase keys live in `:core-storage/build.gradle.kts` buildConfig. API keys must be in `local.properties`.
- Versioning: `versionCode` is derived from `git rev-list --count HEAD` and `versionName` from `appVersion-major.appVersion-minor.{commitCount}` in `gradle/libs.versions.toml`. `AppVersion.getVersionCode/Name` is invoked from `:androidApp/build.gradle.kts`.
- All deps go through `gradle/libs.versions.toml`. No version literals in `build.gradle.kts`.
- AGP 9 KMP modules use `com.android.kotlin.multiplatform.library` (applied by the `kmp-library` convention plugin). The `androidLibrary { }` DSL is configured through a typed helper in `build-logic/extensions/ProjectExtensions.kt` (`KotlinMultiplatformExtension.androidLibrary`).
- Feature submodules (`:feature-X:api`/`impl`/`presentation`/`ui`) apply only `id("kmp-feature-setup")` — the plugin selects auto-wiring by submodule name and applies `kmp-library` (plus `compose-multiplatform-setup` for `:ui`) under the hood. Do not redeclare the dependencies listed in `mobile-architecture.mdc#build-conventions`; keep only feature-specific deps (e.g. `:core-network` on `:impl`) in the module's `build.gradle.kts`. New features must follow this skeleton — drift from the contract is a code-review smell.
- All resources go through `:common-resources/src/commonMain/moko-resources/`. Basenames must be identifier-safe — moko mirrors them verbatim (`constant_heroes.json` → `MR.files.constant_heroes_json`, `NotoSans-Regular.ttf` → `MR.fonts.notosans_regular`). JSON content is read via the `FileContentReader` expect/actual (in `:core-resources`) injected through Koin — Android `FileResource.readText` needs a `Context`, so do not call moko file APIs directly from commonMain.
- iOS Kotlin/Native cache is disabled (`kotlin.native.cacheKind=none` in `gradle.properties`) to work around a Supabase storage-kt build failure on iOS Simulator Arm64. Revisit once Supabase / Kotlin/Native versions move.
- Git hooks live in `.githooks/`. Enable via `git config core.hooksPath .githooks`.
- CI lives in `.github/workflows/`. PRs to `master`, `develop`, `develop-cmp` run Detekt + Android Lint (`:androidApp:lintDebug`) + SwiftLint + SwiftFormat.
- Detekt baseline at `linters/detekt/baseline.xml` — regenerate with `./gradlew detektBaseline` after intentional rule changes.
