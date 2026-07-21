package dev.nonoxy.d2buildhelper.core.match.presentation

import dev.icerock.moko.resources.StringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerRole

enum class UiMatchPlayerRole(val labelRes: StringResource) {
    CORE(labelRes = MR.strings.role_core),
    LIGHT_SUPPORT(labelRes = MR.strings.role_light_support),
    HARD_SUPPORT(labelRes = MR.strings.role_hard_support),
}

fun MatchPlayerRole.toUi(): UiMatchPlayerRole = when (this) {
    MatchPlayerRole.CORE -> UiMatchPlayerRole.CORE
    MatchPlayerRole.LIGHT_SUPPORT -> UiMatchPlayerRole.LIGHT_SUPPORT
    MatchPlayerRole.HARD_SUPPORT -> UiMatchPlayerRole.HARD_SUPPORT
}
