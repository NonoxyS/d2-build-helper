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
            heroes = msg.constants.heroes,
            items = msg.constants.items,
            abilities = msg.constants.abilities,
            gameVersion = msg.constants.gameVersion,
        )
        is Message.SetFilters -> copy(filters = msg.filters)
        is Message.SetActivePicker -> copy(activePicker = msg.kind)
        is Message.SetPickerSearch -> copy(pickerSearch = msg.value)
        is Message.SetPagination -> copy(pagination = msg.pagination)
        is Message.AppendGuides -> copy(guides = guides + msg.guides)
        is Message.SetLoadingMore -> copy(isLoadingMore = msg.isLoadingMore)
        is Message.SetRefreshing -> copy(isRefreshing = msg.isRefreshing)
        is Message.SetLoadMoreError -> copy(isLoadMoreError = msg.isLoadMoreError)
    }
}
