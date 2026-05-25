package dev.nonoxy.d2buildhelper.feature.guides.api.domain

import dev.nonoxy.d2buildhelper.core.domain.models.Hero

class Guide(
    val hero: Hero,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: PlayerStats
)

class PlayerStats(
    val position: MatchPlayerPosition,
    val isRadiant: Boolean,
    val kills: Byte,
    val deaths: Byte,
    val assists: Byte,
    val impact: Short,
    val endNeutralItemId: Short?,
    val sortedEndItemPurchases: List<ItemPurchase>,
)

class ItemPurchase(
    val itemId: Short,
    val time: Int?
)

enum class MatchPlayerPosition(val title: String) {
    POSITION_1("POSITION_1"),
    POSITION_2("POSITION_2"),
    POSITION_3("POSITION_3"),
    POSITION_4("POSITION_4"),
    POSITION_5("POSITION_5"),
    UNKNOWN("UNKNOWN"),
    FILTERED("FILTERED"),
    ALL("ALL")
}
