package dev.nonoxy.d2buildhelper.feature.guides.api.domain.models

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition

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
