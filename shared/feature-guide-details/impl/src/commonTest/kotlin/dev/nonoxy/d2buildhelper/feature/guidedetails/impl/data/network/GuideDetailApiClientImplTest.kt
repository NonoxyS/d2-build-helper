package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network

import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClientImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
    "position": "POSITION_1",
    "kills": 12, "deaths": 3, "assists": 7,
    "imp": 64,
    "finalItemIds": [1, 50],
    "backpackItemIds": [],
    "neutralItemId": 100,
    "abilityLearnEvents": [
      { "time": 0, "abilityId": 5001, "level": 1, "isTalent": false, "isUltimate": false }
    ],
    "itemPurchases": [
      { "itemId": 1, "time": 620 },
      { "itemId": 50, "time": 410 }
    ],
    "inventorySnapshots": [
      { "itemIds": [1, null], "backpackIds": [], "neutralId": 100 }
    ],
    "networthPerMinute": [100, 500],
    "lastHitsPerMinute": [2, 8],
    "goldPerMinuteSeries": [300, 420]
  },
  "lineup": [
    { "steamAccountId": 123456789, "heroId": 1, "isRadiant": true, "position": "POSITION_1" }
  ]
}
"""

@OptIn(ExperimentalCoroutinesApi::class)
class GuideDetailApiClientImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun engineClient(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): Pair<GuideDetailApiClient, MockEngine> {
        val engine = MockEngine { respond(content = body, status = status) }
        val httpClient = HttpClient(engine) { defaultRequest { url("http://localhost") } }
        return GuideDetailApiClientImpl(KtorClientImpl(httpClient, json)) to engine
    }

    @Test
    fun `getGuideDetail parses a valid payload`() = runTest {
        val (api, _) = engineClient(GUIDE_DETAIL_JSON)
        val result = api.getGuideDetail(matchId = 7891234567L, steamAccountId = 123456789L)

        assertTrue(result.isSuccess)
        val detail = result.getOrThrow()
        assertEquals(7891234567L, detail.matchId)
        assertEquals(123456789L, detail.steamAccountId)
        assertEquals(1, detail.player.heroId)
        assertEquals(1, detail.lineup.size)
    }

    @Test
    fun `getGuideDetail requests the matchId steamAccountId path`() = runTest {
        val (api, engine) = engineClient(GUIDE_DETAIL_JSON)
        api.getGuideDetail(matchId = 7891234567L, steamAccountId = 123456789L)

        val path = engine.requestHistory.single().url.encodedPath
        assertEquals("/v1/guides/7891234567/123456789", path)
    }

    @Test
    fun `getGuideDetail returns failure on a 500 response`() = runTest {
        val (api, _) = engineClient("{}", HttpStatusCode.InternalServerError)
        val result = api.getGuideDetail(matchId = 1L, steamAccountId = 2L)
        assertTrue(result.isFailure)
    }
}
