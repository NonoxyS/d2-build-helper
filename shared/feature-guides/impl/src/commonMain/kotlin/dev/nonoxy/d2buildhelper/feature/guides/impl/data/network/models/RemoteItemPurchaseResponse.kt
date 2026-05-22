package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteItemPurchaseResponse(
    @SerialName("itemId") val itemId: Int,
    @SerialName("time") val time: Int,
)
