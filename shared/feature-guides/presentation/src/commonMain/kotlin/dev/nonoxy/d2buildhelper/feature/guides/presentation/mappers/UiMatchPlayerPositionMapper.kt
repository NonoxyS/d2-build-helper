package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiMatchPlayerPosition

interface UiMatchPlayerPositionMapper : Mapper<MatchPlayerPosition, UiMatchPlayerPosition>

class UiMatchPlayerPositionMapperImpl : UiMatchPlayerPositionMapper {
    override fun map(item: MatchPlayerPosition): UiMatchPlayerPosition = when (item) {
        MatchPlayerPosition.POSITION_1 -> UiMatchPlayerPosition.POSITION_1
        MatchPlayerPosition.POSITION_2 -> UiMatchPlayerPosition.POSITION_2
        MatchPlayerPosition.POSITION_3 -> UiMatchPlayerPosition.POSITION_3
        MatchPlayerPosition.POSITION_4 -> UiMatchPlayerPosition.POSITION_4
        MatchPlayerPosition.POSITION_5 -> UiMatchPlayerPosition.POSITION_5
        MatchPlayerPosition.UNKNOWN -> UiMatchPlayerPosition.UNKNOWN
        MatchPlayerPosition.FILTERED -> UiMatchPlayerPosition.FILTERED
        MatchPlayerPosition.ALL -> UiMatchPlayerPosition.ALL
    }
}
