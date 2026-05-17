# MVIKotlin migration and supporting helpers

Date: 2026-05-17
Branch: `refactor/mvikotlin-and-helpers` (PR target: `develop-cmp`)
Roadmap step covered: **#1 — State management migration to MVIKotlin** (`mobile-roadmap.mdc`).

## Goals

1. Replace `BaseViewModel<State, Action, Event>` with the canonical KMMTemplate MVIKotlin stack (`Store` + `Reducer` + `Executor` + presentation `BaseViewModel<State, Label>` + UI mappers).
2. Remove `RequestResult` in favor of `kotlin.Result<T>`.
3. Remove the per-feature `UseCase` layer — repositories own DTO↔domain mapping.
4. Introduce shared building blocks needed by the new architecture: `coRunCatching`, `Result` extensions, `Mapper<From, To>`, `OneTimeEvent`, `CoroutineDispatchers`, Napier logger.
5. Migrate the single live feature (`features/guides/*`) to the new pattern end-to-end. Keep `DetailGuideScreen` as a stub.

Non-goals (explicit, deferred to later roadmap steps):

- Multi-module split — Gradle module per `api/impl/presentation` is roadmap step #2. Here we mirror the package layout so files relocate without internal churn.
- `moko-resources` — roadmap step #3.
- New navigation / new screens / new business features.

## Architectural pattern

End state matches KMMTemplate (`shared/feature-demo-first` is the reference). Per feature:

```
features/<feature>/
├── api/store/<Feature>Store.kt        # interface Store<Intent, State, Label>
├── impl/
│   ├── di/Feature<...>ImplModule.kt   # Koin: StoreFactory, Mappers, ViewModel
│   └── domain/
│       ├── <Feature>StoreFactory.kt    # sealed Action, sealed Message (internal)
│       ├── <Feature>Executor.kt        # BaseExecutor<Intent, Action, State, Message, Label>
│       └── <Feature>Reducer.kt
└── presentation/
    ├── <Feature>ViewModel.kt          # BaseViewModel<UiState, UiLabel>
    ├── models/
    │   ├── Ui<Feature>State.kt
    │   └── Ui<Feature>Label.kt
    ├── mappers/
    │   ├── Ui<Feature>StateMapper.kt   # interface + Impl
    │   └── Ui<Feature>LabelMapper.kt
    └── ui/                             # Compose layer
```

Visibility:

- `Store` interface, `Intent`, `State`, `Label` → public (consumed by presentation).
- `StoreFactory`, `Executor`, `Reducer`, `Action`, `Message` → `internal`.
- `Ui<Feature>StateMapper`/`LabelMapper` → public interface, `Impl` is `internal`.
- `Feature<...>ImplModule` is the only Koin entry the rest of the app sees.

When the multi-module split lands (roadmap #2), each package becomes its own Gradle module and `internal` becomes "intra-module" without changing source.

## Data layer

### Repository contracts (replace `Flow<RequestResult<T>>`)

All current "one-shot" remote / local fetches collapse to plain `suspend` functions returning `Result<T>`. The `RequestResult.InProgress` variant disappears — loading is **Store.State** (`isLoading: Boolean`), not a value carrier.

- `GuidesRepository` (new, splits responsibilities of `GetGuidesUseCase`):
  ```kotlin
  interface GuidesRepository {
      suspend fun getGuides(): Result<List<Guide>>
      suspend fun getHeroGuides(heroId: Short): Result<List<Guide>>
  }
  ```
  Impl wraps `GuidesApi`, runs on `dispatchers.io`, returns `Result.failure` for network/GraphQL errors via `coRunCatching`. DTO→domain mapping (`GuideDto.toDomain()`, `PlayerStatsDto.toDomain()` including `sortedEndItemPurchases` logic) lives here, not in any use-case.
- `ResourcesRepository` — now an interface, current concrete becomes `ResourcesRepositoryImpl` (consistent with `GuidesRepository`/`Impl` and KMMTemplate convention):
  ```kotlin
  interface ResourcesRepository {
      suspend fun getImageResources(): Result<ImageResources>
  }
  ```
  Impl orchestrates the four sub-fetches (`hero/item/ability/additional`) in parallel via `coroutineScope { async/async + awaitAll }`, keeps the in-memory cache (the same four nullable fields), maps to domain types at the boundary. The "conditional fetch" booleans of the old `GetImagesUseCase` are dropped — the only live caller wanted all four, so the conditional combinatorics weren't paying for themselves.
- `GuidesApi` / `GuidesDataSource` — `suspend fun (): Result<List<GuideDto>>`. The `flow { … merge(start, … ) }` ceremony goes away because there is no `InProgress` emission to keep.

### Type renames (clean DTO↔domain border)

| Layer | Before | After |
| --- | --- | --- |
| data DTO | `core/data/api/.../models/Guide` | `GuideDto` (`internal`) |
| data DTO | `Item`, `Ability`, `Hero`, `PlayerStats`, `DetailGuide`, `ImageResources`, `ItemPurchase` (all in `core/data/...`) | `*Dto` (`internal`) |
| domain | `features/guides/domain/models/GuideUI` | `Guide` |
| domain | `HeroUI`, `ItemPurchaseUI`, `PlayerStatsUI`, `MatchPlayerPositionType` | `Hero`, `ItemPurchase`, `PlayerStats`, `MatchPlayerPosition` |
| domain | `ImageResources` (currently mixes domain Hero + data Hero) | `ImageResources` in `features/guides/domain/models`, keyed by domain `Hero` |
| presentation | n/a (current code reuses `*UI` directly in `ViewState`) | `UiGuidesState`, `UiGuidesLabel` |

The `Ui` prefix is reserved for the presentation layer where it now means something. Domain types drop the `UI` suffix because they are no longer "UI-shaped" — they are the canonical model.

## Errors and async

- `common/extensions/CoroutineExtensions.kt`:
  ```kotlin
  suspend inline fun <T> coRunCatching(
      tryBlock: suspend () -> T,
      catchBlock: (Throwable) -> Result<T> = { it.wrapResultFailure() },
  ): Result<T>
  ```
  Re-throws `CancellationException`. Default `catchBlock` lets call sites omit the closure. Signature matches KMMTemplate.
- `common/extensions/ResultExtensions.kt`:
  ```kotlin
  fun <T> T.wrapResultSuccess(): Result<T> = Result.success(this)
  fun <T> Throwable.wrapResultFailure(): Result<T> = Result.failure(this)
  ```
- Apollo error mapping: in `GuidesDataSource` the `ApolloResponse` path becomes
  ```kotlin
  coRunCatching {
      val resp = apolloClient.query(...).execute()
      when {
          resp.hasErrors() -> error(resp.errors.toString())
          resp.exception != null -> throw resp.exception!!
          else -> resp.dataOrThrow()
      }
  }
  ```
  No more `toRequestResult()` extension.

`CoroutineDispatchers` (`common/coroutines/CoroutineDispatchers.kt`) is an interface with `io / default / main` + a default `CoroutineDispatchersImpl` over `Dispatchers.IO / Default / Main.immediate`. Repositories and `BaseExecutor` consume the interface — tests substitute `UnconfinedTestDispatcher`.

## MVIKotlin core

- `core/mvikotlin/BaseExecutor.kt` — direct port from KMMTemplate (`CoroutineExecutor<Intent, Action, State, Message, Label>` with `final override executeIntent/executeAction` launching into the executor scope, plus open `suspendExecuteIntent`/`suspendExecuteAction`).
- `core/mvikotlin/di/CoreMVIKotlinModule.kt`:
  ```kotlin
  val coreMVIKotlinModule = module {
      factory<StoreFactory> {
          val napierLogger = object : Logger { override fun log(text: String) = Napier.v(text) }
          LoggingStoreFactory(delegate = DefaultStoreFactory(), logger = napierLogger)
      }
  }
  ```

## Presentation core

- `core/presentation/viewmodel/BaseIosViewModel.kt` — interface exposing `state: CStateFlow<State>`, `label: CFlow<Label>`, `onCleared/start/stop`. Required by Compose-on-iOS contract via moko-mvvm wrappers (we keep this even though no native Swift UI consumes it yet — the contract is cheap and avoids reshuffling when a Swift entry point is added).
- `core/presentation/viewmodel/BaseViewModel.kt` — direct port from KMMTemplate. Extends `androidx.lifecycle.ViewModel`, exposes `state` / `label`, owns `Binder?`, exposes `bindAndStart { … }` helper that calls MVIKotlin `bind { … bindTo ::acceptState; … bindTo ::acceptLabel }`, disposes the bound store via the subclass's `onCleared`. Uses `Napier.v` for lifecycle traces.

## Common utilities

- `common/utils/OneTimeEvent.kt` — `fun <T> OneTimeEvent(): Channel<T> = Channel(Channel.BUFFERED)` (port).
- `common/mappers/Mapper.kt` — `interface Mapper<From, To> { fun map(item: From): To; fun map(list: List<From>): List<To> = list.map(::map) }` (port).

## Logging

- Add `io.github.aakira:napier:2.7.1` (commonMain).
- Init on each platform entry point. `Napier.base(...)` *appends* an antilog rather than replacing, so re-entry would double-log. The platform entries here run once per process, so a guard isn't strictly needed; if a future entry can be re-created (e.g., test scenarios), wrap with `Napier.takeLogarithm(); Napier.base(...)`:
  - `AndroidApp.onCreate`: `Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))` *before* `initKoin`.
  - `jvmMain/main.kt`: same, before `initKoin` and `application { … }`.
  - `iosMain/MainViewController` (composable factory): same, before `initKoin`.
- Library logging hooks: `LoggingStoreFactory` logger and `BaseViewModel` lifecycle traces use `Napier.v`. `coRunCatching` callers may `Napier.e` in `catchBlock` at their discretion (e.g., repositories).

## Guides feature (concrete migration)

### `GuidesStore` (api)

```kotlin
interface GuidesStore : Store<GuidesStore.Intent, GuidesStore.State, GuidesStore.Label> {

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

### `GuidesStoreFactory` / `Executor` / `Reducer` (impl)

`StoreFactory.Action`:
- `LoadInitial` (bootstrapper-emitted): triggers parallel `getGuides()` + `getImageResources()`.
- `FilterHeroes(value: String)`: triggered by debounced search input.

`StoreFactory.Message`:
- `SetLoading(Boolean)`, `SetError(Boolean)`, `SetGuides(List<Guide>)`, `SetImageResources(ImageResources)`, `SetHeroSearchValue(String)`, `SetHeroSearchFiltered(Map<Hero, String>)`.

`GuidesExecutor`:
- Holds `private val searchValue = MutableStateFlow("")`.
- In `executeAction`:
  - `LoadInitial`: `dispatch(SetLoading(true))`, `dispatch(SetError(false))`, awaits both repository calls in parallel (`coroutineScope { async/async + awaitAll }`), dispatches `SetGuides`/`SetImageResources` on success, `SetError(true)` on any failure, finally `SetLoading(false)`. Also seeds `searchValue.collect.debounce(SEARCH_DEBOUNCE_MS)` into `dispatch(FilterHeroes(...))` once.
  - `FilterHeroes`: computes filtered map on `dispatchers.default`, dispatches `SetHeroSearchFiltered`.
- In `executeIntent`:
  - `OnHeroSearchValueChange(v)` → `dispatch(SetHeroSearchValue(v))` + `searchValue.value = v` (UI sees text instantly; filtering is debounced).
  - `OnHeroSearchDialogClick` → `publish(Label.ShowHeroSearchDialog)`.
  - `OnHeroSelect(id)` → re-fetch via `getHeroGuides(id)` with the same loading/error pattern.
  - `OnRetry` → re-dispatch `LoadInitial`.

`GuidesReducer`: straight `when (msg)` → `state.copy(...)`.

`GuidesStoreFactory`:

```kotlin
internal class GuidesStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) {
    fun create(): GuidesStore = object :
        GuidesStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GuidesStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.LoadInitial),
            executorFactory = { GuidesExecutor(repository, resourcesRepository, dispatchers) },
            reducer = GuidesReducer(),
        ) {}
}
```

### Presentation

- `UiGuidesState` mirrors `GuidesStore.State` field-for-field (for now identity-mapping). It is a separate class so future UI-only fields (formatted strings, derived flags) live in presentation without leaking back to Store.
- `UiGuidesLabel.ShowHeroSearchDialog` — same singleton shape as `Label.ShowHeroSearchDialog`.
- `UiGuidesStateMapperImpl.map(state)` → `UiGuidesState(...)`; `UiGuidesLabelMapperImpl.map(label)` → `UiGuidesLabel.ShowHeroSearchDialog`.
- `GuidesViewModel(store, stateMapper, labelMapper)` extends `BaseViewModel<UiGuidesState, UiGuidesLabel>(initialState = UiGuidesState())`. `init { bindAndStart { store.states.mapNotNull(stateMapper::map) bindTo ::acceptState; store.labels.mapNotNull(labelMapper::map) bindTo ::acceptLabel } }`. Public functions: `onHeroSearchValueChange(String)`, `onHeroSearchClick()`, `onHeroSelect(Short)`, `onRetry()` — each delegates `store.accept(Intent.X)`. `onCleared { store.dispose(); super.onCleared() }`.

### UI

- `GuidesScreen.kt`:
  ```kotlin
  @Composable
  internal fun GuidesScreen(vm: GuidesViewModel = koinViewModel()) {
      val state by vm.state.collectAsState()
      var showHeroDialog by rememberSaveable { mutableStateOf(false) }
      LaunchedEffect(vm) {
          vm.label.collect { label ->
              when (label) { UiGuidesLabel.ShowHeroSearchDialog -> showHeroDialog = true }
          }
      }
      when {
          state.isLoading -> GuidesLoadingView()
          state.isError -> GuidesErrorView(onRetry = vm::onRetry)
          else -> GuidesView(state = state, onEvent = ... )
      }
      if (showHeroDialog) {
          HeroFilterDialog(
              filteredHeroImageUrls = state.heroSearchFiltered,
              heroSearchValue = state.heroSearchValue,
              onSearchValueChanged = vm::onHeroSearchValueChange,
              onHeroSelect = { vm.onHeroSelect(it); showHeroDialog = false },
              onDismiss = { showHeroDialog = false },
          )
      }
  }
  ```
  Old `clearAction()` / `viewActions().collectAsState(null)` ceremony is gone — Label is observed once via `LaunchedEffect`.
- `GuidesView.kt`, `GuidesTopBarView.kt`, `HeroFilterDialogView.kt`, `GuideListView.kt`, `GuideItemView.kt`, `GuidesErrorView.kt` — accept `UiGuidesState` (or its slices) directly. No business logic shifts; this is signature plumbing.

## DI wiring

`appModule` after migration:

```kotlin
val appModule = module {
    includes(coreMVIKotlinModule, featureGuidesImplModule)
    singleOf(::CoroutineDispatchersImpl) bind CoroutineDispatchers::class

    // Apollo / Supabase as today
    single<ApolloClient> { ... }
    single<SupabaseClient> { ... }

    // Network / local datasources (unchanged API surface, internal impls return Result)
    singleOf(::GuidesDataSource) bind GuidesApi::class
    singleOf(::ImageResourcesDataSource) bind ImageResourcesApi::class
    singleOf(::ConstantResourcesDataSource) bind ConstantResources::class

    // Repositories
    singleOf(::GuidesRepositoryImpl) bind GuidesRepository::class
    singleOf(::ResourcesRepositoryImpl) bind ResourcesRepository::class
}
```

`featureGuidesImplModule`:

```kotlin
internal val featureGuidesImplModule = module {
    factory { GuidesStoreFactory(get(), get(), get(), get()).create() }
    singleOf(::UiGuidesStateMapperImpl) bind UiGuidesStateMapper::class
    singleOf(::UiGuidesLabelMapperImpl) bind UiGuidesLabelMapper::class
    viewModelOf(::GuidesViewModel)
}
```

`GetGuidesUseCase` / `GetImagesUseCase` registrations are removed.

## Tests (`commonTest`)

Existing `GetGuidesUseCaseTest` + `FakeGuidesDataSource` are deleted. Replacements:

- `data/repository/guides/GuidesRepositoryTest` — verifies DTO→domain mapping (`sortedEndItemPurchases` ordering, nullable defaults), success and failure paths over a `FakeGuidesApi` returning canned `Result`. Uses `runTest`.
- `features/guides/impl/domain/GuidesExecutorTest` — drives Intents into the executor through `Store` (`TestStoreFactory` from `mvikotlin-extensions-coroutines`), asserts state transitions for: initial success, initial failure, hero search debounce, hero select success, retry. Uses fake repositories returning canned `Result`. `CoroutineDispatchers` substituted with `UnconfinedTestDispatcher` for all three slots.

Mapper unit tests are out of scope (identity mapping for now); they get tests when UI shape diverges from Store shape.

## Versions and dependencies

`gradle/libs.versions.toml` additions (versions verified 2026-05-17):

```toml
mvikotlin = "4.4.0"
napier = "2.7.1"
moko-mvvm = "0.16.1"
```

Library entries:

```toml
mvikotlin-core         = { module = "com.arkivanov.mvikotlin:mvikotlin",                       version.ref = "mvikotlin" }
mvikotlin-main         = { module = "com.arkivanov.mvikotlin:mvikotlin-main",                  version.ref = "mvikotlin" }
mvikotlin-logging      = { module = "com.arkivanov.mvikotlin:mvikotlin-logging",               version.ref = "mvikotlin" }
mvikotlin-coroutines   = { module = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines", version.ref = "mvikotlin" }
napier                 = { module = "io.github.aakira:napier",                                  version.ref = "napier" }
moko-mvvm-flow         = { module = "dev.icerock.moko:mvvm-flow",                               version.ref = "moko-mvvm" }
```

`composeApp/build.gradle.kts` — added to `commonMain.dependencies { ... }`:

- `implementation(libs.mvikotlin.core)`, `.main`, `.logging`, `.coroutines`
- `implementation(libs.napier)`
- `implementation(libs.moko.mvvm.flow)`

`commonTest.dependencies` additionally gets `mvikotlin-extensions-coroutines` test artifacts (for `TestStoreFactory`).

## Documentation updates (in same PR)

- `.claude/rules/mobile-roadmap.mdc` — mark step #1 done; keep #2/#3 as remaining.
- `.claude/rules/mobile-architecture.mdc` — rewrite the VM section: `BaseViewModel<State, Label>` + `BaseExecutor` + Store/Reducer/Factory + Ui mappers. Reference the package layout.
- `.claude/rules/mobile-data-layer.mdc` — replace `RequestResult<T>` references with `Result<T>` and `suspend fun` repositories; drop the `RequestResult.Success | InProgress | Error` matrix; note that DTO→domain mapping is repository responsibility.
- `.claude/rules/mobile-error-handling.mdc` — `coRunCatching` is the suspend-context norm; `runCatching` only in non-suspend code.
- `CLAUDE.md` —
  - Stack: add MVIKotlin 4.4.0, Napier 2.7.1, moko-mvvm 0.16.1.
  - Architecture: new per-feature package layout (`api/impl/presentation`).
  - Gotchas:
    - `coRunCatching` everywhere in suspend; never bare `try/catch` swallowing `CancellationException`.
    - `Store` must be `dispose()`d in `ViewModel.onCleared()` — `BaseViewModel` does it for `bindAndStart`'d stores via subclass override.
    - `Napier.base(...)` is called per platform entry, idempotent.
    - `CoroutineDispatchers` is injected; never reference `Dispatchers.IO` directly in commonMain.

## Commit plan (single branch, one PR)

1. `chore: add common helpers (coRunCatching, Result/Coroutine extensions, Mapper, OneTimeEvent, CoroutineDispatchers)`
2. `chore: wire Napier on all three platform entry points`
3. `feat: add core/mvikotlin and core/presentation (BaseExecutor, BaseViewModel, BaseIosViewModel, LoggingStoreFactory module)`
4. `refactor: replace RequestResult with Result and drop UseCase layer (DTO/domain rename, repositories return Result, mapping moved out of usecases)`
5. `refactor: migrate Guides feature to MVIKotlin (Store/Executor/Reducer/Factory + Ui mappers + ViewModel + Compose layer)`
6. `test: replace GetGuidesUseCaseTest with GuidesRepositoryTest and GuidesExecutorTest`
7. `docs: update .claude/rules and CLAUDE.md for MVIKotlin + Result migration; regenerate detekt baseline`

Each commit must build (`./gradlew compileJvm compileAndroidDebug compileIosSimulatorArm64 jvmTest detekt lintDebug`). The Result migration commit is the largest — that one needs to land complete or not at all (no intermediate state with half-replaced `RequestResult`).

## Risks and mitigations

- **`bindAndStart` interaction with Compose Multiplatform on iOS**: Compose-on-iOS calls Kotlin directly, so we never use `BaseIosViewModel.start/stop`. Risk: `BaseViewModel` traces `Napier.v` from `start()` if called by mistake. Mitigation: leave `start/stop` as no-op pathways for now; document in `mobile-architecture.mdc` that they are reserved for Swift consumers.
- **moko-mvvm `CFlow`/`CStateFlow` on JVM target**: moko-mvvm-flow declares JVM artifacts; should resolve. Verify during commit #3 build that JVM compile is green. If JVM target is missing, fall back to a narrow shim (`expect class CStateFlow<T>` actual=`StateFlow<T>`) — but only if needed; do not pre-empt.
- **Detekt rules on internal sealed types**: large rename + new sealed interfaces may bump warnings. Regenerate baseline as the last step (commit #7); review delta before pushing.
- **Apollo error mapping divergence**: the previous `toRequestResult()` distinguished `hasErrors` (GraphQL) from `exception` (network). We collapse both into `Result.failure` with the throwable carrying enough context. If a feature later needs to branch on those — introduce a sealed error type then, not preemptively.

## Out of scope (explicit)

- `DetailGuide` feature scaffolding — stub stays empty until the screen gets behavior.
- New ktor configuration, new Apollo plugins, new lints.
- Splitting `composeApp` into Gradle modules.
- `moko-resources` migration.
- Any change to icons / strings / theme.
