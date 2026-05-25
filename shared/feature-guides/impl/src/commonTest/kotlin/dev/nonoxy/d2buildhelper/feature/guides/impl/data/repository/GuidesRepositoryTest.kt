package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.FakeGuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers.GuidesPageMapperImpl
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidePlayerResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteItemPurchaseResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemotePaginationResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesRepositoryTest {

    private fun page(vararg guides: RemoteGuideResponse, gameVersionId: Int = 174) = RemoteGuidesPageResponse(
        pagination = RemotePaginationResponse(page = 0, pageSize = 50, hasMore = false),
        gameVersionId = gameVersionId,
        guides = guides.toList(),
    )

    @Test
    fun `getGuides maps DTO to domain and orders end items by time with nulls last`() = runTest {
        val dto = RemoteGuideResponse(
            matchId = 100L,
            steamAccountId = 1L,
            durationSeconds = 1800,
            heroId = 1,
            player = RemoteGuidePlayerResponse(
                position = RemoteMatchPlayerPosition.POSITION_1,
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
        val api = FakeGuidesApiClient(guides = Result.success(page(dto, gameVersionId = 174)))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, GuidesPageMapperImpl(), TestCoroutineDispatchers())

        val pageResult = repo.getGuides().getOrThrow()

        assertEquals(GameVersion(174), pageResult.gameVersion)
        assertEquals(1, pageResult.guides.size)
        val guide = pageResult.guides.single()
        assertEquals(HeroId(1), guide.heroId)
        assertEquals(MatchPlayerPosition.POSITION_1, guide.playerStats.position)
        val purchases = guide.playerStats.sortedEndItemPurchases
        assertEquals(2, purchases.size)
        assertEquals(ItemId(43), purchases[0].itemId)
        assertEquals(100, purchases[0].time)
        assertEquals(ItemId(42), purchases[1].itemId)
        assertEquals(200, purchases[1].time)
    }

    @Test
    fun `getGuides surfaces upstream failure unchanged`() = runTest {
        val boom = IllegalStateException("network")
        val api = FakeGuidesApiClient(guides = Result.failure(boom))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, GuidesPageMapperImpl(), TestCoroutineDispatchers())

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
            heroId = 5,
            player = RemoteGuidePlayerResponse(),
        )
        val repo: GuidesRepository = GuidesRepositoryImpl(
            FakeGuidesApiClient(guides = Result.success(page(dto, gameVersionId = 200))),
            GuidesPageMapperImpl(),
            TestCoroutineDispatchers(),
        )

        val pageResult = repo.getGuides().getOrThrow()
        val guide = pageResult.guides.single()
        val stats = guide.playerStats

        assertEquals(GameVersion(200), pageResult.gameVersion)
        assertEquals(HeroId(5), guide.heroId)
        assertEquals(0, guide.durationSeconds)
        assertNull(stats.position)
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
            heroId = 8,
            player = RemoteGuidePlayerResponse(position = RemoteMatchPlayerPosition.POSITION_1, isRadiant = false),
        )
        val api = FakeGuidesApiClient(
            guides = Result.success(page()),
            heroGuides = Result.success(page(heroDto, gameVersionId = 174)),
        )
        val repo: GuidesRepository = GuidesRepositoryImpl(api, GuidesPageMapperImpl(), TestCoroutineDispatchers())

        val pageResult = repo.getHeroGuides(heroId = HeroId(8)).getOrThrow()

        assertEquals(1, pageResult.guides.size)
        val guide = pageResult.guides.single()
        assertEquals(HeroId(8), guide.heroId)
        assertEquals(false, guide.playerStats.isRadiant)
    }
}
