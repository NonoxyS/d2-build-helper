package dev.nonoxy.d2buildhelper.core.data.api.guides.mappers

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.ItemPurchaseDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.graphql.GuidesQuery
import dev.nonoxy.d2buildhelper.graphql.HeroGuidesQuery

internal fun GuidesQuery.Guide1.toGuideDto(): GuideDto {
    return GuideDto(
        hero = HeroDto(
            heroId = matchPlayer?.hero?.id.toString().toShort(),
            shortName = matchPlayer?.hero?.shortName ?: "",
            displayName = matchPlayer?.hero?.displayName ?: "Unknown",
        ),
        steamAccountId = steamAccountId.toString().toLong(),
        matchId = matchId.toString().toLong(),
        durationSeconds = this.match?.durationSeconds ?: 0,
        playerStats = matchPlayer!!.toPlayerStatsDto()
    )
}

internal fun GuidesQuery.MatchPlayer.toPlayerStatsDto(): PlayerStatsDto {
    return PlayerStatsDto(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant,
        kills = kills?.toString()?.toByte() ?: 0,
        deaths = deaths?.toString()?.toByte() ?: 0,
        assists = assists?.toString()?.toByte() ?: 0,
        impact = imp?.toString()?.toShortOrNull(),
        endItem0Id = item0Id?.toString()?.toShortOrNull(),
        endItem1Id = item1Id?.toString()?.toShortOrNull(),
        endItem2Id = item2Id?.toString()?.toShortOrNull(),
        endItem3Id = item3Id?.toString()?.toShortOrNull(),
        endItem4Id = item4Id?.toString()?.toShortOrNull(),
        endItem5Id = item5Id?.toString()?.toShortOrNull(),
        endBackpack0Id = backpack0Id?.toString()?.toShortOrNull(),
        endBackpack1Id = backpack1Id?.toString()?.toShortOrNull(),
        endBackpack2Id = backpack2Id?.toString()?.toShortOrNull(),
        endNeutralItemId = neutral0Id?.toString()?.toShortOrNull(),
        itemPurchases = stats?.itemPurchases?.map { itemPurchase ->
            itemPurchase?.run {
                ItemPurchaseDto(itemId = itemPurchase.itemId, time = itemPurchase.time)
            }
        },
    )
}

internal fun HeroGuidesQuery.Guide1.toGuideDto(): GuideDto {
    return GuideDto(
        hero = HeroDto(
            heroId = matchPlayer?.hero?.id.toString().toShort(),
            shortName = matchPlayer?.hero?.shortName ?: "",
            displayName = matchPlayer?.hero?.displayName ?: "Unknown",
        ),
        steamAccountId = steamAccountId.toString().toLong(),
        matchId = matchId.toString().toLong(),
        durationSeconds = this.match?.durationSeconds ?: 0,
        playerStats = matchPlayer!!.toPlayerStatsDto()
    )
}

internal fun HeroGuidesQuery.MatchPlayer.toPlayerStatsDto(): PlayerStatsDto {
    return PlayerStatsDto(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant,
        kills = kills?.toString()?.toByte() ?: 0,
        deaths = deaths?.toString()?.toByte() ?: 0,
        assists = assists?.toString()?.toByte() ?: 0,
        impact = imp?.toString()?.toShortOrNull(),
        endItem0Id = item0Id?.toString()?.toShortOrNull(),
        endItem1Id = item1Id?.toString()?.toShortOrNull(),
        endItem2Id = item2Id?.toString()?.toShortOrNull(),
        endItem3Id = item3Id?.toString()?.toShortOrNull(),
        endItem4Id = item4Id?.toString()?.toShortOrNull(),
        endItem5Id = item5Id?.toString()?.toShortOrNull(),
        endBackpack0Id = backpack0Id?.toString()?.toShortOrNull(),
        endBackpack1Id = backpack1Id?.toString()?.toShortOrNull(),
        endBackpack2Id = backpack2Id?.toString()?.toShortOrNull(),
        endNeutralItemId = neutral0Id?.toString()?.toShortOrNull(),
        itemPurchases = stats?.itemPurchases?.map { itemPurchase ->
            itemPurchase?.run {
                ItemPurchaseDto(itemId = itemPurchase.itemId, time = itemPurchase.time)
            }
        },
    )
}
