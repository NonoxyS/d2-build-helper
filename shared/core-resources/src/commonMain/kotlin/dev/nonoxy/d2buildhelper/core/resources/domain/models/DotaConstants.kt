package dev.nonoxy.d2buildhelper.core.resources.domain.models

import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId

data class DotaConstants(
    val gameVersion: GameVersion,
    val heroes: Map<HeroId, Hero>,
    val items: Map<ItemId, Item>,
    val abilities: Map<AbilityId, Ability>,
)
