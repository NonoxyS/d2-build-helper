package dev.nonoxy.d2buildhelper.common.ui.match

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerRole

enum class UiMatchPlayerRole {
    CORE,
    LIGHT_SUPPORT,
    HARD_SUPPORT,
}

fun MatchPlayerRole.toUi(): UiMatchPlayerRole = when (this) {
    MatchPlayerRole.CORE -> UiMatchPlayerRole.CORE
    MatchPlayerRole.LIGHT_SUPPORT -> UiMatchPlayerRole.LIGHT_SUPPORT
    MatchPlayerRole.HARD_SUPPORT -> UiMatchPlayerRole.HARD_SUPPORT
}

@Composable
fun UiMatchPlayerRole.label(): String = stringResource(
    when (this) {
        UiMatchPlayerRole.CORE -> MR.strings.role_core
        UiMatchPlayerRole.LIGHT_SUPPORT -> MR.strings.role_light_support
        UiMatchPlayerRole.HARD_SUPPORT -> MR.strings.role_hard_support
    },
)
