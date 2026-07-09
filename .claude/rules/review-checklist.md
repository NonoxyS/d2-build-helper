# Review Checklist

LLM reviewer answers each item **yes / no + cited line**. "Yes" = violation found.

## Architecture / Layers → `mobile-architecture.mdc#presentation-layer-discipline`

- [ ] Domain type (`Hero`, `Item`, `Guide`, …) reaches a `@Composable` directly (not via `Ui*` model)?
- [ ] `ImmutableList` / `ImmutableMap` used in domain model or `Store.State` (not only in `Ui*`)?
- [ ] Mapper calls a Repository or runs `suspend` code?
- [ ] `Map` in presentation keyed by domain object instead of flat ID (`Short`, `Int`, `String`)?
- [ ] New Repository: interface not in `domain/repository/` or Impl not in `data/repository/`?
- [ ] `Store.Label` / `UiXLabel` / `UiXLabelMapper(Impl)` removed or missing from a feature?

## DI / Koin → `mobile-architecture.mdc#koin-di`

- [ ] `factoryOf(::Impl) { bind<I>() }` or `singleOf(::Impl) { bind<I>() }` used (impl leaks into graph)?
- [ ] Parameterless binding uses `single<I> { new(::Impl) }` instead of `singleOf<I>(::Impl)`?
- [ ] Dependency resolved via `KoinPlatform.getKoin().get()` inside a class body?

## Compose / Design System → `mobile-compose.mdc#design-system`

- [ ] `MaterialTheme.colorScheme.*` or `MaterialTheme.typography.*` accessed directly in `feature-*/ui/`?
- [ ] Bare M3 component (`Button`, `TextField`, `Chip`, …) without a `D2*` wrapper in `feature-*/ui/`?
- [ ] String literal in `Text("…")` instead of moko-resources?
- [ ] `Color(0x…)` / `Color(0xFF…)` literal instead of DS token?
- [ ] UI-emitting `@Composable` missing `modifier: Modifier = Modifier` parameter? (Exception: root-screen composables that call `koinViewModel<T>()`.)

## Navigation → `mobile-architecture.mdc#compose-navigation`

- [ ] Route object placed in `:shared:core-navigation` instead of `Feature<X>ScreenApi.kt`?
- [ ] `Feature<X>ScreenApi.kt` missing `navigateTo<X>Screen(...)` extension or wrong order (Route → navigateTo → composable)?

## Error Handling → `mobile-error-handling.mdc`

- [ ] `runCatching { }` or bare `try/catch` in a `suspend` function instead of `coRunCatching`?
- [ ] Repository throws to caller instead of returning `Result.failure(...)`?
- [ ] `println(...)` used instead of `Napier.*`?

## Code Quality → `mobile-code-rules.mdc`

- [ ] `!!` used without a preceding null-filter / exhaustive check?
- [ ] Comment that describes *what* the code does (not *why*)?
