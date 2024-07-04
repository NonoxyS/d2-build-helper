package dev.nonoxy.d2buildhelper.features.guides.domain.models

class GuideUI(
    val hero: HeroUI,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: PlayerStatsUI
)

data class HeroUI(
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
    val endNeutralItemId: Short?,
    val sortedEndItemPurchases: List<ItemPurchaseUI>,
)

class ItemPurchaseUI(
    val itemId: Short,
    val time: Int?
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