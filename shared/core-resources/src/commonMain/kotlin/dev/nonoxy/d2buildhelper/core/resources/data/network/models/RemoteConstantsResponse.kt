package dev.nonoxy.d2buildhelper.core.resources.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteConstantsResponse(
    @SerialName("gameVersionId") val gameVersionId: Int,
    @SerialName("patch") val patch: String,
    @SerialName("heroes") val heroes: List<RemoteHeroConstantResponse>,
    @SerialName("items") val items: List<RemoteItemConstantResponse>,
    @SerialName("abilities") val abilities: List<RemoteAbilityConstantResponse>,
)
