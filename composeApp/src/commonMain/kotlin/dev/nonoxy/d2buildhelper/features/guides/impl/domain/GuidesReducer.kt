package dev.nonoxy.d2buildhelper.features.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.Reducer
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.impl.domain.GuidesStoreFactory.Message

internal class GuidesReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State = when (msg) {
        is Message.SetLoading -> copy(isLoading = msg.isLoading)
        is Message.SetError -> copy(isError = msg.isError)
        is Message.SetGuides -> copy(guides = msg.guides)
        is Message.SetImageResources -> copy(imageResources = msg.resources)
        is Message.SetHeroSearchValue -> copy(heroSearchValue = msg.value)
        is Message.SetHeroSearchFiltered -> copy(heroSearchFiltered = msg.filtered)
    }
}
