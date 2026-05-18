# d2-build-helper

Dota 2 build helper — Compose Multiplatform app (Android, Desktop/JVM, iOS) that surfaces hero builds and stats from high-rating matches. Data sources: Stratz GraphQL API (via Apollo) and Supabase Storage (for icons). Local JSON for hero/item/ability constants.

## Build Commands

```shell
./gradlew :composeApp:assembleDebug        # Android debug APK
./gradlew :composeApp:run                  # Desktop (JVM)
./gradlew :composeApp:iosSimulatorArm64Test
./gradlew :composeApp:jvmTest
./gradlew :composeApp:lintDebug            # Android Lint
./gradlew detekt                            # Static analysis (Kotlin)
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
- Single-module project (`:composeApp`) with `build-logic` composite build.
- DI: Koin 4.1.x (`core/di/AppModule.kt`, `core/di/Koin.kt#initKoin`).
- MVIKotlin 4.4.0 (`core/mvikotlin/BaseExecutor`, `LoggingStoreFactory` wired through Napier).
- Napier 2.7.1 (logger; `Napier.base(DebugAntilog(...))` on every platform entry).
- moko-mvvm 0.16.1 (CFlow/CStateFlow for iOS contract on `BaseViewModel`).
- State management: MVIKotlin `Store` per feature + `core/presentation/viewmodel/BaseViewModel<State, Label>` (`bindAndStart` binds `store.states`/`store.labels` through mappers).
- Resources: `composeApp/src/commonMain/composeResources/` (`values/strings.xml`, `files/constants/*.json`, `font/`). Generated `Res.string.*` / `Res.readBytes(...)`.
- Navigation: `compose-navigation` 2.9.2 + `AppScreens` sealed routes + `LocalNavHost` CompositionLocal.

API keys come from `local.properties` (`SUPABASE_API_KEY`, `STRATZ_API_KEY`) via `buildConfig` plugin.

## Architecture

Clean-ish layering, single module:

```
composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/
├── App.kt                              # Root composable + theme + NavHost
├── theme/                              # Colors, typography, theme (platform-split)
├── base/
│   └── LocalImageLoader.kt
├── core/
│   ├── di/
│   │   ├── AppModule.kt                # Koin module (Apollo, Supabase, datasources, repos, VMs); aggregates per-feature impl modules
│   │   └── Koin.kt                     # initKoin(appDeclaration) entry point
│   ├── data/
│   │   ├── api/                        # Remote data sources (GraphQL, Supabase Storage) + DTOs
│   │   ├── local/resources/constants/  # Local JSON data sources
│   │   └── repository/                 # Aggregating repositories (suspend fun (): Result<T>) with in-memory cache
│   ├── graphql/                        # Apollo GraphQL queries + Stratz schema
│   ├── mvikotlin/                      # BaseExecutor + CoreMVIKotlinModule (LoggingStoreFactory via Napier)
│   └── presentation/viewmodel/         # BaseViewModel<State, Label> + BaseIosViewModel
├── common/
│   ├── coroutines/                     # CoroutineDispatchers interface + Impl + Dispatchers.kt (load-bearing IO import)
│   ├── extensions/                     # coRunCatching, ResultExtensions
│   ├── mappers/                        # Shared mapper interfaces
│   └── utils/                          # Pure-Kotlin helpers (TimeConverter, OneTimeEvent, ...)
├── features/
│   ├── guides/
│   │   ├── api/store/GuidesStore.kt
│   │   ├── impl/
│   │   │   ├── di/FeatureGuidesImplModule.kt
│   │   │   └── domain/{GuidesStoreFactory, GuidesExecutor, GuidesReducer}.kt
│   │   ├── domain/models/              # Guide, Hero, Item, ImageResources, ...
│   │   └── presentation/
│   │       ├── GuidesViewModel.kt
│   │       ├── models/{UiGuidesState, UiGuidesLabel}.kt
│   │       ├── mappers/{UiGuidesStateMapper, UiGuidesLabelMapper}.kt
│   │       └── ui/                     # Screen + sub-views
│   └── detailGuide/                    # Stub for the next feature
└── navigation/AppScreens.kt
```

## Rules (`.claude/rules/`)

| File | Description |
| --- | --- |
| `mobile-overview.mdc` | Stack, package layout, naming conventions |
| `mobile-architecture.mdc` | MVIKotlin Store/Executor/Reducer, `BaseViewModel<State, Label>`, Koin (`AppModule`, per-feature `*ImplModule`), Compose Navigation |
| `mobile-compose.mdc` | Recomposition optimization, composable splitting, Previews, local-mirror text inputs |
| `mobile-data-layer.mdc` | DTO / domain split, `suspend fun (): Result<T>` repositories, mappers, repository cache, `CoroutineDispatchers` |
| `mobile-network.mdc` | Apollo (GraphQL), Supabase Storage, Ktor engine per platform, BuildConfig keys |
| `mobile-resources.mdc` | `composeResources/` layout, `Res.string.*`, JSON constants |
| `mobile-error-handling.mdc` | `coRunCatching` in suspend, `Result<T>` surfacing, Napier logging in repos/executors |
| `mobile-code-rules.mdc` | Access modifiers, member ordering, NPE-safety |
| `mobile-roadmap.mdc` | Planned migrations (multi-module, moko-resources) and their order |

## Key Gotchas

- In suspend code, use `coRunCatching { ... }` (not bare `try/catch` and not plain `runCatching` — the latter swallows `CancellationException`). Plain `runCatching` is only OK in non-suspend paths.
- All repositories return `suspend fun (): Result<T>`. There is no `RequestResult` or UseCase layer — DTO→domain mapping lives in the repository.
- Loading is `Store.State.isLoading: Boolean`, not a `Result` variant. Errors are `Store.State.isError: Boolean` (and optional `Label`s for one-shot UI side effects).
- `CoroutineDispatchers` is the only way to obtain dispatchers in commonMain — inject it. `common/coroutines/Dispatchers.kt` contains a load-bearing `import kotlinx.coroutines.IO` (guarded by `@file:Suppress("UnusedImport")`) needed for Kotlin/Native — do not remove it.
- `BaseViewModel.onCleared()` must call `store.dispose()` (Store does not auto-dispose with the VM) — see `GuidesViewModel`.
- `Napier.base(DebugAntilog(...))` is called once per platform entry, before `initKoin(...)`. The call appends antilogs — if an entry can be re-created (test scenarios), wrap with `Napier.takeLogarithm()` first to avoid duplicate sinks.
- Core bindings live in `core/di/AppModule.kt`; per-feature bindings live in `<feature>/impl/di/<Feature>ImplModule.kt` and are aggregated by `appModule`. Resolve via constructor parameters in code. Use `koinViewModel<T>()` in composables.
- `initKoin()` is called from each platform entry point (`AndroidApp.onCreate`, `jvmMain/main.kt` before `application{}`, and the iOS `MainViewController` factory). It's idempotent — safe to call from re-created entry points.
- All Apollo/Supabase config lives in `composeApp/build.gradle.kts` under `apollo { ... }` and `buildConfig { ... }`. API keys must be in `local.properties`.
- Versioning: `versionCode` is derived from `git rev-list --count HEAD` and `versionName` from `appVersion-major.appVersion-minor.{commitCount}` in `gradle/libs.versions.toml`. Do not hardcode.
- All deps go through `gradle/libs.versions.toml`. No version literals in `build.gradle.kts`.
- AGP 9.0 + KMP `com.android.application` combination uses legacy flags `android.builtInKotlin=false` and `android.newDsl=false` in `gradle.properties`. These are deprecated by AGP 9 but kept until we split the Android app into its own module (see `mobile-roadmap.mdc`).
- Git hooks live in `.githooks/`. Enable via `git config core.hooksPath .githooks`.
- CI lives in `.github/workflows/`. PRs to `master`, `develop`, `develop-cmp` run Detekt + Android Lint + SwiftLint + SwiftFormat.
- Detekt baseline at `linters/detekt/baseline.xml` — regenerate with `./gradlew detektBaseline` after intentional rule changes.
