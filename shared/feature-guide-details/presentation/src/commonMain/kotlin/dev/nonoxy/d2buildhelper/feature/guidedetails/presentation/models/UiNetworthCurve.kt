package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import kotlinx.collections.immutable.ImmutableList

/**
 * Card 5 — "Экономика". [points] = networth per minute (index = minute).
 * [purchaseMarkerMinutes] = minute indices to mark on the curve (●).
 */
data class UiNetworthCurve(
    val points: ImmutableList<Int>,
    val purchaseMarkerMinutes: ImmutableList<Int>,
    val gpm: Int?,
    val networth: Int?,
    val lastHitsAt10: Int?,
)
