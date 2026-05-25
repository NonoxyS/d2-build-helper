package dev.nonoxy.d2buildhelper.core.resources.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteAbilityConstant(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("iconUrl") val iconUrl: String,
)
