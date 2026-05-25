package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesFilterKind

data class UiFilterChip(
    val kind: GuidesFilterKind,
    val label: String,
    val isApplied: Boolean,
)
