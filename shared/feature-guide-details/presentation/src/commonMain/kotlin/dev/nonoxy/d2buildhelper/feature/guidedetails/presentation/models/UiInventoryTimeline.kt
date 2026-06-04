package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 4 — "Инвентарь по минутам". [snapshots] index = minute of match.
 */
data class UiInventoryTimeline(
    val snapshots: ImmutableList<UiInventorySnapshot>,
)

data class UiInventorySnapshot(
    /** 6 main inventory slots; null = empty slot. */
    val itemIconUrls: ImmutableList<ImageUrl?>,
    /** 3 backpack slots; null = empty slot. */
    val backpackIconUrls: ImmutableList<ImageUrl?>,
    val neutralIconUrl: ImageUrl?,
)
