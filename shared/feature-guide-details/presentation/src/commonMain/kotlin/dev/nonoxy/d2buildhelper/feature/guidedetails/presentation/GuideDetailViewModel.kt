package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dev.nonoxy.d2buildhelper.core.presentation.viewmodel.BaseViewModel
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Intent
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailLabelMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers.UiGuideDetailStateMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailLabel
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailState
import kotlinx.coroutines.flow.mapNotNull

class GuideDetailViewModel internal constructor(
    private val store: GuideDetailStore,
    stateMapper: UiGuideDetailStateMapper,
    labelMapper: UiGuideDetailLabelMapper,
) : BaseViewModel<UiGuideDetailState, UiGuideDetailLabel>(initialState = UiGuideDetailState()) {

    init {
        bindAndStart {
            store.states.mapNotNull(stateMapper::map) bindTo ::acceptState
            store.labels.mapNotNull(labelMapper::map) bindTo ::acceptLabel
        }
    }

    fun onRetry() = store.accept(Intent.OnRetry)

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
