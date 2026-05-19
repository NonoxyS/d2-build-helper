package dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models

import kotlinx.serialization.Serializable

@Serializable
internal class HeroDto(
    val id: Short,
    val shortName: String = "",
    val displayName: String = ""
)