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
./gradlew :composeApp:pixel5Check          # Compose UI tests on Pixel 5 managed device
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode.

## Stack (current — May 2026)

- Kotlin 2.0.0, Compose Multiplatform 1.6.11, AGP 8.3.0, Java target 1.8.
- Apollo Kotlin 4.0.0-beta.6 (Stratz GraphQL).
- Supabase 2.5.0 (storage only — for hero/item/ability icons).
- Ktor 2.3.11 (CIO on Android+JVM, Darwin on iOS).
- Coil 3.0.0-alpha06 (image loading).
- Coroutines 1.9.0-RC, kotlinx.serialization 1.7.0.
- Single-module project (`:composeApp`).
- DI: hand-rolled service locator `core/di/InjectProvider`.
- State management: custom `base/BaseViewModel<State, Action, Event>` (StateFlow + SharedFlow, `obtainEvent`).
- Resources: `composeApp/src/commonMain/composeResources/` (`values/strings.xml`, `files/constants/*.json`, `font/`). Generated `Res.string.*` / `Res.readBytes(...)`.
- Navigation: `compose-navigation` 2.8.0-alpha02 + `AppScreens` sealed routes + `LocalNavHost` CompositionLocal.

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
│   ├── di/InjectProvider.kt            # Service locator (Apollo, Supabase, repositories)
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
| `mobile-architecture.mdc` | `BaseViewModel<State, Action, Event>`, `InjectProvider`, Compose Navigation |
| `mobile-compose.mdc` | Recomposition optimization, composable splitting, Previews |
| `mobile-data-layer.mdc` | Remote / Domain / UI entities, `RequestResult<T>`, mappers, repository cache |
| `mobile-network.mdc` | Apollo (GraphQL), Supabase Storage, Ktor engine per platform, BuildConfig keys |
| `mobile-resources.mdc` | `composeResources/` layout, `Res.string.*`, JSON constants |
| `mobile-error-handling.mdc` | `runCatching` discipline, `RequestResult` wrapping, no swallowed cancellation |
| `mobile-code-rules.mdc` | Access modifiers, member ordering, NPE-safety |
| `mobile-roadmap.mdc` | Planned migrations (Koin, MVIKotlin, multi-module, moko-resources) and their order |

## Key Gotchas

- `runCatching` is currently the norm in suspend functions, but a `coRunCatching` helper that rethrows `CancellationException` should be introduced before adding more retry logic. Until then: never silently swallow `Throwable` in coroutines.
- `InjectProvider` is a static service locator with no scope. Do **not** add more dependencies to it lightly — it is on the migration path to Koin (see `mobile-roadmap.mdc`).
- `BaseViewModel` exposes `_viewStates` (StateFlow) and `_viewActions` (SharedFlow). UI reacts to actions one-time, state continuously. Events go through `obtainEvent(...)`.
- All Apollo/Supabase config lives in `composeApp/build.gradle.kts` under `apollo { ... }` and `buildConfig { ... }`. API keys must be in `local.properties`.
- Versioning: `versionCode` is derived from `git rev-list --count HEAD` and `versionName` from `appVersion-major.appVersion-minor.{commitCount}` in `gradle/libs.versions.toml`. Do not hardcode.
- All deps go through `gradle/libs.versions.toml`. No version literals in `build.gradle.kts`.
- Git hooks live in `.githooks/`. Enable via `git config core.hooksPath .githooks`.
- CI lives in `.github/workflows/`. PRs to `master`, `develop`, `develop-cmp` run Detekt + Android Lint + SwiftLint + SwiftFormat.
- Detekt baseline at `linters/detekt/baseline.xml` — regenerate with `./gradlew detektBaseline` after intentional rule changes.
