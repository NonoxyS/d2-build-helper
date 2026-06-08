package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 5 — "Экономика". [points] = networth per minute (index = minute).
 * [markers] = significant item purchase moments to mark on the curve (●).
 * "Significant" = item ended up in the final build (finalItemIds ∪ backpack ∪ neutral).
 */
data class UiNetworthCurve(
    val points: ImmutableList<Int>,
    val markers: ImmutableList<UiNetworthMarker>,
    val gpm: Int?,
    val networth: Int?,
    val lastHitsAt10: Int?,
)

/**
 * One marker dot on the networth curve.
 * [minute] = time / 60 (x-axis position).
 * [iconUrl] and [name] come from the items constants map (may be null if item unknown).
 */
data class UiNetworthMarker(
    val minute: Int,
    val iconUrl: ImageUrl?,
    val name: String?,
)
