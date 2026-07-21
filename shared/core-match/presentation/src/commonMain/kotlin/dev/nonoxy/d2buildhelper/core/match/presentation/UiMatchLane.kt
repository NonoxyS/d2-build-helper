package dev.nonoxy.d2buildhelper.core.match.presentation

import dev.icerock.moko.resources.StringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.match.domain.MatchLane

enum class UiMatchLane(val labelRes: StringResource) {
    ROAMING(labelRes = MR.strings.lane_roaming),
    SAFE_LANE(labelRes = MR.strings.lane_safe),
    MID_LANE(labelRes = MR.strings.lane_mid),
    OFF_LANE(labelRes = MR.strings.lane_off),
    JUNGLE(labelRes = MR.strings.lane_jungle),
}

fun MatchLane.toUi(): UiMatchLane = when (this) {
    MatchLane.ROAMING -> UiMatchLane.ROAMING
    MatchLane.SAFE_LANE -> UiMatchLane.SAFE_LANE
    MatchLane.MID_LANE -> UiMatchLane.MID_LANE
    MatchLane.OFF_LANE -> UiMatchLane.OFF_LANE
    MatchLane.JUNGLE -> UiMatchLane.JUNGLE
}
