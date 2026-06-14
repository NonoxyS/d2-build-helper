package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.MatchLane
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerRole
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.AbilityLearnEvent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.BuildPlayer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.LineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteAbilityLearnEventResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailPlayerResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteItemPurchaseResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteLineupMemberResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteMatchLane
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteMatchPlayerRole

internal interface GuideDetailMapper : Mapper<RemoteGuideDetailResponse, GuideDetail>

internal class GuideDetailMapperImpl : GuideDetailMapper {

    override fun map(item: RemoteGuideDetailResponse): GuideDetail = GuideDetail(
        matchId = item.matchId,
        steamAccountId = item.steamAccountId,
        didRadiantWin = item.didRadiantWin,
        durationSeconds = item.durationSeconds,
        averageRank = item.averageRank,
        player = item.player.toDomain(),
        lineup = item.lineup.map { it.toDomain() },
    )

    private fun RemoteGuideDetailPlayerResponse.toDomain(): BuildPlayer = BuildPlayer(
        heroId = HeroId(heroId.toShort()),
        isRadiant = isRadiant,
        isVictory = isVictory,
        position = position?.toDomain(),
        role = role?.toDomain(),
        lane = lane?.toDomain(),
        level = level,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = imp,
        goldPerMinute = goldPerMinute,
        networth = networth,
        finalItemIds = finalItemIds.map { ItemId(it.toShort()) },
        backpackItemIds = backpackItemIds.map { ItemId(it.toShort()) },
        neutralItemId = neutralItemId?.toItemId(),
        abilityLearnEvents = abilityLearnEvents.map { it.toDomain() },
        itemPurchases = itemPurchases.map { it.toDomain() },
        networthPerMinute = networthPerMinute,
        lastHitsPerMinute = lastHitsPerMinute,
        goldPerMinuteSeries = goldPerMinuteSeries,
    )

    private fun RemoteAbilityLearnEventResponse.toDomain(): AbilityLearnEvent = AbilityLearnEvent(
        time = time,
        abilityId = abilityId?.let { AbilityId(it.toShort()) },
        level = level,
        isTalent = isTalent ?: false,
        isUltimate = isUltimate ?: false,
    )

    private fun RemoteItemPurchaseResponse.toDomain(): ItemPurchase = ItemPurchase(
        itemId = ItemId(itemId.toShort()),
        time = time,
    )

    private fun RemoteLineupMemberResponse.toDomain(): LineupMember = LineupMember(
        steamAccountId = steamAccountId,
        heroId = heroId?.let { HeroId(it.toShort()) },
        isRadiant = isRadiant,
        position = position?.toDomain(),
        role = role?.toDomain(),
    )

    private fun Int.toItemId(): ItemId = ItemId(toShort())

    private fun RemoteMatchPlayerPosition.toDomain(): MatchPlayerPosition? = when (this) {
        RemoteMatchPlayerPosition.POSITION_1 -> MatchPlayerPosition.POSITION_1
        RemoteMatchPlayerPosition.POSITION_2 -> MatchPlayerPosition.POSITION_2
        RemoteMatchPlayerPosition.POSITION_3 -> MatchPlayerPosition.POSITION_3
        RemoteMatchPlayerPosition.POSITION_4 -> MatchPlayerPosition.POSITION_4
        RemoteMatchPlayerPosition.POSITION_5 -> MatchPlayerPosition.POSITION_5
        RemoteMatchPlayerPosition.UNKNOWN -> null
    }

    private fun RemoteMatchPlayerRole.toDomain(): MatchPlayerRole? = when (this) {
        RemoteMatchPlayerRole.CORE -> MatchPlayerRole.CORE
        RemoteMatchPlayerRole.LIGHT_SUPPORT -> MatchPlayerRole.LIGHT_SUPPORT
        RemoteMatchPlayerRole.HARD_SUPPORT -> MatchPlayerRole.HARD_SUPPORT
        RemoteMatchPlayerRole.UNKNOWN -> null
    }

    private fun RemoteMatchLane.toDomain(): MatchLane? = when (this) {
        RemoteMatchLane.ROAMING -> MatchLane.ROAMING
        RemoteMatchLane.SAFE_LANE -> MatchLane.SAFE_LANE
        RemoteMatchLane.MID_LANE -> MatchLane.MID_LANE
        RemoteMatchLane.OFF_LANE -> MatchLane.OFF_LANE
        RemoteMatchLane.JUNGLE -> MatchLane.JUNGLE
        RemoteMatchLane.UNKNOWN -> null
    }
}
