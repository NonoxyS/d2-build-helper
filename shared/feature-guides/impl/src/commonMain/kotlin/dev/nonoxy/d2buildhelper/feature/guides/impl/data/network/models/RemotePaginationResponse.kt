package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemotePaginationResponse(
    @SerialName("page") val page: Int,
    @SerialName("pageSize") val pageSize: Int,
    @SerialName("hasMore") val hasMore: Boolean,
)
