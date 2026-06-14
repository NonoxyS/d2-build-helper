package dev.nonoxy.d2buildhelper.common.ui.match

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.match.MatchLane

enum class UiMatchLane {
    ROAMING,
    SAFE_LANE,
    MID_LANE,
    OFF_LANE,
    JUNGLE,
}

fun MatchLane.toUi(): UiMatchLane = when (this) {
    MatchLane.ROAMING -> UiMatchLane.ROAMING
    MatchLane.SAFE_LANE -> UiMatchLane.SAFE_LANE
    MatchLane.MID_LANE -> UiMatchLane.MID_LANE
    MatchLane.OFF_LANE -> UiMatchLane.OFF_LANE
    MatchLane.JUNGLE -> UiMatchLane.JUNGLE
}

@Composable
fun UiMatchLane.label(): String = stringResource(
    when (this) {
        UiMatchLane.ROAMING -> MR.strings.lane_roaming
        UiMatchLane.SAFE_LANE -> MR.strings.lane_safe
        UiMatchLane.MID_LANE -> MR.strings.lane_mid
        UiMatchLane.OFF_LANE -> MR.strings.lane_off
        UiMatchLane.JUNGLE -> MR.strings.lane_jungle
    },
)
