package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

private const val GUIDE_DETAIL_JSON = """
{
  "matchId": 7891234567,
  "steamAccountId": 123456789,
  "gameVersionId": 174,
  "didRadiantWin": true,
  "durationSeconds": 1834,
  "averageRank": 80,
  "player": {
    "heroId": 1,
    "isRadiant": true,
    "isVictory": true,
    "position": "POSITION_4",
    "role": "HARD_SUPPORT",
    "lane": "OFF_LANE",
    "level": 25,
    "kills": 12, "deaths": 3, "assists": 7,
    "imp": 64,
    "goldPerMinute": 540,
    "networth": 21000,
    "finalItemIds": [1, 50],
    "backpackItemIds": [42],
    "neutralItemId": 100,
    "abilityLearnEvents": [
      { "time": -89, "abilityId": 5001, "level": 1, "isTalent": false, "isUltimate": false },
      { "time": 600, "abilityId": 5004, "level": 6, "isTalent": false, "isUltimate": true }
    ],
    "itemPurchases": [
      { "itemId": 1, "time": 620 },
      { "itemId": 50, "time": 410 }
    ],
    "networthPerMinute": [0, 300, 700],
    "lastHitsPerMinute": [0, 8, 15],
    "goldPerMinuteSeries": [0, 280, 540]
  },
  "lineup": [
    { "steamAccountId": 123456789, "heroId": 1, "isRadiant": true, "position": "POSITION_4", "role": "HARD_SUPPORT" },
    { "steamAccountId": null, "heroId": 22, "isRadiant": false, "position": "GARBAGE", "role": null }
  ]
}
"""

class RemoteGuideDetailResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `deserializes a full guide detail payload`() {
        val response = json.decodeFromString<RemoteGuideDetailResponse>(GUIDE_DETAIL_JSON)

        assertEquals(7891234567L, response.matchId)
        assertEquals(123456789L, response.steamAccountId)
        assertEquals(174, response.gameVersionId)
        assertEquals(true, response.didRadiantWin)
        assertEquals(1834, response.durationSeconds)
        assertEquals(80, response.averageRank)
    }

    @Test
    fun `deserializes the player block`() {
        val player = json.decodeFromString<RemoteGuideDetailResponse>(GUIDE_DETAIL_JSON).player

        assertEquals(1, player.heroId)
        assertEquals(true, player.isRadiant)
        assertEquals(true, player.isVictory)
        assertEquals(RemoteMatchPlayerPosition.POSITION_4, player.position)
        assertEquals("HARD_SUPPORT", player.role)
        assertEquals("OFF_LANE", player.lane)
        assertEquals(25, player.level)
        assertEquals(12, player.kills)
        assertEquals(64, player.imp)
        assertEquals(540, player.goldPerMinute)
        assertEquals(21000, player.networth)
        assertEquals(listOf(1, 50), player.finalItemIds)
        assertEquals(listOf(42), player.backpackItemIds)
        assertEquals(100, player.neutralItemId)
        assertEquals(listOf(0, 300, 700), player.networthPerMinute)
        assertEquals(listOf(0, 8, 15), player.lastHitsPerMinute)
        assertEquals(listOf(0, 280, 540), player.goldPerMinuteSeries)
    }

    @Test
    fun `deserializes nested ability learn events`() {
        val events = json.decodeFromString<RemoteGuideDetailResponse>(GUIDE_DETAIL_JSON).player.abilityLearnEvents

        assertEquals(2, events.size)
        assertEquals(-89, events.first().time)
        assertEquals(5001, events.first().abilityId)
        assertEquals(false, events.first().isUltimate)
        assertEquals(true, events.last().isUltimate)
    }

    @Test
    fun `deserializes item purchases`() {
        val player = json.decodeFromString<RemoteGuideDetailResponse>(GUIDE_DETAIL_JSON).player

        assertEquals(2, player.itemPurchases.size)
        assertEquals(1, player.itemPurchases.first().itemId)
        assertEquals(620, player.itemPurchases.first().time)
    }

    @Test
    fun `deserializes lineup with unknown position falling back`() {
        val lineup = json.decodeFromString<RemoteGuideDetailResponse>(GUIDE_DETAIL_JSON).lineup

        assertEquals(2, lineup.size)
        assertEquals(123456789L, lineup.first().steamAccountId)
        assertEquals(RemoteMatchPlayerPosition.POSITION_4, lineup.first().position)
        assertNull(lineup.last().steamAccountId)
        assertEquals(22, lineup.last().heroId)
        // unknown position string falls back to UNKNOWN via fallbackEnumSerializer
        assertEquals(RemoteMatchPlayerPosition.UNKNOWN, lineup.last().position)
        assertNull(lineup.last().role)
    }

    @Test
    fun `tolerates a missing optional lineup`() {
        val minimal = """
            {
              "matchId": 1, "steamAccountId": 2, "gameVersionId": 174,
              "player": {
                "heroId": 1,
                "finalItemIds": [], "backpackItemIds": [],
                "abilityLearnEvents": [], "itemPurchases": [],
                "networthPerMinute": [], "lastHitsPerMinute": [], "goldPerMinuteSeries": []
              }
            }
        """.trimIndent()

        val response = json.decodeFromString<RemoteGuideDetailResponse>(minimal)

        assertNull(response.didRadiantWin)
        assertTrue(response.lineup.isEmpty())
        assertNull(response.player.position)
    }
}
