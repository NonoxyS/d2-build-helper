package dev.nonoxy.d2buildhelper.usecases

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.data.FakeGuidesDataSource
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ItemPurchase
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStats
import dev.nonoxy.d2buildhelper.features.guides.domain.usecases.GetGuidesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetGuidesUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val useCase = GetGuidesUseCase(FakeGuidesDataSource())

    @Test
    fun testGetGuides() = testScope.runTest {
        val result = useCase.getGuides().first()
        assertTrue(result is RequestResult.Success)
        assertEquals(mockGuidesUI.size, result.data.size)
        for (guide in result.data) {
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].hero.heroId, guide.hero.heroId)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].hero.shortName, guide.hero.shortName)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].hero.displayName, guide.hero.displayName)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].matchId, guide.matchId)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].steamAccountId, guide.steamAccountId)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].durationSeconds, guide.durationSeconds)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.kills, guide.playerStats.kills)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.deaths, guide.playerStats.deaths)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.assists, guide.playerStats.assists)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.impact, guide.playerStats.impact)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.isRadiant, guide.playerStats.isRadiant)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.position, guide.playerStats.position)
            assertEquals(mockGuidesUI[result.data.indexOf(guide)].playerStats.endNeutralItemId, guide.playerStats.endNeutralItemId)
            for (i in 0 until result.data.size) {
                val expectedItemPurchase = mockGuidesUI[result.data.indexOf(guide)].playerStats.sortedEndItemPurchases[i]
                val actualItemPurchase = guide.playerStats.sortedEndItemPurchases[i]
                assertEquals(expectedItemPurchase.itemId, actualItemPurchase.itemId)
                assertEquals(expectedItemPurchase.time, actualItemPurchase.time)
            }
        }
    }
}

private val mockGuidesUI = listOf(
    Guide(
        hero = Hero(heroId = 11, shortName = "nevermore", displayName = "Shadow Fiend"),
        steamAccountId = 299419908,
        matchId = 7837192981,
        durationSeconds = 2827,
        playerStats = PlayerStats(
            position = MatchPlayerPosition.POSITION_2,
            isRadiant = false,
            kills = 17,
            deaths = 9,
            assists = 18,
            impact = 32,
            endNeutralItemId = 362,
            sortedEndItemPurchases = listOf(ItemPurchase(itemId = 277, time = 873), ItemPurchase(
                itemId = 1,
                time = 1040
            ), ItemPurchase(itemId = 116, time = 1347), ItemPurchase(
                itemId = 123,
                time = 1651
            ), ItemPurchase(itemId = 110, time = 2483), ItemPurchase(itemId = 48, time = null))
        )
    ),
    Guide(
        hero = Hero(heroId = 19, shortName = "tiny", displayName = "Tiny"),
        steamAccountId = 420369951,
        matchId = 7837206286,
        durationSeconds = 2210,
        playerStats = PlayerStats(
            position = MatchPlayerPosition.POSITION_2,
            isRadiant = false,
            kills = 13,
            deaths = 4,
            assists = 16,
            impact = 21,
            endNeutralItemId = 1159,
            sortedEndItemPurchases = listOf(ItemPurchase(
                itemId = 36,
                time = 175
            ), ItemPurchase(itemId = 50, time = 442), ItemPurchase(
                itemId = 1,
                time = 731
            ), ItemPurchase(itemId = 116, time = 1500), ItemPurchase(
                itemId = 1808,
                time = 1757
            ), ItemPurchase(itemId = 152, time = 2197))
        )
    ),
)