package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain

import com.arkivanov.mvikotlin.core.store.Reducer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.State
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.GuideDetailStoreFactory.Message

internal class GuideDetailReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State = when (msg) {
        is Message.SetLoading -> copy(isLoading = msg.isLoading)
        is Message.SetError -> copy(isError = msg.isError)
        is Message.SetDetail -> copy(detail = msg.detail)
        is Message.SetConstants -> copy(
            heroes = msg.constants.heroes,
            items = msg.constants.items,
            abilities = msg.constants.abilities,
        )
    }
}
