package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteGuidesPageResponse(
    @SerialName("pagination") val pagination: RemotePaginationResponse,
    @SerialName("gameVersionId") val gameVersionId: Int,
    @SerialName("guides") val guides: List<RemoteGuideResponse>,
)
