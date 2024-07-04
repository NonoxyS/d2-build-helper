package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.domain.models.GuideUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI

sealed class GuidesViewState {
    data object Loading : GuidesViewState()
    data object Error : GuidesViewState()
    data class Display(
        val heroSearchValue: String = "",
        val heroSearchFiltered: Map<HeroUI, String>,
        val guides: List<GuideUI>,
        val imageResources: ImageResources
    ) : GuidesViewState()
}