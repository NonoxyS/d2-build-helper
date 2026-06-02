package dev.nonoxy.d2buildhelper.feature.guides.api.store

import com.arkivanov.mvikotlin.core.store.Store
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.FilterValue
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Pagination
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State

interface GuidesStore : Store<Intent, State, Label> {

    data class State(
        val guides: List<Guide> = emptyList(),
        val heroes: Map<HeroId, Hero> = emptyMap(),
        val items: Map<ItemId, Item> = emptyMap(),
        val abilities: Map<AbilityId, Ability> = emptyMap(),
        val gameVersion: GameVersion? = null,
        val filters: GuidesFilters = GuidesFilters(),
        val activePicker: GuidesFilterKind? = null,
        val pickerSearch: String = "",
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val pagination: Pagination = Pagination(page = 0, hasMore = false),
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false,
        val isLoadMoreError: Boolean = false,
    )

    sealed interface Intent {
        data class OnFilterChipClick(val kind: GuidesFilterKind) : Intent
        data object OnPickerDismiss : Intent
        data class OnPickerSearchChange(val value: String) : Intent
        data class OnFilterApply(val value: FilterValue) : Intent
        data class OnFilterReset(val kind: GuidesFilterKind) : Intent
        data object OnFiltersResetAll : Intent
        data object OnRetry : Intent
        data object OnLoadMore : Intent
        data object OnRefresh : Intent
    }

    sealed interface Label
}
