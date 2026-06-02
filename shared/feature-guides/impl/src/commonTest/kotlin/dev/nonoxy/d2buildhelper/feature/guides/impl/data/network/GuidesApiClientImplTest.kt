package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network

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

private const val GUIDES_JSON = """
{
  "pagination": { "page": 0, "pageSize": 50, "hasMore": false },
  "gameVersionId": 174,
  "guides": [
    {
      "matchId": 7891234567,
      "steamAccountId": 123456789,
      "durationSeconds": 1834,
      "heroId": 1,
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

    private fun engineClient(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): Pair<GuidesApiClient, MockEngine> {
        val engine = MockEngine { respond(content = body, status = status) }
        val httpClient = HttpClient(engine) { defaultRequest { url("http://localhost") } }
        return GuidesApiClientImpl(KtorClientImpl(httpClient, json)) to engine
    }

    @Test
    fun `getGuides parses a valid payload`() = runTest {
        val (api, _) = engineClient(GUIDES_JSON)
        val result = api.getGuides(heroId = null, position = null, isRadiant = null, page = 0, pageSize = 20)

        assertTrue(result.isSuccess)
        val page = result.getOrThrow()
        assertEquals(174, page.gameVersionId)
        assertEquals(1, page.guides.size)
        assertEquals(7891234567L, page.guides.single().matchId)
    }

    @Test
    fun `getGuides returns failure on a 500 response`() = runTest {
        val (api, _) = engineClient("{}", HttpStatusCode.InternalServerError)
        val result = api.getGuides(heroId = null, position = null, isRadiant = null, page = 0, pageSize = 20)
        assertTrue(result.isFailure)
    }

    @Test
    fun `getGuides forwards all query parameters`() = runTest {
        val (api, engine) = engineClient(GUIDES_JSON)
        api.getGuides(heroId = 5, position = "POSITION_2", isRadiant = true, page = 3, pageSize = 20)

        val params = engine.requestHistory.single().url.parameters
        assertEquals("5", params["heroId"])
        assertEquals("POSITION_2", params["position"])
        assertEquals("true", params["isRadiant"])
        assertEquals("3", params["page"])
        assertEquals("20", params["pageSize"])
    }

    @Test
    fun `getGuides omits null filter parameters`() = runTest {
        val (api, engine) = engineClient(GUIDES_JSON)
        api.getGuides(heroId = null, position = null, isRadiant = null, page = 0, pageSize = 20)

        val params = engine.requestHistory.single().url.parameters
        assertEquals(null, params["heroId"])
        assertEquals(null, params["position"])
        assertEquals(null, params["isRadiant"])
        assertEquals("0", params["page"])
        assertEquals("20", params["pageSize"])
    }
}
