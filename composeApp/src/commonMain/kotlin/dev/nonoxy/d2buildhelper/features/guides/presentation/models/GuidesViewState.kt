package dev.nonoxy.d2buildhelper.features.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources

sealed class GuidesViewState {
    data object Loading : GuidesViewState()
    data object Error : GuidesViewState()
    data class Display(
        val guides: List<Guide>,
        val imageResources: ImageResources
    ) : GuidesViewState()
}