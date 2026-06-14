package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchLane
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerRole
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl

data class UiBuildHeader(
    val heroIconUrl: ImageUrl?,
    val heroName: String,
    val isRadiant: Boolean?,
    val position: UiMatchPlayerPosition?,
    val role: UiMatchPlayerRole?,
    val lane: UiMatchLane?,
    val durationText: String,
    val level: Int?,
    val isVictory: Boolean?,
    val kills: Int?,
    val deaths: Int?,
    val assists: Int?,
    val impact: Int?,
)
