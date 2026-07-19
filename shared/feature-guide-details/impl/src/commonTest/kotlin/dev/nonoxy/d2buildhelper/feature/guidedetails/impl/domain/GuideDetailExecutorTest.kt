package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.BuildPlayer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Intent
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.FakeResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GuideDetailExecutorTest {

    private fun hero(id: Short, name: String = "Hero$id") = Hero(
        id = HeroId(id),
        shortName = name,
        displayName = name,
        iconUrl = ImageUrl("https://img/$id"),
    )

    private fun constants(
        gameVersionId: Int = 174,
        heroes: List<Hero> = listOf(hero(1, "Anti-Mage"), hero(2, "Bristleback")),
    ) = DotaConstants(
        gameVersion = GameVersion(gameVersionId),
        heroes = heroes.associateBy { it.id },
        items = emptyMap(),
        abilities = emptyMap(),
    )

    private fun guideDetail(matchId: Long = 100L, steamAccountId: Long = 1L) = GuideDetail(
        matchId = matchId,
        steamAccountId = steamAccountId,
        didRadiantWin = true,
        durationSeconds = 1834,
        averageRank = 80,
        player = BuildPlayer(
            heroId = HeroId(1),
            isRadiant = true,
            isVictory = true,
            position = MatchPlayerPosition.POSITION_1,
            role = null,
            lane = null,
            level = null,
            kills = null,
            deaths = null,
            assists = null,
            impact = null,
            goldPerMinute = null,
            networth = null,
            experiencePerMinute = null,
            finalItemIds = emptyList(),
            backpackItemIds = emptyList(),
            neutralItemId = null,
            abilityLearnEvents = emptyList(),
            itemPurchases = emptyList(),
            networthPerMinute = emptyList(),
            lastHitsPerMinute = emptyList(),
            goldPerMinuteSeries = emptyList(),
        ),
        lineup = emptyList(),
    )

    @Test
    fun `LoadInitial happy path populates detail and constants and clears loading`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val repo = object : GuideDetailRepository {
            override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long) =
                Result.success(guideDetail(matchId, steamAccountId))
        }

        val store = GuideDetailStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guideDetailRepository = repo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create(matchId = 7891234567L, steamAccountId = 123456789L)

        val state = store.state
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(7891234567L, state.detail?.matchId)
        assertEquals(123456789L, state.detail?.steamAccountId)
        assertEquals(2, state.heroes.size)

        store.dispose()
    }

    @Test
    fun `LoadInitial forwards matchId and steamAccountId to the repository`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        var observedMatchId = -1L
        var observedSteamAccountId = -1L
        val repo = object : GuideDetailRepository {
            override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<GuideDetail> {
                observedMatchId = matchId
                observedSteamAccountId = steamAccountId
                return Result.success(guideDetail(matchId, steamAccountId))
            }
        }

        val store = GuideDetailStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guideDetailRepository = repo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create(matchId = 42L, steamAccountId = 99L)

        assertEquals(42L, observedMatchId)
        assertEquals(99L, observedSteamAccountId)

        store.dispose()
    }

    @Test
    fun `LoadInitial detail failure sets error and clears loading`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val repo = object : GuideDetailRepository {
            override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<GuideDetail> =
                Result.failure(RuntimeException("boom"))
        }

        val store = GuideDetailStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guideDetailRepository = repo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create(matchId = 1L, steamAccountId = 2L)

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isError)
        assertNull(state.detail)

        store.dispose()
    }

    @Test
    fun `LoadInitial constants failure sets error and clears loading`() = runTest {
        val resources = FakeResourcesRepository(
            constants = constants(174),
            getResult = Result.failure(RuntimeException("constants boom")),
        )
        val repo = object : GuideDetailRepository {
            override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long) =
                Result.success(guideDetail(matchId, steamAccountId))
        }

        val store = GuideDetailStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guideDetailRepository = repo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create(matchId = 1L, steamAccountId = 2L)

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isError)

        store.dispose()
    }

    @Test
    fun `OnRetry refetches and recovers from a previous error`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val repo = object : GuideDetailRepository {
            var first = true
            override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<GuideDetail> {
                if (first) {
                    first = false
                    return Result.failure(RuntimeException("boom"))
                }
                return Result.success(guideDetail(matchId, steamAccountId))
            }
        }

        val store = GuideDetailStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guideDetailRepository = repo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create(matchId = 100L, steamAccountId = 1L)

        assertTrue(store.state.isError)

        store.accept(Intent.OnRetry)

        assertFalse(store.state.isError)
        assertFalse(store.state.isLoading)
        assertEquals(100L, store.state.detail?.matchId)

        store.dispose()
    }
}
