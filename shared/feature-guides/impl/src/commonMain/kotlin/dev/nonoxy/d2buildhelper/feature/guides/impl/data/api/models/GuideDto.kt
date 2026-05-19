package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models

internal class GuideDto(
    val hero: HeroDto,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: PlayerStatsDto
)

internal class HeroDto(
    val heroId: Short,
    val shortName: String,
    val displayName: String
)

internal class PlayerStatsDto(
    val position: MatchPlayerPositionType?,
    val isRadiant: Boolean?,
    val kills: Byte,
    val deaths: Byte,
    val assists: Byte,
    val impact: Short?,
    val endItem0Id: Short?,
    val endItem1Id: Short?,
    val endItem2Id: Short?,
    val endItem3Id: Short?,
    val endItem4Id: Short?,
    val endItem5Id: Short?,
    val endBackpack0Id: Short?,
    val endBackpack1Id: Short?,
    val endBackpack2Id: Short?,
    val endNeutralItemId: Short?,
    val itemPurchases: List<ItemPurchaseDto?>?,
)

internal class ItemPurchaseDto(
    val itemId: Int,
    val time: Int
)

internal enum class MatchPlayerPositionType(val title: String) {
    POSITION_1("POSITION_1"),
    POSITION_2("POSITION_2"),
    POSITION_3("POSITION_3"),
    POSITION_4("POSITION_4"),
    POSITION_5("POSITION_5"),
    UNKNOWN("UNKNOWN"),
    FILTERED("FILTERED"),
    ALL("ALL")
}
