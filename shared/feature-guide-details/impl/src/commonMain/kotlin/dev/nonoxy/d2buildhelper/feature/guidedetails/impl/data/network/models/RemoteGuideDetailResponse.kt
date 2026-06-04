package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteGuideDetailResponse(
    @SerialName("matchId") val matchId: Long,
    @SerialName("steamAccountId") val steamAccountId: Long,
    @SerialName("gameVersionId") val gameVersionId: Int,
    @SerialName("didRadiantWin") val didRadiantWin: Boolean? = null,
    @SerialName("durationSeconds") val durationSeconds: Int? = null,
    @SerialName("averageRank") val averageRank: Int? = null,
    @SerialName("player") val player: RemoteGuideDetailPlayerResponse,
    @SerialName("lineup") val lineup: List<RemoteLineupMemberResponse> = emptyList(),
)

@Serializable
internal data class RemoteGuideDetailPlayerResponse(
    @SerialName("heroId") val heroId: Int,
    @SerialName("isRadiant") val isRadiant: Boolean? = null,
    @SerialName("isVictory") val isVictory: Boolean? = null,
    @SerialName("position") val position: RemoteMatchPlayerPosition? = null,
    @SerialName("role") val role: String? = null,
    @SerialName("lane") val lane: String? = null,
    @SerialName("level") val level: Int? = null,
    @SerialName("kills") val kills: Int? = null,
    @SerialName("deaths") val deaths: Int? = null,
    @SerialName("assists") val assists: Int? = null,
    @SerialName("imp") val imp: Int? = null,
    @SerialName("goldPerMinute") val goldPerMinute: Int? = null,
    @SerialName("networth") val networth: Int? = null,
    @SerialName("finalItemIds") val finalItemIds: List<Int> = emptyList(),
    @SerialName("backpackItemIds") val backpackItemIds: List<Int> = emptyList(),
    @SerialName("neutralItemId") val neutralItemId: Int? = null,
    @SerialName("abilityLearnEvents") val abilityLearnEvents: List<RemoteAbilityLearnEventResponse> = emptyList(),
    @SerialName("itemPurchases") val itemPurchases: List<RemoteItemPurchaseResponse> = emptyList(),
    @SerialName("inventorySnapshots") val inventorySnapshots: List<RemoteInventorySnapshotResponse> = emptyList(),
    @SerialName("networthPerMinute") val networthPerMinute: List<Int> = emptyList(),
    @SerialName("lastHitsPerMinute") val lastHitsPerMinute: List<Int> = emptyList(),
    @SerialName("goldPerMinuteSeries") val goldPerMinuteSeries: List<Int> = emptyList(),
)

@Serializable
internal data class RemoteAbilityLearnEventResponse(
    @SerialName("time") val time: Int? = null,
    @SerialName("abilityId") val abilityId: Int? = null,
    @SerialName("level") val level: Int? = null,
    @SerialName("isTalent") val isTalent: Boolean? = null,
    @SerialName("isUltimate") val isUltimate: Boolean? = null,
)

@Serializable
internal data class RemoteInventorySnapshotResponse(
    @SerialName("itemIds") val itemIds: List<Int?> = emptyList(),
    @SerialName("backpackIds") val backpackIds: List<Int?> = emptyList(),
    @SerialName("neutralId") val neutralId: Int? = null,
)

@Serializable
internal data class RemoteLineupMemberResponse(
    @SerialName("steamAccountId") val steamAccountId: Long? = null,
    @SerialName("heroId") val heroId: Int? = null,
    @SerialName("isRadiant") val isRadiant: Boolean? = null,
    @SerialName("position") val position: RemoteMatchPlayerPosition? = null,
    @SerialName("role") val role: String? = null,
)
