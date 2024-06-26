package dev.nonoxy.d2buildhelper.core.data.api.resources.image.models

class ImageResources(
    val heroImages: Map<Short, String>,
    val itemImages: Map<Short, String>,
    val abilityImages: Map<Short, String>,
    val additionalImages: Map<String, String>,
)