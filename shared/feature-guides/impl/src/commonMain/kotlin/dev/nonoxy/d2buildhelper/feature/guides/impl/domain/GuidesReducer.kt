package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.Reducer
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory.Message

internal class GuidesReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State = when (msg) {
        is Message.SetLoading -> copy(isLoading = msg.isLoading)
        is Message.SetError -> copy(isError = msg.isError)
        is Message.SetGuides -> copy(guides = msg.guides)
        is Message.SetConstants -> copy(
            heroes = msg.heroes,
            items = msg.items,
            abilities = msg.abilities,
            gameVersion = msg.gameVersion,
        )
        is Message.SetFilters -> copy(filters = msg.filters)
        is Message.SetActivePicker -> copy(activePicker = msg.kind)
        is Message.SetPickerSearch -> copy(pickerSearch = msg.value)
    }
}
