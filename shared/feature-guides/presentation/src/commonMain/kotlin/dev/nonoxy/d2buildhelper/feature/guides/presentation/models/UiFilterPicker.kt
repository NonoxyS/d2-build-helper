package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import kotlinx.collections.immutable.ImmutableList

sealed interface UiFilterPicker {
    data class Hero(
        val search: String,
        val heroes: ImmutableList<UiHero>,
        val selectedHeroId: HeroId?,
    ) : UiFilterPicker

    data class Position(
        val options: ImmutableList<UiMatchPlayerPosition>,
        val selected: UiMatchPlayerPosition?,
    ) : UiFilterPicker

    data class Side(val selected: Boolean?) : UiFilterPicker
}
