package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class UiGuidesState(
    val guides: ImmutableList<UiGuide> = persistentListOf(),
    val heroFilter: UiHeroFilter? = null,
    val selectedPosition: UiMatchPlayerPosition? = null,
    val selectedSide: Boolean? = null,
    val heroPicker: UiFilterPicker.Hero? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLoadMoreError: Boolean = false,
    val canLoadMore: Boolean = false,
)
