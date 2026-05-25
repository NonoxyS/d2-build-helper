package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteGuideResponse(
    @SerialName("matchId") val matchId: Long,
    @SerialName("steamAccountId") val steamAccountId: Long,
    @SerialName("durationSeconds") val durationSeconds: Int? = null,
    @SerialName("heroId") val heroId: Int,
    @SerialName("player") val player: RemoteGuidePlayerResponse,
)
