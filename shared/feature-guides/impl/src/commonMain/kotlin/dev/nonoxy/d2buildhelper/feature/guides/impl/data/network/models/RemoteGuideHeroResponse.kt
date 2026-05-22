package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteGuideHeroResponse(
    @SerialName("id") val id: Int,
    @SerialName("shortName") val shortName: String? = null,
    @SerialName("displayName") val displayName: String? = null,
)
