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
- State management: custom `base/BaseViewModel<State, Action, Event>` (StateFlow + SharedFlow, `obtainEvent`).
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
│   ├── BaseViewModel.kt                # State + Action + Event abstraction
│   └── LocalImageLoader.kt
├── core/
│   ├── di/
│   │   ├── AppModule.kt                # Koin module (Apollo, Supabase, datasources, repos, use-cases, VMs)
│   │   └── Koin.kt                     # initKoin(appDeclaration) entry point
│   ├── data/
│   │   ├── RequestResult.kt            # Success | InProgress | Error
│   │   ├── api/                        # Remote data sources (GraphQL, Supabase Storage)
│   │   ├── local/resources/constants/  # Local JSON data sources
│   │   └── repository/                 # Aggregating repositories with in-memory cache
│   └── graphql/                        # Apollo GraphQL queries + Stratz schema
├── common/utils/                       # Pure-Kotlin helpers (TimeConverter, ...)
├── features/
│   ├── guides/
│   │   ├── domain/
│   │   │   ├── models/                 # UI-shaped domain models (GuideUI, HeroUI, ...)
│   │   │   └── usecases/               # UseCase classes
│   │   └── presentation/
│   │       ├── models/                 # ViewState | Action | Event
│   │       ├── GuidesViewModel.kt
│   │       └── ui/                     # Screen + sub-views
│   └── detailGuide/                    # Stub for the next feature
└── navigation/AppScreens.kt
```

## Rules (`.claude/rules/`)

| File | Description |
| --- | --- |
| `mobile-overview.mdc` | Stack, package layout, naming conventions |
| `mobile-architecture.mdc` | `BaseViewModel<State, Action, Event>`, Koin (`AppModule`, `initKoin`), Compose Navigation |
| `mobile-compose.mdc` | Recomposition optimization, composable splitting, Previews |
| `mobile-data-layer.mdc` | Remote / Domain / UI entities, `RequestResult<T>`, mappers, repository cache |
| `mobile-network.mdc` | Apollo (GraphQL), Supabase Storage, Ktor engine per platform, BuildConfig keys |
| `mobile-resources.mdc` | `composeResources/` layout, `Res.string.*`, JSON constants |
| `mobile-error-handling.mdc` | `runCatching` discipline, `RequestResult` wrapping, no swallowed cancellation |
| `mobile-code-rules.mdc` | Access modifiers, member ordering, NPE-safety |
| `mobile-roadmap.mdc` | Planned migrations (MVIKotlin, multi-module, moko-resources) and their order |

## Key Gotchas

- `runCatching` is currently the norm in suspend functions, but a `coRunCatching` helper that rethrows `CancellationException` should be introduced before adding more retry logic. Until then: never silently swallow `Throwable` in coroutines.
- Koin bindings live in `core/di/AppModule.kt`. Add new datasources / repos / use-cases / view-models there, then resolve via constructor parameters in code. Use `koinViewModel<T>()` in composables.
- `initKoin()` is called from each platform entry point (`AndroidApp.onCreate`, `jvmMain/main.kt` before `application{}`, and the iOS `MainViewController` factory). It's idempotent — safe to call from re-created entry points.
- `BaseViewModel` exposes `_viewStates` (StateFlow) and `_viewActions` (SharedFlow). UI reacts to actions one-time, state continuously. Events go through `obtainEvent(...)`.
- All Apollo/Supabase config lives in `composeApp/build.gradle.kts` under `apollo { ... }` and `buildConfig { ... }`. API keys must be in `local.properties`.
- Versioning: `versionCode` is derived from `git rev-list --count HEAD` and `versionName` from `appVersion-major.appVersion-minor.{commitCount}` in `gradle/libs.versions.toml`. Do not hardcode.
- All deps go through `gradle/libs.versions.toml`. No version literals in `build.gradle.kts`.
- AGP 9.0 + KMP `com.android.application` combination uses legacy flags `android.builtInKotlin=false` and `android.newDsl=false` in `gradle.properties`. These are deprecated by AGP 9 but kept until we split the Android app into its own module (see `mobile-roadmap.mdc`).
- Git hooks live in `.githooks/`. Enable via `git config core.hooksPath .githooks`.
- CI lives in `.github/workflows/`. PRs to `master`, `develop`, `develop-cmp` run Detekt + Android Lint + SwiftLint + SwiftFormat.
- Detekt baseline at `linters/detekt/baseline.xml` — regenerate with `./gradlew detektBaseline` after intentional rule changes.
