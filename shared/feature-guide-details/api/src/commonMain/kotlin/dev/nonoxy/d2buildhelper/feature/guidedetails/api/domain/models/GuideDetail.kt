package dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models

import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.domain.MatchLane
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerRole

data class GuideDetail(
    val matchId: Long,
    val steamAccountId: Long,
    val didRadiantWin: Boolean?,
    val durationSeconds: Int?,
    val averageRank: Int?,
    val player: BuildPlayer,
    val lineup: List<LineupMember>,
)

data class BuildPlayer(
    val heroId: HeroId,
    val isRadiant: Boolean?,
    val isVictory: Boolean?,
    val position: MatchPlayerPosition?,
    val role: MatchPlayerRole?,
    val lane: MatchLane?,
    val level: Int?,
    val kills: Int?,
    val deaths: Int?,
    val assists: Int?,
    val impact: Int?,
    val goldPerMinute: Int?,
    val networth: Int?,
    val experiencePerMinute: Int?,
    val finalItemIds: List<ItemId>,
    val backpackItemIds: List<ItemId>,
    val neutralItemId: ItemId?,
    val abilityLearnEvents: List<AbilityLearnEvent>,
    val itemPurchases: List<ItemPurchase>,
    val networthPerMinute: List<Int>,
    val lastHitsPerMinute: List<Int>,
    val goldPerMinuteSeries: List<Int>,
)

data class AbilityLearnEvent(
    val time: Int?,
    val abilityId: AbilityId?,
    val level: Int?,
    val isTalent: Boolean,
    val isUltimate: Boolean,
)

data class LineupMember(
    val steamAccountId: Long?,
    val heroId: HeroId?,
    val isRadiant: Boolean?,
    val position: MatchPlayerPosition?,
    val role: MatchPlayerRole?,
)
