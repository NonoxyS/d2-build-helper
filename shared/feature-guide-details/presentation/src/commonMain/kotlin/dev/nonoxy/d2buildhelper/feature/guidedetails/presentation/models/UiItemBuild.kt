package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 3 — "Билд предметов".
 *
 * v1 SIMPLIFIED: a single chronologically-sorted [purchases] row plus the final
 * [neutralItem]. The significant / consumable / neutral split (prototype categories)
 * is deferred to B16 — `core.domain.models.Item` has no `isConsumable` flag yet.
 */
data class UiItemBuild(
    val neutralItem: UiItemBuildEntry?,
    val purchases: ImmutableList<UiItemBuildEntry>,
)

data class UiItemBuildEntry(
    val iconUrl: ImageUrl?,
    val timeText: String,
)
