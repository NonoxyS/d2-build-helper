package dev.nonoxy.d2buildhelper.core.resources.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteItemConstant(
    @SerialName("id") val id: Int,
    @SerialName("shortName") val shortName: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("quality") val quality: String? = null,
    @SerialName("isRecipe") val isRecipe: Boolean = false,
    @SerialName("components") val components: List<Int> = emptyList(),
)
