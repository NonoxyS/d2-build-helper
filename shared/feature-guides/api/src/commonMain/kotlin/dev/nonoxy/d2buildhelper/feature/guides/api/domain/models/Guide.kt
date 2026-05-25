package dev.nonoxy.d2buildhelper.feature.guides.api.domain

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId

data class Guide(
    val matchId: Long,
    val steamAccountId: Long,
    val durationSeconds: Int,
    val heroId: HeroId,
    val playerStats: PlayerStats,
)

data class PlayerStats(
    val position: MatchPlayerPosition?,
    val isRadiant: Boolean,
    val kills: Byte,
    val deaths: Byte,
    val assists: Byte,
    val impact: Short,
    val endNeutralItemId: ItemId?,
    val sortedEndItemPurchases: List<ItemPurchase>,
)

data class ItemPurchase(
    val itemId: ItemId,
    val time: Int?,
)

enum class MatchPlayerPosition {
    POSITION_1,
    POSITION_2,
    POSITION_3,
    POSITION_4,
    POSITION_5,
}
