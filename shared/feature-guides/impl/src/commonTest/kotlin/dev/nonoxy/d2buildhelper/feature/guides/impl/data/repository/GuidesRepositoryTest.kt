package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.FakeGuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideHeroResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidePlayerResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteItemPurchaseResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemotePaginationResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesRepositoryTest {

    private fun page(vararg guides: RemoteGuideResponse) = RemoteGuidesPageResponse(
        pagination = RemotePaginationResponse(page = 0, pageSize = 50, hasMore = false),
        guides = guides.toList(),
    )

    @Test
    fun `getGuides maps DTO to domain and orders end items by time with nulls last`() = runTest {
        val dto = RemoteGuideResponse(
            matchId = 100L,
            steamAccountId = 1L,
            durationSeconds = 1800,
            hero = RemoteGuideHeroResponse(id = 1, shortName = "antimage", displayName = "Anti-Mage"),
            player = RemoteGuidePlayerResponse(
                position = "POSITION_1",
                isRadiant = true,
                kills = 10,
                deaths = 1,
                assists = 5,
                impact = 70,
                finalItemIds = listOf(42, 43),
                backpackItemIds = emptyList(),
                neutralItemId = null,
                itemPurchases = listOf(
                    RemoteItemPurchaseResponse(itemId = 42, time = 200),
                    RemoteItemPurchaseResponse(itemId = 43, time = 100),
                ),
            ),
        )
        val api = FakeGuidesApiClient(guides = Result.success(page(dto)))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides().getOrThrow()

        assertEquals(1, result.size)
        val guide = result.single()
        assertEquals("Anti-Mage", guide.hero.displayName)
        assertEquals(MatchPlayerPosition.POSITION_1, guide.playerStats.position)
        val purchases = guide.playerStats.sortedEndItemPurchases
        assertEquals(2, purchases.size)
        assertEquals(43.toShort(), purchases[0].itemId)
        assertEquals(100, purchases[0].time)
        assertEquals(42.toShort(), purchases[1].itemId)
        assertEquals(200, purchases[1].time)
    }

    @Test
    fun `getGuides surfaces upstream failure unchanged`() = runTest {
        val boom = IllegalStateException("network")
        val api = FakeGuidesApiClient(guides = Result.failure(boom))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides()

        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
    }

    @Test
    fun `getGuides applies fallbacks for nullable DTO fields`() = runTest {
        val dto = RemoteGuideResponse(
            matchId = 200L,
            steamAccountId = 99L,
            durationSeconds = null,
            hero = RemoteGuideHeroResponse(id = 5, shortName = null, displayName = null),
            player = RemoteGuidePlayerResponse(),
        )
        val repo: GuidesRepository = GuidesRepositoryImpl(
            FakeGuidesApiClient(guides = Result.success(page(dto))),
            TestCoroutineDispatchers(),
        )

        val guide = repo.getGuides().getOrThrow().single()
        val stats = guide.playerStats

        assertEquals("", guide.hero.shortName)
        assertEquals("", guide.hero.displayName)
        assertEquals(0, guide.durationSeconds)
        assertEquals(MatchPlayerPosition.UNKNOWN, stats.position)
        assertEquals(true, stats.isRadiant)
        assertEquals(25.toShort(), stats.impact)
        assertNull(stats.endNeutralItemId)
        assertTrue(stats.sortedEndItemPurchases.isEmpty())
    }

    @Test
    fun `getHeroGuides reads the hero-specific page and maps it to domain`() = runTest {
        val heroDto = RemoteGuideResponse(
            matchId = 300L,
            steamAccountId = 7L,
            durationSeconds = 2400,
            hero = RemoteGuideHeroResponse(id = 8, shortName = "juggernaut", displayName = "Juggernaut"),
            player = RemoteGuidePlayerResponse(position = "POSITION_1", isRadiant = false),
        )
        val api = FakeGuidesApiClient(
            guides = Result.success(page()),
            heroGuides = Result.success(page(heroDto)),
        )
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getHeroGuides(heroId = 8).getOrThrow()

        assertEquals(1, result.size)
        val guide = result.single()
        assertEquals(8.toShort(), guide.hero.id)
        assertEquals("Juggernaut", guide.hero.displayName)
        assertEquals(false, guide.playerStats.isRadiant)
    }
}
