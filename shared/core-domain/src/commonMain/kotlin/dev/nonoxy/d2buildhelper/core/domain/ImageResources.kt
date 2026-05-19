package dev.nonoxy.d2buildhelper.core.domain

data class ImageResources(
    val heroImages: Map<Hero, String>,
    val itemImages: Map<Item, String>,
    val abilityImages: Map<Ability, String>,
    val additionalImages: Map<String, String>,
)
