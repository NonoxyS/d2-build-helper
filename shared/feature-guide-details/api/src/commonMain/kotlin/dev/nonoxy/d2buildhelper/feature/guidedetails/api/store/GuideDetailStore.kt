package dev.nonoxy.d2buildhelper.feature.guidedetails.api.store

import com.arkivanov.mvikotlin.core.store.Store
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Intent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Label
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.State

interface GuideDetailStore : Store<Intent, State, Label> {

    data class State(
        val detail: GuideDetail? = null,
        val heroes: Map<HeroId, Hero> = emptyMap(),
        val items: Map<ItemId, Item> = emptyMap(),
        val abilities: Map<AbilityId, Ability> = emptyMap(),
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    sealed interface Intent {
        data object OnRetry : Intent
    }

    sealed interface Label
}
