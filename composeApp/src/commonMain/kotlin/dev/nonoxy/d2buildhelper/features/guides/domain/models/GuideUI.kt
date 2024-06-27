package dev.nonoxy.d2buildhelper.features.guides.domain.models

class GuideUI(
    val hero: Hero,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: PlayerStatsUI
)

class Hero(
    val heroId: Short,
    val shortName: String,
    val displayName: String
)

class PlayerStatsUI(
    val position: MatchPlayerPositionType,
    val isRadiant: Boolean,
    val kills: Byte,
    val deaths: Byte,
    val assists: Byte,
    val impact: Short,
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
    val sortedEndItemPurchases: List<ItemPurchase>,
)

class ItemPurchase(
    val itemId: Int,
    val time: Int
)

enum class MatchPlayerPositionType(val title: String) {
    POSITION_1("POSITION_1"),
    POSITION_2("POSITION_2"),
    POSITION_3("POSITION_3"),
    POSITION_4("POSITION_4"),
    POSITION_5("POSITION_5"),
    UNKNOWN("UNKNOWN"),
    FILTERED("FILTERED"),
    ALL("ALL")
}