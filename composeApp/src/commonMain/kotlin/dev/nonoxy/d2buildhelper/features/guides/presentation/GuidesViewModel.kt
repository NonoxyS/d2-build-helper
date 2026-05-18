package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
            val (guidesResult, heroResult, itemResult, additionalResult) = coroutineScope {
                val guidesDef = async { guidesRepository.getGuides() }
                val heroesDef = async { resourcesRepository.getHeroImages() }
                val itemsDef = async { resourcesRepository.getItemImages() }
                val additionalDef = async { resourcesRepository.getAdditionalImages() }
                FetchResults(
                    guides = guidesDef.await(),
                    heroImages = heroesDef.await(),
                    itemImages = itemsDef.await(),
                    additionalImages = additionalDef.await(),
                )
            }

            val firstFailure = listOf(guidesResult, heroResult, itemResult, additionalResult)
                .firstOrNull { it.isFailure }
            if (firstFailure != null) {
                Napier.e(throwable = firstFailure.exceptionOrNull(), message = "GuidesViewModel.fetchData failed")
                viewState = GuidesViewState.Error
                return@launch
            }

            val guides = guidesResult.getOrThrow()
            val heroImages = heroResult.getOrThrow()
            val itemImages = itemResult.getOrThrow()
            val additional = additionalResult.getOrThrow()

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

private data class FetchResults(
    val guides: Result<List<dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide>>,
    val heroImages: Result<Map<dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero, String>>,
    val itemImages: Result<Map<dev.nonoxy.d2buildhelper.features.guides.domain.models.Item, String>>,
    val additionalImages: Result<Map<String, String>>,
)
