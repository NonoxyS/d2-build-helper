package dev.nonoxy.d2buildhelper.core.domain

import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.Item

data class ImageResources(
    val heroImages: Map<Hero, String>,
    val itemImages: Map<Item, String>,
    val abilityImages: Map<Ability, String>,
)
