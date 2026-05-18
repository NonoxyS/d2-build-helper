package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Item
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

    init {
        fetchData()
    }

    private var searchJob: Job? = null

    private fun fetchData() {
        viewModelScope.launch {
            val results = coroutineScope {
                awaitAll(
                    async { guidesRepository.getGuides() },
                    async { resourcesRepository.getHeroImages() },
                    async { resourcesRepository.getItemImages() },
                    async { resourcesRepository.getAdditionalImages() },
                )
            }
            if (results.any { it.isFailure }) {
                viewState = GuidesViewState.Error
                return@launch
            }
            @Suppress("UNCHECKED_CAST")
            val guides = (results[0] as Result<List<Guide>>).getOrThrow()

            @Suppress("UNCHECKED_CAST")
            val heroImages = (results[1] as Result<Map<Hero, String>>).getOrThrow()

            @Suppress("UNCHECKED_CAST")
            val itemImages = (results[2] as Result<Map<Item, String>>).getOrThrow()

            @Suppress("UNCHECKED_CAST")
            val additional = (results[3] as Result<Map<String, String>>).getOrThrow()

            val imageResources = ImageResources(
                heroImages = heroImages,
                itemImages = itemImages.mapKeys { it.key.id },
                abilityImages = emptyMap(),
                additionalImages = additional,
            )
            val sortedHeroFiltered = withContext(Dispatchers.Default) {
                heroImages.toList().sortedBy { (hero, _) -> hero.displayName }.toMap()
            }
            viewState = GuidesViewState.Display(
                guides = guides,
                imageResources = imageResources,
                heroSearchFiltered = sortedHeroFiltered,
            )
        }
    }

    private fun fetchHeroGuides(heroId: Short) {
        viewModelScope.launch {
            val current = viewState as? GuidesViewState.Display ?: return@launch
            viewState = GuidesViewState.Loading
            guidesRepository.getHeroGuides(heroId)
                .onSuccess { heroGuides -> viewState = current.copy(guides = heroGuides) }
                .onFailure { viewState = GuidesViewState.Error }
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
                val query = current.heroSearchValue.trim()
                current.imageResources.heroImages
                    .filter { (hero, _) -> hero.displayName.contains(query, ignoreCase = true) }
                    .toList()
                    .sortedBy { (hero, _) -> hero.displayName }
                    .toMap()
            }
            viewState = current.copy(heroSearchFiltered = filtered)
        }
    }
}
