package dev.nonoxy.d2buildhelper.core.data.api.resources.image.models

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero

class ImageResources(
    val heroImages: Map<Hero, String>,
    val itemImages: Map<Short, String>,
    val abilityImages: Map<Short, String>,
    val additionalImages: Map<String, String>,
)