package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

data class UiNetworthCurve(
    val points: ImmutableList<Int>,
    val markers: ImmutableList<UiNetworthMarker>,
    val gpm: Int?,
    val networth: Int?,
    val lastHitsAt10: Int?,
)

data class UiNetworthMarker(
    val minute: Int,
    val iconUrl: ImageUrl?,
    val name: String?,
)
