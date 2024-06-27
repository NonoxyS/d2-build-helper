package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.domain.models.GuideUI

sealed class GuidesViewState {
    data object Loading : GuidesViewState()
    data object Error : GuidesViewState()
    data class Display(
        val guides: List<GuideUI>,
        val imageResources: ImageResources
    ) : GuidesViewState()
}