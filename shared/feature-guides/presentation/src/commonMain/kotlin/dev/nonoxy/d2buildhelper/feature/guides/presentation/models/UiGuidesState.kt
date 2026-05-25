package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class UiGuidesState(
    val guides: ImmutableList<UiGuide> = persistentListOf(),
    val filterChips: ImmutableList<UiFilterChip> = persistentListOf(),
    val activePicker: UiFilterPicker? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)
