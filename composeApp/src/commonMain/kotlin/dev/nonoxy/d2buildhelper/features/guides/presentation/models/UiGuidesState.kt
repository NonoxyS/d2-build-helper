package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.ImageResources

data class UiGuidesState(
    val guides: List<Guide> = emptyList(),
    val imageResources: ImageResources? = null,
    val heroSearchValue: String = "",
    val heroSearchFiltered: Map<Hero, String> = emptyMap(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)
