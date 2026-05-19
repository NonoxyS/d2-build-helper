package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.GuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.HeroDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.ItemPurchaseDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.FakeGuidesApi
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesRepositoryTest {

    @Test
    fun `getGuides maps DTO to domain and orders end items by time with nulls last`() = runTest {
        val dto = GuideDto(
            hero = HeroDto(heroId = 1, shortName = "antimage", displayName = "Anti-Mage"),
            steamAccountId = 1L,
            matchId = 100L,
            durationSeconds = 1800,
            playerStats = PlayerStatsDto(
                position = MatchPlayerPositionType.POSITION_1,
                isRadiant = true,
                kills = 10.toByte(),
                deaths = 1.toByte(),
                assists = 5.toByte(),
                impact = 70.toShort(),
                endItem0Id = 42.toShort(),
                endItem1Id = 43.toShort(),
                endItem2Id = null,
                endItem3Id = null,
                endItem4Id = null,
                endItem5Id = null,
                endBackpack0Id = null,
                endBackpack1Id = null,
                endBackpack2Id = null,
                endNeutralItemId = null,
                itemPurchases = listOf(
                    ItemPurchaseDto(itemId = 42, time = 200),
                    ItemPurchaseDto(itemId = 43, time = 100),
                ),
            ),
        )
        val api = FakeGuidesApi(guides = Result.success(listOf(dto)))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides().getOrThrow()

        assertEquals(1, result.size)
        val guide = result.single()
        assertEquals("Anti-Mage", guide.hero.displayName)
        assertEquals(MatchPlayerPosition.POSITION_1, guide.playerStats.position)
        val purchases = guide.playerStats.sortedEndItemPurchases
        assertEquals(2, purchases.size)
        assertEquals(100, purchases[0].time)
        assertEquals(200, purchases[1].time)
    }

    @Test
    fun `getGuides surfaces upstream failure unchanged`() = runTest {
        val boom = IllegalStateException("network")
        val api = FakeGuidesApi(guides = Result.failure(boom))
        val repo: GuidesRepository = GuidesRepositoryImpl(api, TestCoroutineDispatchers())

        val result = repo.getGuides()

        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
    }

    @Test
    fun `getGuides applies fallbacks for nullable DTO fields`() = runTest {
        val dto = GuideDto(
            hero = HeroDto(heroId = 5, shortName = "drow", displayName = "Drow Ranger"),
            steamAccountId = 99L,
            matchId = 200L,
            durationSeconds = 1500,
            playerStats = PlayerStatsDto(
                position = null,
                isRadiant = null,
                kills = 0.toByte(),
                deaths = 0.toByte(),
                assists = 0.toByte(),
                impact = null,
                endItem0Id = null,
                endItem1Id = null,
                endItem2Id = null,
                endItem3Id = null,
                endItem4Id = null,
                endItem5Id = null,
                endBackpack0Id = null,
                endBackpack1Id = null,
                endBackpack2Id = null,
                endNeutralItemId = null,
                itemPurchases = null,
            ),
        )
        val repo: GuidesRepository = GuidesRepositoryImpl(
            FakeGuidesApi(guides = Result.success(listOf(dto))),
            TestCoroutineDispatchers(),
        )

        val stats = repo.getGuides().getOrThrow().single().playerStats

        assertEquals(MatchPlayerPosition.UNKNOWN, stats.position)
        assertEquals(true, stats.isRadiant)
        assertEquals(25.toShort(), stats.impact)
        assertNull(stats.endNeutralItemId)
        assertTrue(stats.sortedEndItemPurchases.isEmpty())
    }
}
