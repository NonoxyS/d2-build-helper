package dev.nonoxy.d2buildhelper.core.match.presentation

import dev.icerock.moko.resources.ImageResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition

enum class UiMatchPlayerPosition(
    val iconResource: ImageResource,
    val shortNumber: Int,
) {
    POSITION_1(iconResource = MR.images.pos_1, shortNumber = 1),
    POSITION_2(iconResource = MR.images.pos_2, shortNumber = 2),
    POSITION_3(iconResource = MR.images.pos_3, shortNumber = 3),
    POSITION_4(iconResource = MR.images.pos_4, shortNumber = 4),
    POSITION_5(iconResource = MR.images.pos_5, shortNumber = 5),
}

fun MatchPlayerPosition.toUi(): UiMatchPlayerPosition = when (this) {
    MatchPlayerPosition.POSITION_1 -> UiMatchPlayerPosition.POSITION_1
    MatchPlayerPosition.POSITION_2 -> UiMatchPlayerPosition.POSITION_2
    MatchPlayerPosition.POSITION_3 -> UiMatchPlayerPosition.POSITION_3
    MatchPlayerPosition.POSITION_4 -> UiMatchPlayerPosition.POSITION_4
    MatchPlayerPosition.POSITION_5 -> UiMatchPlayerPosition.POSITION_5
}

fun UiMatchPlayerPosition.toDomain(): MatchPlayerPosition = when (this) {
    UiMatchPlayerPosition.POSITION_1 -> MatchPlayerPosition.POSITION_1
    UiMatchPlayerPosition.POSITION_2 -> MatchPlayerPosition.POSITION_2
    UiMatchPlayerPosition.POSITION_3 -> MatchPlayerPosition.POSITION_3
    UiMatchPlayerPosition.POSITION_4 -> MatchPlayerPosition.POSITION_4
    UiMatchPlayerPosition.POSITION_5 -> MatchPlayerPosition.POSITION_5
}
