package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.icerock.moko.resources.ImageResource
import dev.nonoxy.d2buildhelper.common.resources.MR

enum class UiMatchPlayerPosition(val iconResource: ImageResource?) {
    POSITION_1(MR.images.position_1),
    POSITION_2(MR.images.position_2),
    POSITION_3(MR.images.position_3),
    POSITION_4(MR.images.position_4),
    POSITION_5(MR.images.position_5),
    UNKNOWN(null),
    FILTERED(null),
    ALL(null),
}
