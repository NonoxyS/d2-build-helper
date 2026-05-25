package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind

sealed interface UiFilterChip {
    val kind: GuidesFilterKind
    val isApplied: Boolean

    data class Hero(val appliedHeroName: String?) : UiFilterChip {
        override val kind: GuidesFilterKind get() = GuidesFilterKind.Hero
        override val isApplied: Boolean get() = appliedHeroName != null
    }

    data class Position(val appliedPosition: UiMatchPlayerPosition?) : UiFilterChip {
        override val kind: GuidesFilterKind get() = GuidesFilterKind.Position
        override val isApplied: Boolean get() = appliedPosition != null
    }

    data class Side(val appliedIsRadiant: Boolean?) : UiFilterChip {
        override val kind: GuidesFilterKind get() = GuidesFilterKind.Side
        override val isApplied: Boolean get() = appliedIsRadiant != null
    }
}
