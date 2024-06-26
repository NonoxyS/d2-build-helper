package dev.nonoxy.d2buildhelper.features.guides.presentation

import androidx.lifecycle.viewModelScope
import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.guides.domain.GetGuidesAndImagesUseCase
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.launch

class GuidesViewModel(
    private val getGuidesAndImagesUseCase: GetGuidesAndImagesUseCase = InjectProvider.getDependency(
        GetGuidesAndImagesUseCase::class
    )
) : BaseViewModel<GuidesViewState, GuidesAction, GuidesEvent>(initialState = GuidesViewState.Loading) {

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            getGuidesAndImagesUseCase.invoke().collect { newState ->
                viewState = newState
            }
        }
    }

    override fun obtainEvent(viewEvent: GuidesEvent) {
        TODO("Not yet implemented")
    }
}