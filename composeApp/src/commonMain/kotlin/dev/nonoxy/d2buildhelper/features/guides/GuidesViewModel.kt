package dev.nonoxy.d2buildhelper.features.guides

import dev.nonoxy.d2buildhelper.base.BaseViewModel
import dev.nonoxy.d2buildhelper.features.guides.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.models.GuidesViewState

class GuidesViewModel : BaseViewModel<GuidesViewState, GuidesAction, GuidesEvent>(initialState = GuidesViewState.Loading) {
    override fun obtainEvent(viewEvent: GuidesEvent) {
        TODO("Not yet implemented")
    }
}