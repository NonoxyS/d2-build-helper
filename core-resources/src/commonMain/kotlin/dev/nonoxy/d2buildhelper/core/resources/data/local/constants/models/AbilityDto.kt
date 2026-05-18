package dev.nonoxy.d2buildhelper.core.resources.data.local.constants.models

import kotlinx.serialization.Serializable

@Serializable
internal class AbilityDto(
    val id: Short,
    val name: String = ""
)