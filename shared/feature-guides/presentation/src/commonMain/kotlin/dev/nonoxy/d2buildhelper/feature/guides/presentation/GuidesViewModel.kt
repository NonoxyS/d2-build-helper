package dev.nonoxy.d2buildhelper.feature.guides.presentation

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.presentation.viewmodel.BaseViewModel
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.FilterValue
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
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

    fun onFilterChipClick(kind: GuidesFilterKind) = store.accept(Intent.OnFilterChipClick(kind))
    fun onPickerDismiss() = store.accept(Intent.OnPickerDismiss)
    fun onPickerSearchChange(value: String) = store.accept(Intent.OnPickerSearchChange(value))
    fun onHeroSelected(heroId: HeroId) = store.accept(Intent.OnFilterApply(FilterValue.Hero(heroId)))
    fun onPositionSelected(position: MatchPlayerPosition) =
        store.accept(Intent.OnFilterApply(FilterValue.Position(position)))
    fun onSideSelected(isRadiant: Boolean) = store.accept(Intent.OnFilterApply(FilterValue.Side(isRadiant)))
    fun onFilterReset(kind: GuidesFilterKind) = store.accept(Intent.OnFilterReset(kind))
    fun onFiltersResetAll() = store.accept(Intent.OnFiltersResetAll)
    fun onRetry() = store.accept(Intent.OnRetry)

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
