package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteGuidePlayerResponse(
    @SerialName("position") val position: RemoteMatchPlayerPosition? = null,
    @SerialName("isRadiant") val isRadiant: Boolean? = null,
    @SerialName("kills") val kills: Int? = null,
    @SerialName("deaths") val deaths: Int? = null,
    @SerialName("assists") val assists: Int? = null,
    @SerialName("impact") val impact: Int? = null,
    @SerialName("finalItemIds") val finalItemIds: List<Int> = emptyList(),
    @SerialName("backpackItemIds") val backpackItemIds: List<Int> = emptyList(),
    @SerialName("neutralItemId") val neutralItemId: Int? = null,
    @SerialName("itemPurchases") val itemPurchases: List<RemoteItemPurchaseResponse> = emptyList(),
)
