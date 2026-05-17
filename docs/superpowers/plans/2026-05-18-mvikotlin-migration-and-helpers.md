# MVIKotlin Migration and Helpers — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace `BaseViewModel<State, Action, Event>` + `RequestResult` + UseCase layer with the KMMTemplate MVIKotlin stack (Store + Reducer + Executor, `BaseViewModel<State, Label>`, Ui mappers), `suspend Result<T>` repositories, and shared helpers (`coRunCatching`, `Mapper`, `OneTimeEvent`, `CoroutineDispatchers`, Napier).

**Architecture:** Single-module project, mirror KMMTemplate's per-feature package layout (`api/impl/presentation`) using `internal` visibility. Repositories return `suspend Result<T>` (one-shot). Loading is a `Store.State.isLoading` flag. Aggregation of image resources moves into the consuming feature (`features/guides/domain/models/ImageResources`); the repository exposes granular methods. The hero search `TextField` uses a local-mirror pattern (`mutableStateOf` inside the composable + propagate to Store) so keystrokes never wait on the Store round-trip.

**Tech Stack:** Kotlin 2.3.10, Compose Multiplatform 1.10.1, Koin 4.1.1, MVIKotlin 4.4.0, Napier 2.7.1, moko-mvvm 0.16.1, Apollo 4.3.1, Ktor 3.3.3, Coil 3.2.0.

**Reference spec:** `docs/superpowers/specs/2026-05-17-mvikotlin-migration-and-helpers-design.md`. Each task points back to the relevant spec section for full design context.

**Branch:** `refactor/mvikotlin-and-helpers` (target `develop-cmp`). Spec is already committed.

**Build verification command (used in every task):**
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
`./gradlew :composeApp:jvmTest` is used after Task 10 (tests rewritten).

---

## Task 1: Add MVIKotlin / Napier / moko-mvvm dependencies

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `composeApp/build.gradle.kts`

Spec reference: *Versions and dependencies*.

- [ ] **Step 1: Add version entries in `gradle/libs.versions.toml`**

Insert under `[versions]`, alongside existing dependency versions:

```toml
# MVI / Logging / Multiplatform helpers
mvikotlin = "4.4.0"
napier = "2.7.1"
moko-mvvm = "0.16.1"
```

- [ ] **Step 2: Add library entries in `gradle/libs.versions.toml`**

Insert under `[libraries]`:

```toml
# MVIKotlin
mvikotlin-core       = { module = "com.arkivanov.mvikotlin:mvikotlin",                       version.ref = "mvikotlin" }
mvikotlin-main       = { module = "com.arkivanov.mvikotlin:mvikotlin-main",                  version.ref = "mvikotlin" }
mvikotlin-logging    = { module = "com.arkivanov.mvikotlin:mvikotlin-logging",               version.ref = "mvikotlin" }
mvikotlin-coroutines = { module = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines", version.ref = "mvikotlin" }

# Logging
napier = { module = "io.github.aakira:napier", version.ref = "napier" }

# moko-mvvm (CFlow/CStateFlow)
moko-mvvm-flow = { module = "dev.icerock.moko:mvvm-flow", version.ref = "moko-mvvm" }
```

- [ ] **Step 3: Wire dependencies in `composeApp/build.gradle.kts`**

Inside `kotlin { sourceSets { commonMain.dependencies { ... } } }`, append (preserve existing entries):

```kotlin
implementation(libs.mvikotlin.core)
implementation(libs.mvikotlin.main)
implementation(libs.mvikotlin.logging)
implementation(libs.mvikotlin.coroutines)
implementation(libs.napier)
implementation(libs.moko.mvvm.flow)
```

- [ ] **Step 4: Verify the build resolves the new deps**

Run:
```bash
./gradlew :composeApp:dependencies --configuration commonMainImplementation | grep -E "mvikotlin|napier|mvvm"
```
Expected: lines for `mvikotlin`, `mvikotlin-main`, `mvikotlin-logging`, `mvikotlin-extensions-coroutines`, `napier`, `mvvm-flow`. No errors.

- [ ] **Step 5: Verify all platform compiles still pass**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64
```
Expected: `BUILD SUCCESSFUL`. No new compilation errors (deps are added but not yet referenced from sources).

- [ ] **Step 6: Commit**

```bash
git add gradle/libs.versions.toml composeApp/build.gradle.kts
git commit -m "chore: add MVIKotlin, Napier and moko-mvvm dependencies"
```

---

## Task 2: Add common helpers (Mapper, OneTimeEvent, coRunCatching, ResultExtensions, CoroutineDispatchers)

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/mappers/Mapper.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/utils/OneTimeEvent.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/extensions/CoroutineExtensions.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/extensions/ResultExtensions.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common/coroutines/CoroutineDispatchers.kt`

Spec reference: *Common utilities*, *Errors and async*.

- [ ] **Step 1: Create `Mapper.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.common.mappers

interface Mapper<From, To> {
    fun map(item: From): To

    fun map(list: List<From>): List<To> = list.map(::map)
}
```

- [ ] **Step 2: Create `OneTimeEvent.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.common.utils

import kotlinx.coroutines.channels.Channel

@Suppress("FunctionName")
fun <T> OneTimeEvent(): Channel<T> = Channel(Channel.BUFFERED)
```

- [ ] **Step 3: Create `ResultExtensions.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.common.extensions

fun <T> T.wrapResultSuccess(): Result<T> = Result.success(this)

fun <T> Throwable.wrapResultFailure(): Result<T> = Result.failure(this)
```

- [ ] **Step 4: Create `CoroutineExtensions.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.common.extensions

import kotlinx.coroutines.CancellationException

suspend inline fun <T> coRunCatching(
    crossinline tryBlock: suspend () -> T,
    crossinline catchBlock: (Throwable) -> Result<T> = { it.wrapResultFailure() },
): Result<T> = try {
    Result.success(tryBlock())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    catchBlock(throwable)
}
```

- [ ] **Step 5: Create `CoroutineDispatchers.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}

class CoroutineDispatchersImpl : CoroutineDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main.immediate
}
```

- [ ] **Step 6: Register `CoroutineDispatchers` in Koin**

Modify `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt`: add inside `module { ... }` (top of the block, before `single<ApolloClient>`):

```kotlin
single<CoroutineDispatchers> { CoroutineDispatchersImpl() }
```

Add the import:
```kotlin
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchersImpl
```

- [ ] **Step 7: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
Expected: `BUILD SUCCESSFUL`. New helpers are unused so far — that's fine.

- [ ] **Step 8: Commit**

```bash
git add composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/common composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt
git commit -m "feat: add common helpers (Mapper, OneTimeEvent, coRunCatching, Result/Coroutine extensions, CoroutineDispatchers)"
```

---

## Task 3: Wire Napier on all three platform entry points

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/dev/nonoxy/d2buildhelper/App.android.kt`
- Modify: `composeApp/src/jvmMain/kotlin/main.kt`
- Modify: `composeApp/src/iosMain/kotlin/dev/nonoxy/d2buildhelper/MainViewController.kt`

Spec reference: *Logging*.

- [ ] **Step 1: Android — init Napier in `AndroidApp.onCreate`**

Modify `App.android.kt`. Replace the `AndroidApp` class with:

```kotlin
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
```

Add imports at the top of the file (alongside existing imports):
```kotlin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
```

- [ ] **Step 2: JVM — init Napier in `jvmMain/main.kt`**

Modify `composeApp/src/jvmMain/kotlin/main.kt`. Replace the body of `fun main()` so it reads:

```kotlin
fun main() {
    Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
    initKoin()
    application {
        Window(
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 600)
            App()
        }
    }
}
```

Add imports:
```kotlin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
```

- [ ] **Step 3: iOS — init Napier in `MainViewController`**

Modify `composeApp/src/iosMain/kotlin/dev/nonoxy/d2buildhelper/MainViewController.kt`. Replace with:

```kotlin
package dev.nonoxy.d2buildhelper

import androidx.compose.ui.window.ComposeUIViewController
import dev.nonoxy.d2buildhelper.core.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        initKoin()
    }
) {
    App()
}
```

- [ ] **Step 4: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add composeApp/src/androidMain composeApp/src/jvmMain composeApp/src/iosMain
git commit -m "feat: initialize Napier (DebugAntilog) on Android, JVM and iOS entry points"
```

---

## Task 4: Add `core/mvikotlin` — `BaseExecutor` and `CoreMVIKotlinModule`

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/mvikotlin/BaseExecutor.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/mvikotlin/di/CoreMVIKotlinModule.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt`

Spec reference: *MVIKotlin core*.

- [ ] **Step 1: Create `BaseExecutor.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.mvikotlin

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseExecutor<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext = mainContext) {

    final override fun executeAction(action: Action) {
        scope.launch {
            suspendExecuteAction(action)
        }
    }

    final override fun executeIntent(intent: Intent) {
        scope.launch {
            suspendExecuteIntent(intent)
        }
    }

    open suspend fun suspendExecuteIntent(intent: Intent) {
        // no-op
    }

    open suspend fun suspendExecuteAction(action: Action) {
        // no-op
    }
}
```

- [ ] **Step 2: Create `CoreMVIKotlinModule.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.mvikotlin.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.github.aakira.napier.Napier
import org.koin.dsl.module

val coreMVIKotlinModule = module {

    factory<StoreFactory> {
        val napierLogger = object : Logger {
            override fun log(text: String) {
                Napier.v(text)
            }
        }
        LoggingStoreFactory(delegate = DefaultStoreFactory(), logger = napierLogger)
    }
}
```

- [ ] **Step 3: Wire `coreMVIKotlinModule` into `appModule`**

In `AppModule.kt`, add the import:
```kotlin
import dev.nonoxy.d2buildhelper.core.mvikotlin.di.coreMVIKotlinModule
```

Change `val appModule = module { ... }` so the first line inside the block is:
```kotlin
val appModule = module {
    includes(coreMVIKotlinModule)
    // existing content unchanged below
```

- [ ] **Step 4: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/mvikotlin composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt
git commit -m "feat: add core/mvikotlin — BaseExecutor and LoggingStoreFactory Koin module"
```

---

## Task 5: Add `core/presentation` — `BaseIosViewModel` and `BaseViewModel`

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/BaseIosViewModel.kt`
- Create: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation/viewmodel/BaseViewModel.kt`

Spec reference: *Presentation core*.

- [ ] **Step 1: Create `BaseIosViewModel.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.presentation.viewmodel

import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.CStateFlow

interface BaseIosViewModel<State, Label> {

    val state: CStateFlow<State>
    val label: CFlow<Label>

    fun onCleared()

    fun start()

    fun stop()
}
```

- [ ] **Step 2: Create `BaseViewModel.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.BindingsBuilder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.nonoxy.d2buildhelper.common.utils.OneTimeEvent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<State, Label>(
    initialState: State,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel(), BaseIosViewModel<State, Label> {

    private val mutableState = MutableStateFlow(initialState).cMutableStateFlow()
    private val mutableLabel = OneTimeEvent<Label>()

    override val state: CStateFlow<State>
        get() = mutableState.cStateFlow()

    override val label: CFlow<Label>
        get() = mutableLabel.receiveAsFlow().cFlow()

    private var binder: Binder? = null

    private var binderIsStarted = false

    init {
        Napier.v("VM $this init")
    }

    protected open fun acceptState(state: State) {
        mutableState.value = state
    }

    protected open fun acceptLabel(label: Label) {
        viewModelScope.launch {
            mutableLabel.send(label)
        }
    }

    protected fun bindAndStart(
        mainContext: CoroutineContext = mainDispatcher,
        builder: BindingsBuilder.() -> Unit,
    ) = bind(mainContext, builder).run {
        binder = this
        this@BaseViewModel.start()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        Napier.v("VM $this onCleared")
    }

    /** Only for iOS. No-op in Compose-on-iOS code paths. */
    override fun start() {
        if (binderIsStarted) return
        binderIsStarted = true
        Napier.v("VM $this start")
        binder?.start()
    }

    /** Only for iOS. No-op in Compose-on-iOS code paths. */
    override fun stop() {
        if (!binderIsStarted) return
        binderIsStarted = false
        Napier.v("VM $this stop")
        binder?.stop()
    }
}
```

- [ ] **Step 3: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
Expected: `BUILD SUCCESSFUL`. If moko-mvvm JVM artifact is unresolved, fall back to `expect class CStateFlow<T>` shim (see *Risks* in the spec) — investigate before improvising.

- [ ] **Step 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/presentation
git commit -m "feat: add core/presentation — BaseViewModel and BaseIosViewModel"
```

---

## Task 6: Rename data-layer models to `*Dto` and domain models to drop `UI` suffix

This is mechanical and behavior-preserving. Build must stay green throughout.

**Files (rename / move + reference updates):**

DTO renames (mark them `internal` while renaming):
- `core/data/api/guides/models/Guide.kt` → `GuideDto.kt` (type rename `Guide` → `GuideDto`)
- `core/data/api/guides/models/DetailGuide.kt` → contains `DetailGuide` + `PlayerStats` + nested types → rename top-levels to `DetailGuideDto` and `PlayerStatsDto`; rename inner `enum class MatchPlayerPositionType` to `MatchPlayerPositionTypeDto`; rename `ItemPurchase` to `ItemPurchaseDto`
- `core/data/api/resources/image/models/ImageResources.kt` (current location) — this file moves in Task 7, but here rename its `ImageResources` type to a temporary `ImageResourcesDto` only if it contains DTO fields; **inspect first** — if it already references domain Hero/Item/Ability, leave the type name alone and let Task 7 reposition it.
- `core/data/local/resources/constants/models/Hero.kt` → `HeroDto.kt`
- `core/data/local/resources/constants/models/Item.kt` → `ItemDto.kt`
- `core/data/local/resources/constants/models/Ability.kt` → `AbilityDto.kt`

Domain renames:
- `features/guides/domain/models/GuideUI.kt` → `Guide.kt` (this file currently bundles `GuideUI`, `HeroUI`, `ItemPurchaseUI`, `MatchPlayerPositionType` and `PlayerStatsUI`; split per type if it makes sense, or rename in place — see Step 4)

Reference updates affect: `core/data/api/guides/GuidesApi.kt`, `core/data/api/guides/GuidesDataSource.kt`, `core/data/api/guides/mappers/GuideMappers.kt`, `features/guides/domain/usecases/GetGuidesUseCase.kt`, `features/guides/domain/usecases/GetImagesUseCase.kt`, `features/guides/presentation/GuidesViewModel.kt`, `features/guides/presentation/models/GuidesViewState.kt`, and all UI views under `features/guides/presentation/ui/`.

Spec reference: *Type renames (clean DTO↔domain border)*.

- [ ] **Step 1: Inspect current `GuideUI.kt` and `ImageResources.kt`**

Run:
```bash
cat composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/GuideUI.kt
cat composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/models/ImageResources.kt
```
Confirm the types declared. Note whether `ImageResources` is keyed by domain (`HeroUI`) or DTO (`Hero`) — that decides whether it stays under data or moves.

- [ ] **Step 2: Rename DTO files using `git mv` + targeted edits**

For each DTO file, two ops: `git mv` the file and update the `class`/`enum` declaration to the new name.

```bash
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/models/Guide.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/models/GuideDto.kt
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/Hero.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/HeroDto.kt
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/Item.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/ItemDto.kt
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/Ability.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/models/AbilityDto.kt
```

Edit each renamed file so the class name matches the file name (e.g., `data class Guide` → `data class GuideDto`). Also mark `internal`:
```kotlin
internal data class GuideDto(...)
```

For `core/data/api/guides/models/DetailGuide.kt`: stays in place file-name-wise (or rename file too if simpler), but rename top-level types to `DetailGuideDto`, `PlayerStatsDto`, `ItemPurchaseDto`, `MatchPlayerPositionTypeDto`. Mark `internal`.

- [ ] **Step 3: Update references to renamed DTOs**

Run after each rename to surface broken imports / usages:
```bash
./gradlew :composeApp:compileKotlinJvm
```

Touch the call sites: `GuidesApi`, `GuidesDataSource`, `GuideMappers` (in `core/data/api/guides/mappers/`), `ImageResourcesApi`, `ImageResourcesDataSource`, `ConstantResourcesDataSource`. Replace `Guide` (DTO) → `GuideDto`, `Hero` → `HeroDto`, etc. Inside `GetGuidesUseCase.toGuideUi()` the receiver type `Guide` becomes `GuideDto`.

- [ ] **Step 4: Rename domain models**

Edit `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/GuideUI.kt`. Rename types inside the file:
- `GuideUI` → `Guide`
- `HeroUI` → `Hero`
- `ItemPurchaseUI` → `ItemPurchase`
- `PlayerStatsUI` → `PlayerStats`
- `MatchPlayerPositionType` already has no `UI` suffix — leave the name, but if the spec dictates `MatchPlayerPosition`, also drop the `Type` suffix (spec final wording uses `MatchPlayerPosition`). Apply the shorter name.

Then `git mv` the file:
```bash
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/GuideUI.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/Guide.kt
```

If `Guide` collides with `GuideDto` in any file (it should not, because DTOs are in `core/data/...`), use fully qualified imports.

- [ ] **Step 5: Update references to renamed domain models**

Update:
- `features/guides/domain/usecases/GetGuidesUseCase.kt` (return types, `toGuideUi` returns `Guide`)
- `features/guides/domain/usecases/GetImagesUseCase.kt` (`HeroUI` → `Hero`, `ImageResources(heroImages = ...)` key types)
- `features/guides/presentation/GuidesViewModel.kt`
- `features/guides/presentation/models/GuidesViewState.kt` (uses `HeroUI`, `GuideUI`)
- `features/guides/presentation/models/GuidesEvent.kt` (no model refs, probably skip)
- All UI views under `features/guides/presentation/ui/`

A wide sweep:
```bash
grep -rl --include="*.kt" "HeroUI\|GuideUI\|ItemPurchaseUI\|PlayerStatsUI\|MatchPlayerPositionType" composeApp/src
```

For each match: replace the symbol with the new name.

- [ ] **Step 6: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 :composeApp:jvmTest detekt
```
Expected: `BUILD SUCCESSFUL`. (jvmTest must pass — existing `GetGuidesUseCaseTest` references `GuideUI` etc. and must be updated as part of this task.)

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "refactor: rename DTOs with Dto suffix, drop UI suffix from domain models"
```

---

## Task 7: Move `ImageResources` to feature-guides domain

**Files:**
- Move: `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/models/ImageResources.kt` → `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/ImageResources.kt`

Spec reference: *Data layer → Type renames*; *Data layer → Repository contracts* explains why aggregation is a feature concern.

- [ ] **Step 1: Move the file**

```bash
git mv composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/models/ImageResources.kt \
       composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/models/ImageResources.kt
```

- [ ] **Step 2: Update the package declaration inside the moved file**

Open the moved file. Change:
```kotlin
package dev.nonoxy.d2buildhelper.core.data.api.resources.image.models
```
to:
```kotlin
package dev.nonoxy.d2buildhelper.features.guides.domain.models
```

- [ ] **Step 3: Update all imports that referenced the old path**

```bash
grep -rl --include="*.kt" "core.data.api.resources.image.models.ImageResources" composeApp/src
```

For each hit, replace:
```kotlin
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
```
with:
```kotlin
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources
```

- [ ] **Step 4: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 :composeApp:jvmTest detekt
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "refactor: move ImageResources to features/guides/domain/models"
```

---

## Task 8: Replace `RequestResult` with `Result`, extract repositories, delete UseCases

This is the largest task. The build must remain green at the **end**; intermediate sub-steps may be transient.

**Files to modify (existing):**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/GuidesApi.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/GuidesDataSource.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/guides/mappers/GuideMappers.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/ImageResourcesApi.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/api/resources/image/ImageResourcesDataSource.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/ConstantResources.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/local/resources/constants/ConstantResourcesDataSource.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/resources/ResourcesRepository.kt` (rewritten as interface + impl)
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/GuidesViewModel.kt` (adapted to consume repositories directly with `Result`; old MVI shape stays — will be replaced in Task 9)
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt`

**Files to create:**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/guides/GuidesRepository.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/guides/GuidesRepositoryImpl.kt`

**Files to delete:**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/RequestResult.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/usecases/GetGuidesUseCase.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/usecases/GetImagesUseCase.kt`

Spec reference: *Data layer*, *Errors and async*, *DI wiring*.

- [ ] **Step 1: Rewrite `GuidesApi.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.data.api.guides

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto

internal interface GuidesApi {
    suspend fun getGuides(): Result<List<GuideDto>>

    suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>>

    suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto>
}
```

- [ ] **Step 2: Rewrite `GuidesDataSource.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.data.api.guides

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.core.data.api.guides.mappers.toGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.graphql.GuidesQuery
import dev.nonoxy.d2buildhelper.graphql.HeroGuidesQuery
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

internal class GuidesDataSource(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineDispatchers,
) : GuidesApi {

    override suspend fun getGuides(): Result<List<GuideDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                val resp = apolloClient.query(GuidesQuery()).execute()
                when {
                    resp.hasErrors() -> error("GraphQL errors: ${resp.errors}")
                    resp.exception != null -> throw resp.exception!!
                    else -> resp.dataOrThrow().heroStats?.guideFilterNotNull()?.flatMap { guide ->
                        guide.guidesFilterNotNull()?.map { it.toGuideDto() } ?: emptyList()
                    } ?: emptyList()
                }
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getGuides failed")
                Result.failure(throwable)
            },
        )
    }

    override suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                val resp = apolloClient.query(HeroGuidesQuery(heroId = Optional.present(heroId.toInt()))).execute()
                when {
                    resp.hasErrors() -> error("GraphQL errors: ${resp.errors}")
                    resp.exception != null -> throw resp.exception!!
                    else -> resp.dataOrThrow().heroStats?.guideFilterNotNull()?.flatMap { guide ->
                        guide.guidesFilterNotNull()?.map { it.toGuideDto() } ?: emptyList()
                    } ?: emptyList()
                }
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getHeroGuides($heroId) failed")
                Result.failure(throwable)
            },
        )
    }

    override suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto> {
        TODO("Not yet implemented — detail guide feature is a stub")
    }
}
```

Note: `mappers/GuideMappers.kt` currently exposes `.toGuide()` returning the old data model. After Task 6 rename it likely exposes `.toGuideDto()` (DTO → DTO inside data layer). If the existing mapper does *Apollo response → domain*, instead rename it to keep DTO mapping inside data and move the Dto→Domain mapping to the repository (next step). Adjust the function name as needed and update its signature so it produces `GuideDto` (the DTO data class) from Apollo's `*FragmentImpl`.

- [ ] **Step 3: Create `GuidesRepository.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.data.repository.guides

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide

interface GuidesRepository {
    suspend fun getGuides(): Result<List<Guide>>

    suspend fun getHeroGuides(heroId: Short): Result<List<Guide>>
}
```

- [ ] **Step 4: Create `GuidesRepositoryImpl.kt`**

Reuses the DTO→domain mapping previously in `GetGuidesUseCase.toGuideUi()` / `toPlayerStatsUi()`. Move that logic in here:

```kotlin
package dev.nonoxy.d2buildhelper.core.data.repository.guides

import androidx.compose.ui.util.fastLastOrNull
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ItemPurchase
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStats
import kotlinx.coroutines.withContext

internal class GuidesRepositoryImpl(
    private val guidesApi: GuidesApi,
    private val dispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(): Result<List<Guide>> =
        guidesApi.getGuides().mapCatching { dtos ->
            withContext(dispatchers.default) { dtos.map(GuideDto::toDomain) }
        }

    override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> =
        guidesApi.getHeroGuides(heroId).mapCatching { dtos ->
            withContext(dispatchers.default) { dtos.map(GuideDto::toDomain) }
        }
}

private fun GuideDto.toDomain(): Guide = Guide(
    hero = Hero(
        heroId = hero.heroId,
        shortName = hero.shortName,
        displayName = hero.displayName,
    ),
    steamAccountId = steamAccountId,
    matchId = matchId,
    durationSeconds = durationSeconds,
    playerStats = playerStats.toDomain(),
)

private fun PlayerStatsDto.toDomain(): PlayerStats {
    val endItemIds = listOfNotNull(endItem0Id, endItem1Id, endItem2Id, endItem3Id, endItem4Id, endItem5Id)
    val sortedEndItemPurchases = endItemIds.map { endItemId ->
        itemPurchases?.fastLastOrNull { it?.itemId?.toShort() == endItemId }?.let {
            ItemPurchase(itemId = it.itemId.toShort(), time = it.time)
        } ?: ItemPurchase(itemId = endItemId, time = null)
    }.sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStats(
        position = MatchPlayerPosition.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant ?: true,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = impact ?: 25,
        endNeutralItemId = endNeutralItemId,
        sortedEndItemPurchases = sortedEndItemPurchases,
    )
}
```

If `Guide` / `PlayerStats` / etc. import paths differ, fall back to the actual files under `features/guides/domain/models/`. Cross-check after Task 6.

- [ ] **Step 5: Rewrite `ImageResourcesApi.kt` and its DataSource**

Rewrite the API:
```kotlin
package dev.nonoxy.d2buildhelper.core.data.api.resources.image

import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto

internal interface ImageResourcesApi {
    suspend fun getHeroImageUrls(heroes: List<HeroDto>): Result<Map<HeroDto, String>>
    suspend fun getItemImageUrls(items: List<ItemDto>): Result<Map<ItemDto, String>>
    suspend fun getAbilityImageUrls(abilities: List<AbilityDto>): Result<Map<AbilityDto, String>>
    suspend fun getAdditionalImageUrls(): Result<Map<String, String>>
}
```

Rewrite `ImageResourcesDataSource.kt` to match — wrap calls with `coRunCatching` + `withContext(dispatchers.io)`. Preserve any Supabase URL construction logic from the original.

- [ ] **Step 6: Rewrite `ConstantResources.kt` and DataSource**

```kotlin
package dev.nonoxy.d2buildhelper.core.data.local.resources.constants

import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto

internal interface ConstantResources {
    suspend fun getHeroConstants(): Result<List<HeroDto>>
    suspend fun getItemConstants(): Result<List<ItemDto>>
    suspend fun getAbilityConstants(): Result<List<AbilityDto>>
}
```

Rewrite `ConstantResourcesDataSource.kt` similarly — `Res.readBytes(...)` calls inside `coRunCatching` + `withContext(dispatchers.io)`.

- [ ] **Step 7: Rewrite `ResourcesRepository.kt` as interface + impl with granular methods**

Replace the existing file's contents with:

```kotlin
package dev.nonoxy.d2buildhelper.core.data.repository.resources

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.ImageResourcesApi
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.ConstantResources
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Ability
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Item
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface ResourcesRepository {
    suspend fun getHeroImages(): Result<Map<Hero, String>>
    suspend fun getItemImages(): Result<Map<Item, String>>
    suspend fun getAbilityImages(): Result<Map<Ability, String>>
    suspend fun getAdditionalImages(): Result<Map<String, String>>
}

internal class ResourcesRepositoryImpl(
    private val imageResourcesApi: ImageResourcesApi,
    private val constantResourcesDataSource: ConstantResources,
    private val dispatchers: CoroutineDispatchers,
) : ResourcesRepository {

    private val constantsMutex = Mutex()
    private var heroConstants: List<HeroDto>? = null
    private var itemConstants: List<ItemDto>? = null
    private var abilityConstants: List<AbilityDto>? = null

    private var heroImagesCache: Map<Hero, String>? = null
    private var itemImagesCache: Map<Item, String>? = null
    private var abilityImagesCache: Map<Ability, String>? = null
    private var additionalImagesCache: Map<String, String>? = null

    override suspend fun getHeroImages(): Result<Map<Hero, String>> {
        heroImagesCache?.let { return Result.success(it) }
        return ensureHeroes().mapCatching { heroes ->
            val urls = imageResourcesApi.getHeroImageUrls(heroes).getOrThrow()
            val domain = withContext(dispatchers.default) {
                urls.mapKeys { (dto, _) -> dto.toDomain() }
            }
            heroImagesCache = domain
            domain
        }
    }

    override suspend fun getItemImages(): Result<Map<Item, String>> {
        itemImagesCache?.let { return Result.success(it) }
        return ensureItems().mapCatching { items ->
            val urls = imageResourcesApi.getItemImageUrls(items).getOrThrow()
            val domain = withContext(dispatchers.default) {
                urls.mapKeys { (dto, _) -> dto.toDomain() }
            }
            itemImagesCache = domain
            domain
        }
    }

    override suspend fun getAbilityImages(): Result<Map<Ability, String>> {
        abilityImagesCache?.let { return Result.success(it) }
        return ensureAbilities().mapCatching { abilities ->
            val urls = imageResourcesApi.getAbilityImageUrls(abilities).getOrThrow()
            val domain = withContext(dispatchers.default) {
                urls.mapKeys { (dto, _) -> dto.toDomain() }
            }
            abilityImagesCache = domain
            domain
        }
    }

    override suspend fun getAdditionalImages(): Result<Map<String, String>> {
        additionalImagesCache?.let { return Result.success(it) }
        return imageResourcesApi.getAdditionalImageUrls().onSuccess { additionalImagesCache = it }
    }

    private suspend fun ensureHeroes(): Result<List<HeroDto>> = constantsMutex.withLock {
        heroConstants?.let { return Result.success(it) }
        constantResourcesDataSource.getHeroConstants().onSuccess { heroConstants = it }
    }

    private suspend fun ensureItems(): Result<List<ItemDto>> = constantsMutex.withLock {
        itemConstants?.let { return Result.success(it) }
        constantResourcesDataSource.getItemConstants().onSuccess { itemConstants = it }
    }

    private suspend fun ensureAbilities(): Result<List<AbilityDto>> = constantsMutex.withLock {
        abilityConstants?.let { return Result.success(it) }
        constantResourcesDataSource.getAbilityConstants().onSuccess { abilityConstants = it }
    }
}

private fun HeroDto.toDomain(): Hero = Hero(heroId = id, shortName = shortName, displayName = displayName)
private fun ItemDto.toDomain(): Item = Item(id = id /* + other fields */)
private fun AbilityDto.toDomain(): Ability = Ability(id = id /* + other fields */)
```

Cross-check the existing `Hero`/`Item`/`Ability` domain data-class shape before finalizing the `toDomain()` bodies — fields must match. If `Item`/`Ability` domain models don't exist yet, add them under `features/guides/domain/models/` (mirror DTO field set, drop `Dto`).

- [ ] **Step 8: Delete `RequestResult.kt`**

```bash
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/data/RequestResult.kt
```

- [ ] **Step 9: Delete UseCase files**

```bash
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/usecases/GetGuidesUseCase.kt
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/domain/usecases/GetImagesUseCase.kt
```

- [ ] **Step 10: Adapt the old `GuidesViewModel` to consume repositories (transitional)**

This is a stop-gap so the project compiles before Task 9. Keep the old MVI shape (`BaseViewModel<State, Action, Event>`) — replace UseCase usage with direct repository calls. Replace `GuidesViewModel.kt` body with:

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class GuidesViewModel(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
) : BaseViewModel<GuidesViewState, GuidesAction, GuidesEvent>(initialState = GuidesViewState.Loading) {

    init { fetchData() }

    private var searchJob: Job? = null

    private fun fetchData() {
        viewModelScope.launch {
            val (guidesRes, heroesRes, itemsRes, additionalRes) = coroutineScope {
                awaitAll(
                    async { guidesRepository.getGuides() },
                    async { resourcesRepository.getHeroImages() },
                    async { resourcesRepository.getItemImages() },
                    async { resourcesRepository.getAdditionalImages() },
                )
            }.let { results ->
                @Suppress("UNCHECKED_CAST")
                Quad(
                    results[0] as Result<List<dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide>>,
                    results[1] as Result<Map<Hero, String>>,
                    results[2] as Result<Map<dev.nonoxy.d2buildhelper.features.guides.domain.models.Item, String>>,
                    results[3] as Result<Map<String, String>>,
                )
            }

            if (guidesRes.isFailure || heroesRes.isFailure || itemsRes.isFailure || additionalRes.isFailure) {
                viewState = GuidesViewState.Error
                return@launch
            }

            val imageResources = ImageResources(
                heroImages = heroesRes.getOrThrow(),
                itemImages = itemsRes.getOrThrow().mapKeys { it.key.id },
                abilityImages = emptyMap(),
                additionalImages = additionalRes.getOrThrow(),
            )
            val heroSearchFiltered = withContext(Dispatchers.Default) {
                imageResources.heroImages.toList()
                    .sortedBy { (hero, _) -> hero.displayName }
                    .toMap()
            }
            viewState = GuidesViewState.Display(
                guides = guidesRes.getOrThrow(),
                imageResources = imageResources,
                heroSearchFiltered = heroSearchFiltered,
            )
        }
    }

    private fun fetchHeroGuides(heroId: Short) {
        viewModelScope.launch {
            val current = viewState as? GuidesViewState.Display ?: return@launch
            viewState = GuidesViewState.Loading
            guidesRepository.getHeroGuides(heroId).onSuccess { heroGuides ->
                viewState = current.copy(guides = heroGuides)
            }.onFailure {
                viewState = GuidesViewState.Error
            }
        }
    }

    override fun obtainEvent(viewEvent: GuidesEvent) {
        when (viewEvent) {
            is GuidesEvent.HeroSearchValueChanged -> updateHeroSearchValue(viewEvent.newValue)
            is GuidesEvent.SelectHeroInSearchDialog -> fetchHeroGuides(viewEvent.heroId)
            GuidesEvent.HeroSearchDialogClicked -> viewAction = GuidesAction.ShowHeroSearchDialog
        }
    }

    private fun updateHeroSearchValue(newValue: String) {
        val current = viewState as? GuidesViewState.Display ?: return
        viewState = current.copy(heroSearchValue = newValue)
        debounceSearch()
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(100L)
            val current = viewState as? GuidesViewState.Display ?: return@launch
            val filtered = withContext(Dispatchers.Default) {
                current.imageResources.heroImages
                    .filter { (hero, _) -> hero.displayName.contains(current.heroSearchValue.trim(), ignoreCase = true) }
                    .toList()
                    .sortedBy { (hero, _) -> hero.displayName }
                    .toMap()
            }
            viewState = current.copy(heroSearchFiltered = filtered)
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
```

This file is rewritten again in Task 9. The point here is to keep the build green during the data-layer migration.

- [ ] **Step 11: Update `AppModule.kt`**

Replace the `appModule` body so it reads:

```kotlin
val appModule = module {
    includes(coreMVIKotlinModule)

    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }

    single<ApolloClient> {
        ApolloClient.Builder()
            .serverUrl(BuildConfig.API_BASE_URL)
            .addHttpHeader("Authorization", "Bearer ${BuildConfig.STRATZ_API_KEY}")
            .build()
    }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_BASE_URL,
            supabaseKey = BuildConfig.SUPABASE_API_KEY,
        ) {
            install(Storage)
        }
    }

    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::ImageResourcesDataSource) bind ImageResourcesApi::class
    singleOf(::ConstantResourcesDataSource) bind ConstantResources::class

    singleOf(::GuidesRepositoryImpl) bind GuidesRepository::class
    singleOf(::ResourcesRepositoryImpl) bind ResourcesRepository::class

    viewModelOf(::GuidesViewModel)
}
```

Update imports to include the new repository types (`GuidesRepository`, `GuidesRepositoryImpl`, `ResourcesRepository`, `ResourcesRepositoryImpl`) and remove unused ones (`GetGuidesUseCase`, `GetImagesUseCase`).

- [ ] **Step 12: Update `GuidesViewState.kt`** if Steps above broke its type references

Inspect:
```bash
cat composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesViewState.kt
```
After Task 6 rename, this file's type imports should already use `Guide` / `Hero` / `ImageResources` from `features/guides/domain/models/`. Verify and adjust.

- [ ] **Step 13: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 detekt
```
Expected: `BUILD SUCCESSFUL`. `jvmTest` will fail until Task 10 — accept that.

If `jvmTest` is run by default in CI, ensure it isn't gating local verification here. The `GetGuidesUseCaseTest` references deleted types; it gets replaced in Task 10. Either delete the test file as part of this step (preferred) or accept the broken state until Task 10 — the latter is fine because tests are not in the verification command above.

Actually delete the test file now to keep the working tree consistent:
```bash
git rm composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/usecases/GetGuidesUseCaseTest.kt
git rm composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/data/FakeGuidesDataSource.kt
```

Re-run verification:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compileTestKotlinJvm detekt
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 14: Commit**

```bash
git add -A
git commit -m "refactor: replace RequestResult with Result, extract repositories, delete UseCase layer"
```

---

## Task 9: Migrate Guides feature to MVIKotlin (Store/Executor/Reducer/Factory + Mappers + ViewModel + UI)

After Task 8, the data layer is on `Result`/repositories. This task completes the architectural migration by replacing the `BaseViewModel<State, Action, Event>` shape with the KMMTemplate stack.

**Files to create:**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/api/store/GuidesStore.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/GuidesStoreFactory.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/GuidesExecutor.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/GuidesReducer.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/di/FeatureGuidesImplModule.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/UiGuidesState.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/UiGuidesLabel.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/mappers/UiGuidesStateMapper.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/mappers/UiGuidesLabelMapper.kt`

**Files to rewrite:**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/GuidesViewModel.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/GuidesScreen.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/GuidesView.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/HeroFilterDialogView.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/ui/views/GuidesErrorView.kt` (add retry callback)
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/core/di/AppModule.kt`

**Files to delete:**
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/base/BaseViewModel.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesAction.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesEvent.kt`
- `composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesViewState.kt`

Spec reference: *Guides feature (concrete migration)*.

- [ ] **Step 1: Create `GuidesStore.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.api.store

import com.arkivanov.mvikotlin.core.store.Store
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources

interface GuidesStore : Store<Intent, State, Label> {

    data class State(
        val guides: List<Guide> = emptyList(),
        val imageResources: ImageResources? = null,
        val heroSearchValue: String = "",
        val heroSearchFiltered: Map<Hero, String> = emptyMap(),
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    sealed interface Intent {
        data class OnHeroSearchValueChange(val newValue: String) : Intent
        data object OnHeroSearchDialogClick : Intent
        data class OnHeroSelect(val heroId: Short) : Intent
        data object OnRetry : Intent
    }

    sealed interface Label {
        data object ShowHeroSearchDialog : Label
    }
}
```

- [ ] **Step 2: Create `GuidesStoreFactory.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources

internal class GuidesStoreFactory(
    private val storeFactory: StoreFactory,
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) {
    fun create(): GuidesStore = object :
        GuidesStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GuidesStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.LoadInitial),
            executorFactory = {
                GuidesExecutor(
                    guidesRepository = guidesRepository,
                    resourcesRepository = resourcesRepository,
                    dispatchers = dispatchers,
                )
            },
            reducer = GuidesReducer(),
        ) {}

    internal sealed interface Action {
        data object LoadInitial : Action
        data class FilterHeroes(val query: String) : Action
    }

    internal sealed interface Message {
        data class SetLoading(val isLoading: Boolean) : Message
        data class SetError(val isError: Boolean) : Message
        data class SetGuides(val guides: List<Guide>) : Message
        data class SetImageResources(val resources: ImageResources) : Message
        data class SetHeroSearchValue(val value: String) : Message
        data class SetHeroSearchFiltered(val filtered: Map<Hero, String>) : Message
    }
}
```

- [ ] **Step 3: Create `GuidesReducer.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.Reducer
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory.Message

internal class GuidesReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State = when (msg) {
        is Message.SetLoading -> copy(isLoading = msg.isLoading)
        is Message.SetError -> copy(isError = msg.isError)
        is Message.SetGuides -> copy(guides = msg.guides)
        is Message.SetImageResources -> copy(imageResources = msg.resources)
        is Message.SetHeroSearchValue -> copy(heroSearchValue = msg.value)
        is Message.SetHeroSearchFiltered -> copy(heroSearchFiltered = msg.filtered)
    }
}
```

- [ ] **Step 4: Create `GuidesExecutor.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.impl.domain

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.mvikotlin.BaseExecutor
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory.Action
import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory.Message
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

private const val SEARCH_DEBOUNCE_MS = 100L

@OptIn(FlowPreview::class)
internal class GuidesExecutor(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) : BaseExecutor<Intent, Action, State, Message, Label>(mainContext = dispatchers.main) {

    private val searchValue = MutableStateFlow("")
    private var debouncePipelineStarted = false

    override suspend fun suspendExecuteAction(action: Action) {
        when (action) {
            Action.LoadInitial -> loadInitial()
            is Action.FilterHeroes -> filterHeroes(action.query)
        }
    }

    override suspend fun suspendExecuteIntent(intent: Intent) {
        when (intent) {
            is Intent.OnHeroSearchValueChange -> {
                dispatch(Message.SetHeroSearchValue(intent.newValue))
                searchValue.value = intent.newValue
            }
            Intent.OnHeroSearchDialogClick -> publish(Label.ShowHeroSearchDialog)
            is Intent.OnHeroSelect -> selectHero(intent.heroId)
            Intent.OnRetry -> suspendExecuteAction(Action.LoadInitial)
        }
    }

    private suspend fun loadInitial() {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))

        val results = coroutineScope {
            awaitAll(
                async { guidesRepository.getGuides() },
                async { resourcesRepository.getHeroImages() },
                async { resourcesRepository.getItemImages() },
                async { resourcesRepository.getAdditionalImages() },
            )
        }
        if (results.any { it.isFailure }) {
            dispatch(Message.SetError(true))
            dispatch(Message.SetLoading(false))
            return
        }

        @Suppress("UNCHECKED_CAST")
        val guides = (results[0] as Result<List<dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide>>).getOrThrow()
        @Suppress("UNCHECKED_CAST")
        val heroImages = (results[1] as Result<Map<dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero, String>>).getOrThrow()
        @Suppress("UNCHECKED_CAST")
        val itemImages = (results[2] as Result<Map<dev.nonoxy.d2buildhelper.features.guides.domain.models.Item, String>>).getOrThrow()
        @Suppress("UNCHECKED_CAST")
        val additional = (results[3] as Result<Map<String, String>>).getOrThrow()

        val imageResources = ImageResources(
            heroImages = heroImages,
            itemImages = itemImages.mapKeys { it.key.id },
            abilityImages = emptyMap(),
            additionalImages = additional,
        )
        val sortedHeroFiltered = withContext(dispatchers.default) {
            heroImages.toList().sortedBy { (hero, _) -> hero.displayName }.toMap()
        }

        dispatch(Message.SetGuides(guides))
        dispatch(Message.SetImageResources(imageResources))
        dispatch(Message.SetHeroSearchFiltered(sortedHeroFiltered))
        dispatch(Message.SetLoading(false))

        if (!debouncePipelineStarted) {
            debouncePipelineStarted = true
            searchValue
                .drop(1) // skip initial empty value
                .debounce(SEARCH_DEBOUNCE_MS)
                .onEach { value -> suspendExecuteAction(Action.FilterHeroes(value)) }
                .launchIn(scope)
        }
    }

    private suspend fun filterHeroes(query: String) {
        val current = state()
        val source = current.imageResources?.heroImages ?: return
        val filtered = withContext(dispatchers.default) {
            source.filter { (hero, _) -> hero.displayName.contains(query.trim(), ignoreCase = true) }
                .toList()
                .sortedBy { (hero, _) -> hero.displayName }
                .toMap()
        }
        dispatch(Message.SetHeroSearchFiltered(filtered))
    }

    private suspend fun selectHero(heroId: Short) {
        dispatch(Message.SetLoading(true))
        guidesRepository.getHeroGuides(heroId)
            .onSuccess { guides -> dispatch(Message.SetGuides(guides)) }
            .onFailure { dispatch(Message.SetError(true)) }
        dispatch(Message.SetLoading(false))
    }
}
```

- [ ] **Step 5: Create `UiGuidesState.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources

data class UiGuidesState(
    val guides: List<Guide> = emptyList(),
    val imageResources: ImageResources? = null,
    val heroSearchValue: String = "",
    val heroSearchFiltered: Map<Hero, String> = emptyMap(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)
```

- [ ] **Step 6: Create `UiGuidesLabel.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation.models

sealed interface UiGuidesLabel {
    data object ShowHeroSearchDialog : UiGuidesLabel
}
```

- [ ] **Step 7: Create `UiGuidesStateMapper.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesState

interface UiGuidesStateMapper : Mapper<GuidesStore.State, UiGuidesState>

internal class UiGuidesStateMapperImpl : UiGuidesStateMapper {
    override fun map(item: GuidesStore.State): UiGuidesState = with(item) {
        UiGuidesState(
            guides = guides,
            imageResources = imageResources,
            heroSearchValue = heroSearchValue,
            heroSearchFiltered = heroSearchFiltered,
            isLoading = isLoading,
            isError = isError,
        )
    }
}
```

- [ ] **Step 8: Create `UiGuidesLabelMapper.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesLabel

interface UiGuidesLabelMapper : Mapper<GuidesStore.Label, UiGuidesLabel>

internal class UiGuidesLabelMapperImpl : UiGuidesLabelMapper {
    override fun map(item: GuidesStore.Label): UiGuidesLabel = when (item) {
        GuidesStore.Label.ShowHeroSearchDialog -> UiGuidesLabel.ShowHeroSearchDialog
    }
}
```

- [ ] **Step 9: Rewrite `GuidesViewModel.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dev.nonoxy.d2buildhelper.core.presentation.viewmodel.BaseViewModel
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesLabel
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesState
import kotlinx.coroutines.flow.mapNotNull

class GuidesViewModel internal constructor(
    private val store: GuidesStore,
    private val stateMapper: UiGuidesStateMapper,
    private val labelMapper: UiGuidesLabelMapper,
) : BaseViewModel<UiGuidesState, UiGuidesLabel>(initialState = UiGuidesState()) {

    init {
        bindAndStart {
            store.states.mapNotNull(stateMapper::map) bindTo ::acceptState
            store.labels.mapNotNull(labelMapper::map) bindTo ::acceptLabel
        }
    }

    fun onHeroSearchValueChange(value: String) = store.accept(Intent.OnHeroSearchValueChange(value))

    fun onHeroSearchDialogClick() = store.accept(Intent.OnHeroSearchDialogClick)

    fun onHeroSelect(heroId: Short) = store.accept(Intent.OnHeroSelect(heroId))

    fun onRetry() = store.accept(Intent.OnRetry)

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
```

- [ ] **Step 10: Create `FeatureGuidesImplModule.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.impl.di

import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesLabelMapperImpl
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.features.guides.presentation.mappers.UiGuidesStateMapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureGuidesImplModule = module {
    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
    singleOf(::UiGuidesStateMapperImpl) bind UiGuidesStateMapper::class
    singleOf(::UiGuidesLabelMapperImpl) bind UiGuidesLabelMapper::class
    viewModelOf(::GuidesViewModel)
}
```

- [ ] **Step 11: Update `AppModule.kt`**

Replace `viewModelOf(::GuidesViewModel)` with `includes(featureGuidesImplModule)`. Final shape:

```kotlin
val appModule = module {
    includes(coreMVIKotlinModule, featureGuidesImplModule)

    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }

    single<ApolloClient> { /* unchanged */ }
    single<SupabaseClient> { /* unchanged */ }

    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::ImageResourcesDataSource) bind ImageResourcesApi::class
    singleOf(::ConstantResourcesDataSource) bind ConstantResources::class

    singleOf(::GuidesRepositoryImpl) bind GuidesRepository::class
    singleOf(::ResourcesRepositoryImpl) bind ResourcesRepository::class
}
```

Drop the unused imports for the old `GuidesViewModel`/UseCases.

- [ ] **Step 12: Rewrite `GuidesScreen.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesLabel
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesErrorView
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesLoadingView
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.HeroFilterDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GuidesScreen(vm: GuidesViewModel = koinViewModel()) {
    val state by vm.state.collectAsState()
    var showHeroDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.label.collect { label ->
            when (label) {
                UiGuidesLabel.ShowHeroSearchDialog -> showHeroDialog = true
            }
        }
    }

    when {
        state.isLoading -> GuidesLoadingView()
        state.isError -> GuidesErrorView(onRetry = vm::onRetry)
        else -> GuidesView(
            state = state,
            onHeroSearchDialogClick = vm::onHeroSearchDialogClick,
        )
    }

    if (showHeroDialog) {
        HeroFilterDialog(
            filteredHeroImageUrls = state.heroSearchFiltered,
            heroSearchValue = state.heroSearchValue,
            onSearchValueChanged = vm::onHeroSearchValueChange,
            onHeroSelect = {
                vm.onHeroSelect(it)
                showHeroDialog = false
            },
            onDismiss = { showHeroDialog = false },
        )
    }
}
```

- [ ] **Step 13: Rewrite `GuidesView.kt`**

Open the existing file and adapt the signature to:
```kotlin
@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onHeroSearchDialogClick: () -> Unit,
)
```

Replace internal references to `viewState: GuidesViewState.Display` with `state: UiGuidesState`. Any sub-view that took `viewState` accepts the same data fields from `state`. Old `eventHandler` calls become direct callbacks.

Show the actual updated body once you've read the current file; the structural change is: drop `when (viewState)`-style dispatch (handled in `GuidesScreen`), accept `UiGuidesState` straight.

- [ ] **Step 14: Rewrite `HeroFilterDialogView.kt` with the local-mirror pattern**

Adapt the search `OutlinedTextField` (or whatever input the dialog uses) to use the local mirror — replace its `value =`/`onValueChange =` with:

```kotlin
var input by remember(heroSearchValue) { mutableStateOf(heroSearchValue) }
OutlinedTextField(
    value = input,
    onValueChange = { newValue ->
        input = newValue
        onSearchValueChanged(newValue)
    },
    /* ... other params unchanged ... */
)
```

Imports to add at the top:
```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
```

- [ ] **Step 15: Update `GuidesErrorView.kt` to accept `onRetry`**

```kotlin
@Composable
internal fun GuidesErrorView(onRetry: () -> Unit) {
    // existing UI plus a button bound to onRetry — keep the layout intent of the original file
}
```

If the original file was a no-op placeholder, keep it minimal but expose the parameter so `GuidesScreen` compiles.

- [ ] **Step 16: Delete obsolete files**

```bash
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/base/BaseViewModel.kt
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesAction.kt
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesEvent.kt
git rm composeApp/src/commonMain/kotlin/dev/nonoxy/d2buildhelper/features/guides/presentation/models/GuidesViewState.kt
```

- [ ] **Step 17: Verify build**

Run:
```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compileTestKotlinJvm detekt
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 18: Smoke-run desktop app**

Run:
```bash
./gradlew :composeApp:run
```
Expected: app launches, guides load, hero search dialog opens, hero filter works, hero select fetches the per-hero list. Quit the app once confirmed.

(If desktop crashes at startup because Koin can't resolve `GuidesStoreFactory` — inspect the stack trace, fix bindings before continuing.)

- [ ] **Step 19: Commit**

```bash
git add -A
git commit -m "refactor: migrate Guides feature to MVIKotlin (Store/Executor/Reducer/Factory + Ui mappers + ViewModel + UI)"
```

---

## Task 10: Replace tests — `GuidesRepositoryTest` and `GuidesExecutorTest`

**Files:**
- Create: `composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/core/data/repository/guides/GuidesRepositoryTest.kt`
- Create: `composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/features/guides/impl/domain/GuidesExecutorTest.kt`
- Create: `composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/data/FakeGuidesApi.kt`
- Create: `composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/data/FakeResourcesRepository.kt`
- Create: `composeApp/src/commonTest/kotlin/dev/nonoxy/d2buildhelper/data/TestCoroutineDispatchers.kt`

Spec reference: *Tests (commonTest)*.

- [ ] **Step 1: Create `TestCoroutineDispatchers.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
internal class TestCoroutineDispatchers(
    private val dispatcher: kotlinx.coroutines.CoroutineDispatcher = UnconfinedTestDispatcher(),
) : CoroutineDispatchers {
    override val io = dispatcher
    override val default = dispatcher
    override val main = dispatcher
}
```

- [ ] **Step 2: Create `FakeGuidesApi.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto

internal class FakeGuidesApi(
    var guides: Result<List<GuideDto>> = Result.success(emptyList()),
    var heroGuides: Result<List<GuideDto>> = Result.success(emptyList()),
) : GuidesApi {
    override suspend fun getGuides(): Result<List<GuideDto>> = guides
    override suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>> = heroGuides
    override suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto> =
        Result.failure(NotImplementedError())
}
```

- [ ] **Step 3: Create `FakeResourcesRepository.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Ability
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Item

internal class FakeResourcesRepository(
    var heroImages: Result<Map<Hero, String>> = Result.success(emptyMap()),
    var itemImages: Result<Map<Item, String>> = Result.success(emptyMap()),
    var abilityImages: Result<Map<Ability, String>> = Result.success(emptyMap()),
    var additionalImages: Result<Map<String, String>> = Result.success(emptyMap()),
) : ResourcesRepository {
    override suspend fun getHeroImages() = heroImages
    override suspend fun getItemImages() = itemImages
    override suspend fun getAbilityImages() = abilityImages
    override suspend fun getAdditionalImages() = additionalImages
}
```

- [ ] **Step 4: Write `GuidesRepositoryTest.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.core.data.repository.guides

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.ItemPurchaseDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.data.FakeGuidesApi
import dev.nonoxy.d2buildhelper.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesRepositoryTest {

    @Test
    fun `getGuides maps Dto to domain and orders end items by time (nulls last)`() = runTest {
        val dto = GuideDto(
            hero = HeroDto(heroId = 1, shortName = "antimage", displayName = "Anti-Mage"),
            steamAccountId = 1L,
            matchId = 100L,
            durationSeconds = 1800,
            playerStats = PlayerStatsDto(
                position = PlayerStatsDto.MatchPlayerPositionTypeDto.POSITION_1,
                isRadiant = true,
                kills = 10, deaths = 1, assists = 5,
                impact = 70, endNeutralItemId = null,
                endItem0Id = 42, endItem1Id = 43, endItem2Id = null,
                endItem3Id = null, endItem4Id = null, endItem5Id = null,
                itemPurchases = listOf(
                    ItemPurchaseDto(itemId = 42, time = 200),
                    ItemPurchaseDto(itemId = 43, time = 100),
                ),
            ),
        )
        val api = FakeGuidesApi(guides = Result.success(listOf(dto)))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides().getOrThrow()

        assertEquals(1, result.size)
        val guide = result.single()
        assertEquals("Anti-Mage", guide.hero.displayName)
        assertEquals(MatchPlayerPosition.POSITION_1, guide.playerStats.position)
        assertEquals(listOf<Int?>(100, 200), guide.playerStats.sortedEndItemPurchases.map { it.time })
    }

    @Test
    fun `getGuides surfaces api failure unchanged`() = runTest {
        val api = FakeGuidesApi(guides = Result.failure(IllegalStateException("network")))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }
}
```

Adjust DTO field names if they differ from the spec — these names come from the original `Guide` / `PlayerStats` files post-rename.

- [ ] **Step 5: Write `GuidesExecutorTest.kt`**

```kotlin
package dev.nonoxy.d2buildhelper.features.guides.impl.domain

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.data.FakeResourcesRepository
import dev.nonoxy.d2buildhelper.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Item
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStats
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesExecutorTest {

    private fun hero(id: Short, name: String) = Hero(heroId = id, shortName = name, displayName = name)
    private fun item(id: Short) = Item(id = id)
    private fun guide(heroId: Short) = Guide(
        hero = hero(heroId, "Hero$heroId"),
        steamAccountId = 1L, matchId = 1L, durationSeconds = 100,
        playerStats = PlayerStats(
            position = MatchPlayerPosition.POSITION_1, isRadiant = true,
            kills = 0, deaths = 0, assists = 0, impact = 0,
            endNeutralItemId = null, sortedEndItemPurchases = emptyList(),
        ),
    )

    @Test
    fun `LoadInitial success transitions to loaded state`() = runTest {
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(listOf(guide(1)))
            override suspend fun getHeroGuides(heroId: Short) = Result.success(listOf(guide(heroId)))
        }
        val resources = FakeResourcesRepository(
            heroImages = Result.success(mapOf(hero(1, "A") to "url_a", hero(2, "B") to "url_b")),
            itemImages = Result.success(mapOf(item(10) to "url_10")),
            additionalImages = Result.success(emptyMap()),
        )

        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        val state = store.state
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(1, state.guides.size)
        assertEquals(2, state.heroSearchFiltered.size)
        store.dispose()
    }

    @Test
    fun `LoadInitial failure sets error and clears loading`() = runTest {
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.failure<List<Guide>>(RuntimeException("boom"))
            override suspend fun getHeroGuides(heroId: Short) = Result.success<List<Guide>>(emptyList())
        }
        val store = GuidesStoreFactory(
            DefaultStoreFactory(), guidesRepo, FakeResourcesRepository(), TestCoroutineDispatchers(),
        ).create()

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isError)
        store.dispose()
    }

    @Test
    fun `OnHeroSelect refreshes guides for the selected hero`() = runTest {
        var lastHeroId: Short? = null
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(listOf(guide(1)))
            override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> {
                lastHeroId = heroId
                return Result.success(listOf(guide(heroId)))
            }
        }
        val resources = FakeResourcesRepository(
            heroImages = Result.success(mapOf(hero(1, "A") to "u")),
            itemImages = Result.success(emptyMap()),
            additionalImages = Result.success(emptyMap()),
        )
        val store = GuidesStoreFactory(
            DefaultStoreFactory(), guidesRepo, resources, TestCoroutineDispatchers(),
        ).create()

        store.accept(Intent.OnHeroSelect(7))
        assertEquals(7.toShort(), lastHeroId)
        assertEquals(7.toShort(), store.state.guides.single().hero.heroId.toShort())
        store.dispose()
    }
}
```

- [ ] **Step 6: Run tests**

```bash
./gradlew :composeApp:jvmTest
```
Expected: all tests pass (3+ in repo test, 3 in executor test).

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "test: replace GetGuidesUseCaseTest with GuidesRepositoryTest and GuidesExecutorTest"
```

---

## Task 11: Update docs and regenerate detekt baseline

**Files:**
- Modify: `.claude/rules/mobile-roadmap.mdc`
- Modify: `.claude/rules/mobile-architecture.mdc`
- Modify: `.claude/rules/mobile-data-layer.mdc`
- Modify: `.claude/rules/mobile-error-handling.mdc`
- Modify: `.claude/rules/mobile-compose.mdc`
- Modify: `CLAUDE.md`
- Regenerate: `linters/detekt/baseline.xml`

Spec reference: *Documentation updates*.

- [ ] **Step 1: Mark roadmap step #1 done**

Edit `.claude/rules/mobile-roadmap.mdc`. Under the migration order section change:
```
1. **State management migration to MVIKotlin.**
   - Introduces Store / Intent / State / Label per feature.
   ...
```
to use a `[x]` checkbox like the earlier entries and leave a short note (`Completed 2026-05-18`). Leave steps #2 and #3 unchanged.

- [ ] **Step 2: Rewrite the architecture rule's VM section**

In `.claude/rules/mobile-architecture.mdc`, replace the section about `BaseViewModel<State, Action, Event>` with the new contract:

```
## State management — MVIKotlin

Each feature exposes a Store through `<feature>/api/store/<Feature>Store.kt` and implements
it under `<feature>/impl/domain` (`<Feature>StoreFactory` + `<Feature>Executor` extending
`BaseExecutor<Intent, Action, State, Message, Label>` + `<Feature>Reducer`). UI talks to
the store via `<feature>/presentation/<Feature>ViewModel.kt` (extends
`core/presentation/viewmodel/BaseViewModel<UiState, UiLabel>`); Store.State and Store.Label
are translated through `<feature>/presentation/mappers/Ui<Feature>StateMapper` and
`Ui<Feature>LabelMapper`.

Visibility: Store + Intent/State/Label are public; StoreFactory/Executor/Reducer/Action/Message
and mapper Impls are `internal`. Feature DI lives in
`<feature>/impl/di/Feature<...>ImplModule.kt`.

Loading is a `Store.State.isLoading` flag (no Result variant). One-shot side effects go
through `Label`. Programmatic VM-public functions delegate `store.accept(Intent.X)`.

The Store must be disposed in `ViewModel.onCleared()` — see `GuidesViewModel`.
```

- [ ] **Step 3: Rewrite the data-layer rule**

In `.claude/rules/mobile-data-layer.mdc`, drop all `RequestResult`/`Flow<RequestResult<T>>` references. Add:

```
Repositories expose `suspend fun (): Result<T>`. DTO models live under `core/data/...`
with the `Dto` suffix and are `internal`. Domain models live next to their consumer
(`features/<feature>/domain/models/`). The DTO→domain mapping is the **repository**'s
responsibility, not a separate use-case. Errors surface as `Result.failure(throwable)`
via `coRunCatching`; loading is tracked as a Store.State flag.

`CoroutineDispatchers` is injected (interface in `common/coroutines/`). Never reference
`Dispatchers.IO` / `Dispatchers.Default` directly in commonMain — go through the abstraction.
```

- [ ] **Step 4: Rewrite the error-handling rule**

In `.claude/rules/mobile-error-handling.mdc`, replace `runCatching` discipline with:

```
- In suspend code, use `coRunCatching { ... }` from `common/extensions/CoroutineExtensions.kt`.
  It rethrows `CancellationException` and converts other throwables to `Result.failure`.
- Plain `runCatching` is only acceptable in non-suspend code paths.
- Repositories log via `Napier.e(...)` in their catch blocks; presentation code does not
  swallow errors silently.
```

- [ ] **Step 5: Add the local-mirror text-input convention to the Compose rule**

In `.claude/rules/mobile-compose.mdc`, append a section:

```
## Local-mirror text inputs

When a text field is bound to a Store-side value with any latency between keystroke and
state update (debounced filtering, async pipelines, MVI round-trip), mirror the value
locally inside the composable to keep rendering instant:

```kotlin
var input by remember(externalValue) { mutableStateOf(externalValue) }
OutlinedTextField(
    value = input,
    onValueChange = {
        input = it
        onValueChange(it)   // propagate to Store/VM
    },
    /* ... */
)
```

`remember(externalValue)` lets external state still resync the input on programmatic resets.
Used in `HeroFilterDialog`; apply the same pattern for any future field with similar latency.
```

- [ ] **Step 6: Update `CLAUDE.md`**

In the `## Stack` section, append (after the existing Koin line):

```
- MVIKotlin 4.4.0 (`core/mvikotlin/BaseExecutor`, `LoggingStoreFactory` wired through Napier).
- Napier 2.7.1 (logger; `Napier.base(DebugAntilog(...))` on every platform entry).
- moko-mvvm 0.16.1 (CFlow/CStateFlow for iOS contract on `BaseViewModel`).
```

In the `## Architecture` section, update the tree comment for `features/guides` to show:
```
features/guides/
├── api/store/GuidesStore.kt
├── impl/
│   ├── di/FeatureGuidesImplModule.kt
│   └── domain/{GuidesStoreFactory, GuidesExecutor, GuidesReducer}.kt
└── presentation/
    ├── GuidesViewModel.kt
    ├── models/{UiGuidesState, UiGuidesLabel}.kt
    ├── mappers/{UiGuidesStateMapper, UiGuidesLabelMapper}.kt
    └── ui/
```

In the `## Key Gotchas` section, replace the `runCatching` line with `coRunCatching` guidance and add:

```
- All repositories return `suspend fun (): Result<T>`. There is no `RequestResult` or
  UseCase layer — DTO→domain mapping lives in the repository.
- Loading is a `Store.State.isLoading` boolean, not a `Result` variant.
- `CoroutineDispatchers` is the only way to obtain dispatchers in commonMain — inject it.
- `BaseViewModel.onCleared()` must call `store.dispose()` — pattern shown in `GuidesViewModel`.
- `Napier.base(DebugAntilog(...))` is called once per platform entry. `Napier.base` appends
  antilogs — if an entry can be re-created, wrap with `Napier.takeLogarithm()` first.
```

- [ ] **Step 7: Regenerate detekt baseline**

```bash
./gradlew detektBaseline
```

Inspect the diff:
```bash
git diff linters/detekt/baseline.xml | head -120
```

If the new baseline removes warnings (counts go down), commit as-is. If it adds new warnings, review them — silenced warnings should be intentional.

- [ ] **Step 8: Final verification**

```bash
./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64 :composeApp:jvmTest detekt :composeApp:lintDebug
```
Expected: `BUILD SUCCESSFUL` on every task. No new lint regressions.

- [ ] **Step 9: Commit**

```bash
git add -A
git commit -m "docs: align rules and CLAUDE.md with MVIKotlin migration; regenerate detekt baseline"
```

---

## Task 12: Push branch and open PR

- [ ] **Step 1: Push the branch**

```bash
git push -u origin refactor/mvikotlin-and-helpers
```

- [ ] **Step 2: Open the pull request**

```bash
gh pr create --base develop-cmp --title "refactor: migrate to MVIKotlin (Store/Executor/Reducer) and replace RequestResult/UseCase layer" --body "$(cat <<'EOF'
## Summary
- Replaces `BaseViewModel<State, Action, Event>` with the KMMTemplate MVIKotlin stack: per-feature Store/Reducer/Executor/StoreFactory under `features/<feature>/{api,impl,presentation}` (package layout only — multi-module split is roadmap step #2).
- Drops `RequestResult` in favor of plain `kotlin.Result<T>`. Repositories expose `suspend fun (): Result<T>`. Loading is a `Store.State.isLoading` flag.
- Removes the per-feature UseCase layer; DTO→domain mapping moves into the repository. DTO models gain the `Dto` suffix and become `internal`; domain models drop the `UI` suffix.
- Adds shared helpers: `coRunCatching`, `Mapper`, `OneTimeEvent`, `CoroutineDispatchers`, plus Napier logging on all three platform entry points.
- `HeroFilterDialog` adopts the toir-mobile local-mirror pattern for the search field so keystrokes are never gated on the Store round-trip.

## Test plan
- [ ] `./gradlew :composeApp:jvmTest` passes (replaces `GetGuidesUseCaseTest` with `GuidesRepositoryTest` + `GuidesExecutorTest`).
- [ ] `./gradlew :composeApp:compileKotlinJvm :composeApp:compileDebugKotlinAndroid :composeApp:compileKotlinIosSimulatorArm64` succeeds.
- [ ] `./gradlew detekt :composeApp:lintDebug` succeeds (baseline regenerated).
- [ ] Manual smoke test: desktop app loads guides, hero filter dialog opens, search filters, hero select refetches guides, error retry works.
- [ ] CI on PR is green.
EOF
)"
```

- [ ] **Step 3: Verify CI is green**

```bash
gh pr checks
```
Wait for all checks to pass. If anything fails, do not merge — fix and push.

Done — PR is open against `develop-cmp` and ready for review.

---

## Self-review checklist (run after writing this plan; fix issues inline)

1. **Spec coverage** — every section of `2026-05-17-mvikotlin-migration-and-helpers-design.md` is touched:
   - Common utilities → Task 2. ✓
   - Logging → Task 3. ✓
   - Core MVIKotlin → Task 4. ✓
   - Presentation core → Task 5. ✓
   - Data layer (Result + Repositories) → Task 8. ✓
   - Guides feature migration → Task 9. ✓
   - DI wiring → Tasks 4, 8, 9. ✓
   - Tests → Task 10. ✓
   - Versions / deps → Task 1. ✓
   - Documentation updates → Task 11. ✓
   - Commit plan (7 commits → adjusted to 11 commits across 11 tasks) — same intent, finer granularity. ✓
   - Risks: moko-mvvm JVM artifact resolution flagged in Task 5; detekt baseline regenerated in Task 11. ✓

2. **Type-consistency check**
   - Domain types: `Guide`, `Hero`, `Item`, `Ability`, `PlayerStats`, `ItemPurchase`, `MatchPlayerPosition`, `ImageResources` — used consistently across Tasks 6, 7, 8, 9, 10.
   - DTO types: `GuideDto`, `HeroDto`, `ItemDto`, `AbilityDto`, `PlayerStatsDto`, `ItemPurchaseDto`, `MatchPlayerPositionTypeDto`, `DetailGuideDto` — used in data layer only, Tasks 6, 8, 10.
   - Repository methods: `getGuides()`, `getHeroGuides(heroId)`, `getHeroImages()`, `getItemImages()`, `getAbilityImages()`, `getAdditionalImages()` — consistent across Tasks 7, 8, 9, 10.
   - VM methods: `onHeroSearchValueChange`, `onHeroSearchDialogClick`, `onHeroSelect`, `onRetry` — consistent in Tasks 9 (VM) and 9 (Screen).

3. **Placeholders** — none of "TBD / TODO / similar to Task N / fill in details".

4. **Build verifiability** — each task ends with a verification command and `BUILD SUCCESSFUL` expectation. Tasks 8 and 9 are explicitly noted as the transitional and replacement points; tests only become green at Task 10.
