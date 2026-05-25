package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import kotlinx.collections.immutable.ImmutableList

data class UiGuide(
    val matchId: Long,
    val steamAccountId: Long,
    val hero: UiHero,
    val position: UiMatchPlayerPosition?,
    val isRadiant: Boolean,
    val durationFormatted: String,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val impactLabel: String,
    val impactProgress: Float,
    val items: ImmutableList<UiItemPurchase>,
    val neutralItem: UiNeutralItem?,
)
