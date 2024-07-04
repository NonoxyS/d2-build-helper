package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.guides.domain.GetGuidesAndImagesUseCase
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuidesViewModel(
    private val getGuidesAndImagesUseCase: GetGuidesAndImagesUseCase = InjectProvider.getDependency(
        GetGuidesAndImagesUseCase::class
    )
) : BaseViewModel<GuidesViewState, GuidesAction, GuidesEvent>(initialState = GuidesViewState.Loading) {

    init {
        fetchData()
    }

    private var searchJob: Job? = null

    private fun fetchData() {
        viewModelScope.launch {
            getGuidesAndImagesUseCase.invoke().collect { newState ->
                viewState = newState
            }
        }
    }

    override fun obtainEvent(viewEvent: GuidesEvent) {
        when (viewEvent) {
            is GuidesEvent.HeroSearchValueChanged -> updateHeroSearchValue(viewEvent.newValue)
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