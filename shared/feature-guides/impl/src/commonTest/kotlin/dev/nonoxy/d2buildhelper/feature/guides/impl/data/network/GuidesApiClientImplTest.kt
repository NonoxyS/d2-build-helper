package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network

import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClientImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val GUIDES_JSON = """
{
  "pagination": { "page": 0, "pageSize": 50, "hasMore": false },
  "guides": [
    {
      "matchId": 7891234567,
      "steamAccountId": 123456789,
      "durationSeconds": 1834,
      "hero": { "id": 1, "shortName": "antimage", "displayName": "Anti-Mage" },
      "player": {
        "position": "POSITION_1",
        "isRadiant": true,
        "kills": 12, "deaths": 3, "assists": 7,
        "impact": 64,
        "finalItemIds": [1, 50],
        "backpackItemIds": [],
        "neutralItemId": 100,
        "itemPurchases": [
          { "itemId": 1, "time": 620 },
          { "itemId": 50, "time": 410 }
        ]
      }
    }
  ]
}
"""

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesApiClientImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun apiClient(status: HttpStatusCode, body: String): GuidesApiClient {
        val engine = MockEngine { respond(content = body, status = status) }
        val httpClient = HttpClient(engine) {
            defaultRequest { url("http://localhost") }
        }
        return GuidesApiClientImpl(KtorClientImpl(httpClient, json))
    }

    @Test
    fun `getGuides parses a valid payload`() = runTest {
        val result = apiClient(HttpStatusCode.OK, GUIDES_JSON).getGuides()

        assertTrue(result.isSuccess)
        val page = result.getOrThrow()
        assertEquals(1, page.guides.size)
        val guide = page.guides.single()
        assertEquals(7891234567L, guide.matchId)
        assertEquals(1, guide.hero.id)
        assertEquals(listOf(1, 50), guide.player.finalItemIds)
        assertEquals(2, guide.player.itemPurchases.size)
    }

    @Test
    fun `getGuides returns failure on a 500 response`() = runTest {
        val result = apiClient(HttpStatusCode.InternalServerError, "{}").getGuides()

        assertTrue(result.isFailure)
    }
}
