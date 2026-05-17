package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero

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