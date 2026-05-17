# d2-build-helper

Compose Multiplatform application (Android, Desktop/JVM, iOS) that shows hero builds and statistics from high-rating Dota 2 matches. Data: Stratz GraphQL + Supabase Storage.

## Before running

- Run [KDoctor](https://github.com/Kotlin/kdoctor) to verify the environment.
- Install JDK 17+.
- Create `local.properties` in the project root with:

  ```properties
  sdk.dir=/path/to/Android/sdk
  SUPABASE_API_KEY=...
  STRATZ_API_KEY=...
  ```

## Building

### Android

```shell
./gradlew :composeApp:assembleDebug
# APK: composeApp/build/outputs/apk/debug/
./gradlew :composeApp:pixel5Check     # UI tests on managed Pixel 5
```

Or import the project in Android Studio and use the Android run configuration.

### Desktop

```shell
./gradlew :composeApp:run
./gradlew :composeApp:jvmTest
```

### iOS

```shell
./gradlew :composeApp:iosSimulatorArm64Test
```

For an iOS run, open `iosApp/iosApp.xcodeproj` in Xcode, or use the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) in Android Studio.

## Tooling

### Linters

Static analysis and formatters run both in CI and via the local git hook.

```shell
./gradlew detekt              # Kotlin (config: linters/detekt/config.yml)
./gradlew :composeApp:lintDebug
# iOS: see brew install below
```

Detekt reports land at `build/reports/detekt/detekt.html`. The baseline lives at `linters/detekt/baseline.xml` and is committed.

For iOS lint/format you need `swiftlint` and `swiftformat` locally:

```shell
brew install swiftlint swiftformat
```

Configs: `linters/swiftlint/.swiftlint.yml`, `linters/swiftformat/.swiftformat`.

### Git hooks

```shell
git config core.hooksPath .githooks
```

`pre-commit` runs SwiftFormat + SwiftLint on staged `.swift` files and Detekt on staged `.kt` files. `post-rewrite` re-runs SwiftFormat + SwiftLint after rebases.

### CI

`.github/workflows/`:

- `android-checks.yml` — runs Detekt and Android Lint on PRs against `master`, `develop`, `develop-cmp`. Uploads HTML reports as artifacts.
- `ios-checks.yml` — runs SwiftLint + SwiftFormat on the same PRs.

`.github/dependabot.yml` — grouped monthly updates for Gradle and GitHub Actions.

## Project layout

- `composeApp/` — single Compose Multiplatform module (Android + JVM + iOS targets).
- `iosApp/` — Xcode wrapper that hosts the Compose framework.
- `build-logic/` — composite Gradle build with shared utilities (`AppVersion`, `GitCommitCountValueSource`, `JsonSerializationPlugin`).
- `gradle/libs.versions.toml` — version catalog.
- `linters/` — Detekt, Android Lint, SwiftLint, SwiftFormat configs.
- `.githooks/` — local git hooks.
- `.github/` — workflows + Dependabot.
- `.claude/` — AI-development context (`CLAUDE.md` is at the repo root).
- `docs/` — long-form documentation (`docs/superpowers/plans/`, `docs/specs/`).

`versionCode` is derived from `git rev-list --count HEAD`. `versionName` is `appVersion-major.appVersion-minor.{commitCount}` from `gradle/libs.versions.toml`.
