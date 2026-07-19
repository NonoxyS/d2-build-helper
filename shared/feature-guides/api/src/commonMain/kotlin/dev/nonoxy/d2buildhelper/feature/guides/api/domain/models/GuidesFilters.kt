package dev.nonoxy.d2buildhelper.feature.guides.api.domain.models

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition

sealed interface GuidesFilterKind {
    data object Hero : GuidesFilterKind
    data object Position : GuidesFilterKind
    data object Side : GuidesFilterKind
}

data class GuidesFilters(
    val heroId: HeroId? = null,
    val position: MatchPlayerPosition? = null,
    val isRadiant: Boolean? = null,
)

sealed interface FilterValue {
    data class Hero(val heroId: HeroId) : FilterValue
    data class Position(val position: MatchPlayerPosition) : FilterValue
    data class Side(val isRadiant: Boolean) : FilterValue
}
