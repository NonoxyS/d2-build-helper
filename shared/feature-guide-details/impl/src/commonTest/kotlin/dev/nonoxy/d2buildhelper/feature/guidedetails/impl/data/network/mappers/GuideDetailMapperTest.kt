package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers

import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteAbilityLearnEventResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailPlayerResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteInventorySnapshotResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteItemPurchaseResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteLineupMemberResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteMatchPlayerPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GuideDetailMapperTest {

    private val mapper: GuideDetailMapper = GuideDetailMapperImpl()

    private val response = RemoteGuideDetailResponse(
        matchId = 7891234567L,
        steamAccountId = 123456789L,
        gameVersionId = 174,
        didRadiantWin = true,
        durationSeconds = 1834,
        averageRank = 80,
        player = RemoteGuideDetailPlayerResponse(
            heroId = 1,
            isRadiant = true,
            isVictory = true,
            position = RemoteMatchPlayerPosition.POSITION_4,
            role = "HARD_SUPPORT",
            lane = "OFF_LANE",
            level = 25,
            kills = 12,
            deaths = 3,
            assists = 7,
            imp = 64,
            goldPerMinute = 540,
            networth = 21000,
            finalItemIds = listOf(1, 50),
            backpackItemIds = listOf(42),
            neutralItemId = 100,
            abilityLearnEvents = listOf(
                RemoteAbilityLearnEventResponse(
                    time = -89,
                    abilityId = 5001,
                    level = 1,
                    isTalent = null,
                    isUltimate = null,
                ),
                RemoteAbilityLearnEventResponse(
                    time = 600,
                    abilityId = null,
                    level = 6,
                    isTalent = true,
                    isUltimate = true,
                ),
            ),
            itemPurchases = listOf(
                RemoteItemPurchaseResponse(itemId = 1, time = 620),
                RemoteItemPurchaseResponse(itemId = 50, time = null),
            ),
            inventorySnapshots = listOf(
                RemoteInventorySnapshotResponse(
                    itemIds = listOf(1, null, 50),
                    backpackIds = listOf(null),
                    neutralId = 100,
                ),
            ),
            networthPerMinute = listOf(0, 300, 700),
            lastHitsPerMinute = listOf(0, 8, 15),
            goldPerMinuteSeries = listOf(0, 280, 540),
        ),
        lineup = listOf(
            RemoteLineupMemberResponse(
                steamAccountId = 123456789L,
                heroId = 1,
                isRadiant = true,
                position = RemoteMatchPlayerPosition.POSITION_4,
                role = "HARD_SUPPORT",
            ),
            RemoteLineupMemberResponse(
                steamAccountId = null,
                heroId = 22,
                isRadiant = false,
                position = RemoteMatchPlayerPosition.UNKNOWN,
                role = null,
            ),
        ),
    )

    @Test
    fun `maps top-level fields`() {
        val detail = mapper.map(response)

        assertEquals(7891234567L, detail.matchId)
        assertEquals(123456789L, detail.steamAccountId)
        assertEquals(true, detail.didRadiantWin)
        assertEquals(1834, detail.durationSeconds)
        assertEquals(80, detail.averageRank)
    }

    @Test
    fun `maps player ids into value classes and imp into impact`() {
        val player = mapper.map(response).player

        assertEquals(HeroId(1), player.heroId)
        assertEquals(MatchPlayerPosition.POSITION_4, player.position)
        assertEquals("HARD_SUPPORT", player.role)
        assertEquals("OFF_LANE", player.lane)
        assertEquals(64, player.impact)
        assertEquals(listOf(ItemId(1), ItemId(50)), player.finalItemIds)
        assertEquals(listOf(ItemId(42)), player.backpackItemIds)
        assertEquals(ItemId(100), player.neutralItemId)
        assertEquals(listOf(0, 300, 700), player.networthPerMinute)
        assertEquals(listOf(0, 8, 15), player.lastHitsPerMinute)
        assertEquals(listOf(0, 280, 540), player.goldPerMinuteSeries)
    }

    @Test
    fun `defaults missing ability talent and ultimate flags to false`() {
        val events = mapper.map(response).player.abilityLearnEvents

        assertEquals(AbilityId(5001), events.first().abilityId)
        assertEquals(false, events.first().isTalent)
        assertEquals(false, events.first().isUltimate)
        assertNull(events.last().abilityId)
        assertEquals(true, events.last().isTalent)
        assertEquals(true, events.last().isUltimate)
    }

    @Test
    fun `maps item purchases preserving null time`() {
        val purchases = mapper.map(response).player.itemPurchases

        assertEquals(2, purchases.size)
        assertEquals(ItemId(1), purchases.first().itemId)
        assertEquals(620, purchases.first().time)
        assertEquals(ItemId(50), purchases.last().itemId)
        assertNull(purchases.last().time)
    }

    @Test
    fun `maps inventory snapshots preserving null slots`() {
        val snapshot = mapper.map(response).player.inventorySnapshots.single()

        assertEquals(listOf(ItemId(1), null, ItemId(50)), snapshot.itemIds)
        assertEquals(listOf(null), snapshot.backpackIds)
        assertEquals(ItemId(100), snapshot.neutralId)
    }

    @Test
    fun `maps lineup with unknown position to null`() {
        val lineup = mapper.map(response).lineup

        assertEquals(2, lineup.size)
        assertEquals(HeroId(1), lineup.first().heroId)
        assertEquals(MatchPlayerPosition.POSITION_4, lineup.first().position)
        assertNull(lineup.last().steamAccountId)
        assertEquals(HeroId(22), lineup.last().heroId)
        assertNull(lineup.last().position)
        assertNull(lineup.last().role)
    }
}
