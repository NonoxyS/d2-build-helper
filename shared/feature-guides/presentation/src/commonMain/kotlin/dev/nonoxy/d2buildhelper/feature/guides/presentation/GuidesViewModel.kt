package dev.nonoxy.d2buildhelper.feature.guides.presentation

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dev.nonoxy.d2buildhelper.core.presentation.viewmodel.BaseViewModel
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesLabelMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers.UiGuidesStateMapper
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesLabel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
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

    fun onHeroSearchValueChange(value: String) {
        store.accept(Intent.OnHeroSearchValueChange(value))
    }

    fun onHeroSearchDialogClick() {
        store.accept(Intent.OnHeroSearchDialogClick)
    }

    fun onHeroSelect(heroId: Short) {
        store.accept(Intent.OnHeroSelect(heroId))
    }

    fun onRetry() {
        store.accept(Intent.OnRetry)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
