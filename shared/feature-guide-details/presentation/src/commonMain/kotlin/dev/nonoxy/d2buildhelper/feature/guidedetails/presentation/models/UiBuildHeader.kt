package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.match.presentation.UiMatchLane
import dev.nonoxy.d2buildhelper.core.match.presentation.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.core.match.presentation.UiMatchPlayerRole
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
)
