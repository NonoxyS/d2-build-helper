package com.nonoxy.d2buildhelper.data

import com.nonoxy.GuidesInfoQuery
import com.nonoxy.HeroBuildQuery
import com.nonoxy.HeroGuidesInfoQuery
import com.nonoxy.d2buildhelper.domain.model.AbilityLearnEvent
import com.nonoxy.d2buildhelper.domain.model.Backpack0
import com.nonoxy.d2buildhelper.domain.model.Backpack1
import com.nonoxy.d2buildhelper.domain.model.Backpack2
import com.nonoxy.d2buildhelper.domain.model.GuideInfo
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.model.InventoryChange
import com.nonoxy.d2buildhelper.domain.model.Item0
import com.nonoxy.d2buildhelper.domain.model.Item1
import com.nonoxy.d2buildhelper.domain.model.Item2
import com.nonoxy.d2buildhelper.domain.model.Item3
import com.nonoxy.d2buildhelper.domain.model.Item4
import com.nonoxy.d2buildhelper.domain.model.Item5
import com.nonoxy.d2buildhelper.domain.model.ItemPurchase
import com.nonoxy.d2buildhelper.domain.model.MatchPlayerPositionType

fun GuidesInfoQuery.Guide.toGuideInfo(): HeroGuideInfo {
    return HeroGuideInfo(
        heroId = heroId.toString().toShort(),
        guidesInfo = guides?.map { it ->
            it?.run {
                GuideInfo(
                    matchId = it.matchId.toString().toLong(),
                    steamAccountId = it.steamAccountId.toString().toLong()
                )
            }
        }
    )
}

fun HeroGuidesInfoQuery.Guide.toHeroGuideInfo(): HeroGuideInfo {
    return HeroGuideInfo(
        heroId = heroId.toString().toShort(),
        guidesInfo = guides?.map { it ->
            it?.run {
                GuideInfo(
                    matchId = it.matchId.toString().toLong(),
                    steamAccountId = it.steamAccountId.toString().toLong()
                )
            }
        }
    )
}

fun HeroBuildQuery.Player.toHeroGuideBuild(durationSeconds: Int): HeroGuideBuild {
    return HeroGuideBuild(
        matchId = matchId.toString().toLong(),
        steamAccountId = steamAccountId.toString().toLong(),
        durationSeconds = durationSeconds,
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
        itemPurchases = stats?.itemPurchases?.map { it ->
            it?.run {
                ItemPurchase(
                    itemId = it.itemId,
                    time = it.time
                )
            } },

        inventoryChanges = stats?.inventoryReport?.map { it ->
            it.run {
                InventoryChange(
                    item0 = it?.item0?.let {
                        Item0(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    item1 = it?.item1?.let {
                        Item1(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    item2 = it?.item2?.let {
                        Item2(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    item3 = it?.item3?.let {
                        Item3(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    item4 = it?.item4?.let {
                        Item4(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    item5 = it?.item5?.let {
                        Item5(
                            itemId = it.itemId,
                            charges = it.charges
                        ) },
                    backpack0 = it?.backPack0?.let {
                        Backpack0(
                            itemId = it.itemId
                        ) },
                    backpack1 = it?.backPack1?.let {
                        Backpack1(
                            itemId = it.itemId
                        ) },
                    backpack2 = it?.backPack2?.let {
                        Backpack2(
                            itemId = it.itemId
                        ) }
                )
            } },

        abilityLearnEvents = playbackData?.abilityLearnEvents?.map { it ->
            it?.run {
                AbilityLearnEvent(
                    abilityId = it.abilityId.toString().toShort(),
                    levelAbility = it.level,
                    levelObtained = it.levelObtained,
                    isTalent = it.isTalent
                )
            }
        }
    )
}