package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources

sealed class GuidesViewState {
    data object Loading : GuidesViewState()
    data object Error : GuidesViewState()
    data class Display(
        val heroSearchValue: String = "",
        val heroSearchFiltered: Map<Hero, String>,
        val guides: List<Guide>,
        val imageResources: ImageResources
    ) : GuidesViewState()
}