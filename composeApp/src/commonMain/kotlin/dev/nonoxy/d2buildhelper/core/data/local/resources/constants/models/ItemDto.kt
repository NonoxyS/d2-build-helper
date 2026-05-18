package dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models

import kotlinx.serialization.Serializable

@Serializable
internal class ItemDto(
    val id: Short,
    val shortName: String = "",
    val displayName: String = ""
)