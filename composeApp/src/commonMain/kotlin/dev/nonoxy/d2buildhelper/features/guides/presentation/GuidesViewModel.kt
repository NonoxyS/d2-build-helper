package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import dev.nonoxy.d2buildhelper.features.guides.domain.usecases.GetGuidesUseCase
import dev.nonoxy.d2buildhelper.features.guides.domain.usecases.GetImagesUseCase
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class GuidesViewModel(
    private val getGuidesUseCase: GetGuidesUseCase = GetGuidesUseCase(),
    private val getImagesUseCase: GetImagesUseCase = GetImagesUseCase(),
) : BaseViewModel<GuidesViewState, GuidesAction, GuidesEvent>(initialState = GuidesViewState.Loading) {

    init {
        fetchData()
    }

    private var searchJob: Job? = null

    private fun fetchData() {
        viewModelScope.launch {
            combine(
                getGuidesUseCase.getGuides(),
                getImagesUseCase.invoke(isNeedAbilityImages = false)
            ) { guidesResult, imagesResult ->
                when {
                    guidesResult is RequestResult.Error || imagesResult is RequestResult.Error -> {
                        GuidesViewState.Error
                    }

                    guidesResult is RequestResult.Success && imagesResult is RequestResult.Success -> {
                        val heroSearchFiltered = withContext(Dispatchers.Default) {
                            imagesResult.data.heroImages.toList()
                                .sortedBy { (hero, _) ->
                                    hero.displayName
                                }.toMap()
                        }
                        GuidesViewState.Display(
                            guides = guidesResult.data,
                            imageResources = imagesResult.data,
                            heroSearchFiltered = heroSearchFiltered
                        )
                    }

                    else -> GuidesViewState.Loading
                }
            }.collect { newState ->
                viewState = newState
            }
        }
    }

    private fun fetchHeroGuides(heroId: Short) {
        viewModelScope.launch {
            val currentState = (viewState as GuidesViewState.Display)
            getGuidesUseCase.getHeroGuides(heroId).collect { heroGuidesResult ->
                when (heroGuidesResult) {
                    is RequestResult.Error -> viewState = GuidesViewState.Error
                    is RequestResult.InProgress -> viewState = GuidesViewState.Loading
                    is RequestResult.Success -> {
                        viewState = currentState.copy(guides = heroGuidesResult.data)
                    }
                }
            }
        }
    }

    override fun obtainEvent(viewEvent: GuidesEvent) {
        when (viewEvent) {
            is GuidesEvent.HeroSearchValueChanged -> updateHeroSearchValue(viewEvent.newValue)
            is GuidesEvent.SelectHeroInSearchDialog -> fetchHeroGuides(viewEvent.heroId)
            GuidesEvent.HeroSearchDialogClicked -> heroSearchClicked()
        }
    }

    private fun updateHeroSearchValue(newValue: String) {
        viewState = (viewState as GuidesViewState.Display).copy(heroSearchValue = newValue)
        debounceSearch()
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(100L)
            viewState = (viewState as GuidesViewState.Display).copy(
                heroSearchFiltered = filterHeroSearch(viewState as GuidesViewState.Display)
            )
        }
    }

    private suspend fun filterHeroSearch(state: GuidesViewState.Display): Map<HeroUI, String> {
        val filteredHeroes = viewModelScope.async(Dispatchers.Default) {
            state.imageResources.heroImages.filter { (hero, _) ->
                hero.displayName.contains(state.heroSearchValue.trim(), ignoreCase = true)
            }.toList().sortedBy { (hero, _) -> hero.displayName }.toMap()
        }
        return filteredHeroes.await()
    }

    private fun heroSearchClicked() {
        viewAction = GuidesAction.ShowHeroSearchDialog
    }
}