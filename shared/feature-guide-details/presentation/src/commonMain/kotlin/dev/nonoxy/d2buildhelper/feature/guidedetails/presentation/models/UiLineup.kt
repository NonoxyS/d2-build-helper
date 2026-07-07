package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerRole
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

data class UiLineup(
    val enemies: ImmutableList<UiLineupMember>,
    val allies: ImmutableList<UiLineupMember>,
)

data class UiLineupMember(
    val heroIconUrl: ImageUrl?,
    val heroName: String?,
    val isMe: Boolean,
    val role: UiMatchPlayerRole?,
)
