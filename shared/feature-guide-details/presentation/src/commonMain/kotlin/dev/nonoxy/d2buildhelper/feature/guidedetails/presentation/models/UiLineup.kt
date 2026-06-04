package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 6 — "Состав". [enemies] (top) + [allies] (bottom), each sorted by position 1..5.
 */
data class UiLineup(
    val enemies: ImmutableList<UiLineupMember>,
    val allies: ImmutableList<UiLineupMember>,
)

data class UiLineupMember(
    val heroIconUrl: ImageUrl?,
    val isMe: Boolean,
    val role: String?,
)
