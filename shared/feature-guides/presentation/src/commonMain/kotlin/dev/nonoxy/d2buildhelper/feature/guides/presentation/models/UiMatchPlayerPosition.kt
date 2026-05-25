package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.icerock.moko.resources.ImageResource
import dev.nonoxy.d2buildhelper.common.resources.MR

enum class UiMatchPlayerPosition(
    val iconResource: ImageResource,
    val shortNumber: Int,
) {
    POSITION_1(iconResource = MR.images.position_1, shortNumber = 1),
    POSITION_2(iconResource = MR.images.position_2, shortNumber = 2),
    POSITION_3(iconResource = MR.images.position_3, shortNumber = 3),
    POSITION_4(iconResource = MR.images.position_4, shortNumber = 4),
    POSITION_5(iconResource = MR.images.position_5, shortNumber = 5),
}
