package dev.nonoxy.d2buildhelper.core.data.repository.guides.mappers

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Backpack0
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Backpack1
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Backpack2
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Hero
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.InventoryChange
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item0
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item1
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item2
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item3
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item4
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Item5
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.ItemPurchase
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStats
import dev.nonoxy.d2buildhelper.graphql.GuidesQuery

fun GuidesQuery.Guide1.toGuide(): Guide {
    return Guide(
        hero = Hero(
            heroId = heroId.toString().toShort(),
            shortName = matchPlayer?.hero?.shortName ?: "",
            displayName = matchPlayer?.hero?.displayName ?: "",
        ),
        steamAccountId = steamAccountId.toString().toLong(),
        matchId = matchId.toString().toLong(),
        durationSeconds = this.match?.durationSeconds ?: 0,
        playerStats = matchPlayer!!.toPlayerStats()
    )
}

fun GuidesQuery.MatchPlayer.toPlayerStats(): PlayerStats {
    return PlayerStats(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant,
        kills = kills.toString().toByte(),
        deaths = deaths.toString().toByte(),
        assists = assists.toString().toByte(),
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
                ItemPurchase(itemId = itemPurchase.itemId, time = itemPurchase.time)
            }
        },
        inventoryChanges = stats?.inventoryReport?.map { inventoryReport -> inventoryReport?.toInventoryChange() }
        )
}

fun GuidesQuery.InventoryReport.toInventoryChange(): InventoryChange {
    return InventoryChange(
        item0 = item0?.run {
            Item0(
                itemId = item0.itemId,
                charges = item0.charges
            )
        },
        item1 = item1?.run {
            Item1(
                itemId = item1.itemId,
                charges = item1.charges
            )
        },
        item2 = item2?.run {
            Item2(
                itemId = item2.itemId,
                charges = item2.charges
            )
        },
        item3 = item3?.run {
            Item3(
                itemId = item3.itemId,
                charges = item3.charges
            )
        },
        item4 = item4?.run {
            Item4(
                itemId = item4.itemId,
                charges = item4.charges
            )
        },
        item5 = item5?.run {
            Item5(
                itemId = item5.itemId,
                charges = item5.charges
            )
        },
        backpack0 = backPack0?.run {
            Backpack0(
                itemId = backPack0.itemId,
            )
        },
        backpack1 = backPack1?.run {
            Backpack1(
                itemId = backPack1.itemId,
            )
        },
        backpack2 = backPack2?.run {
            Backpack2(
                itemId = backPack2.itemId,
            )
        },
    )
}