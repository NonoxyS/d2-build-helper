package dev.nonoxy.d2buildhelper.core.resources.data.network

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

private const val CONSTANTS_JSON = """
{
  "gameVersionId": 179,
  "patch": "7.39",
  "heroes": [
    { "id": 1, "shortName": "antimage", "displayName": "Anti-Mage", "iconUrl": "https://cdn.example/heroes/antimage.png" }
  ],
  "items": [
    { "id": 1, "shortName": "blink", "displayName": "Blink Dagger", "iconUrl": "https://cdn.example/items/blink.png" }
  ],
  "abilities": [
    { "id": 5001, "name": "antimage_mana_break", "iconUrl": "https://cdn.example/abilities/mana_break.png" }
  ]
}
"""

@OptIn(ExperimentalCoroutinesApi::class)
class ConstantsApiClientImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun apiClient(status: HttpStatusCode, body: String): ConstantsApiClient {
        val engine = MockEngine { respond(content = body, status = status) }
        val httpClient = HttpClient(engine) {
            defaultRequest { url("http://localhost") }
        }
        return ConstantsApiClientImpl(KtorClientImpl(httpClient, json))
    }

    @Test
    fun `getConstants parses a valid payload`() = runTest {
        val result = apiClient(HttpStatusCode.OK, CONSTANTS_JSON).getConstants()

        assertTrue(result.isSuccess)
        val constants = result.getOrThrow()
        assertEquals(1, constants.heroes.size)
        assertEquals("antimage", constants.heroes.single().shortName)
        assertEquals(1, constants.items.size)
        assertEquals(1, constants.abilities.size)
        assertEquals("https://cdn.example/abilities/mana_break.png", constants.abilities.single().iconUrl)
    }

    @Test
    fun `getConstants returns failure on a 503 response`() = runTest {
        val result = apiClient(HttpStatusCode.ServiceUnavailable, "{}").getConstants()

        assertTrue(result.isFailure)
    }
}
