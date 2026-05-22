package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.mvikotlin.BaseExecutor
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.ImageResources
import dev.nonoxy.d2buildhelper.core.domain.Item
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory.Action
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory.Message
import io.github.aakira.napier.Napier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
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
    private var lastFailedRetry: (suspend () -> Unit)? = null

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
            Intent.OnRetry -> (lastFailedRetry ?: { suspendExecuteAction(Action.LoadInitial) }).invoke()
        }
    }

    private suspend fun loadInitial() {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))

        val results = coroutineScope {
            val guidesDef = async { guidesRepository.getGuides() }
            val heroesDef = async { resourcesRepository.getHeroImages() }
            val itemsDef = async { resourcesRepository.getItemImages() }
            LoadResults(
                guides = guidesDef.await(),
                heroImages = heroesDef.await(),
                itemImages = itemsDef.await(),
            )
        }
        val firstFailure = listOf(results.guides, results.heroImages, results.itemImages)
            .firstOrNull { it.isFailure }
        if (firstFailure != null) {
            Napier.e(throwable = firstFailure.exceptionOrNull(), message = "GuidesExecutor.loadInitial failed")
            lastFailedRetry = { suspendExecuteAction(Action.LoadInitial) }
            dispatch(Message.SetError(true))
            dispatch(Message.SetLoading(false))
            return
        }

        val guides = results.guides.getOrThrow()
        val heroImages = results.heroImages.getOrThrow()
        val itemImages = results.itemImages.getOrThrow()
        val imageResources = ImageResources(
            heroImages = heroImages,
            itemImages = itemImages,
            // abilities are not shown on the guides screen
            abilityImages = emptyMap(),
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
                .drop(1)
                .debounce(SEARCH_DEBOUNCE_MS)
                .onEach { suspendExecuteAction(Action.FilterHeroes(it)) }
                .launchIn(scope)
        }
        lastFailedRetry = null
    }

    private suspend fun filterHeroes(query: String) {
        val source = state().imageResources?.heroImages ?: return
        val filtered = withContext(dispatchers.default) {
            val q = query.trim()
            source
                .filter { (hero, _) -> hero.displayName.contains(q, ignoreCase = true) }
                .toList()
                .sortedBy { (hero, _) -> hero.displayName }
                .toMap()
        }
        dispatch(Message.SetHeroSearchFiltered(filtered))
    }

    private suspend fun selectHero(heroId: Short) {
        dispatch(Message.SetLoading(true))
        guidesRepository.getHeroGuides(heroId)
            .onSuccess {
                dispatch(Message.SetGuides(it))
                lastFailedRetry = null
            }
            .onFailure { throwable ->
                Napier.e(throwable = throwable, message = "GuidesExecutor.selectHero($heroId) failed")
                lastFailedRetry = { selectHero(heroId) }
                dispatch(Message.SetError(true))
            }
        dispatch(Message.SetLoading(false))
    }

    private data class LoadResults(
        val guides: Result<List<Guide>>,
        val heroImages: Result<Map<Hero, String>>,
        val itemImages: Result<Map<Item, String>>,
    )
}
