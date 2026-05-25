package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.FilterValue
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesPage
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.FakeResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesExecutorTest {

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

    private fun guide(heroId: Short) = Guide(
        matchId = heroId.toLong(),
        steamAccountId = 1L,
        durationSeconds = 100,
        heroId = HeroId(heroId),
        playerStats = PlayerStats(
            position = MatchPlayerPosition.POSITION_1,
            isRadiant = true,
            kills = 0.toByte(),
            deaths = 0.toByte(),
            assists = 0.toByte(),
            impact = 0.toShort(),
            endNeutralItemId = null,
            sortedEndItemPurchases = emptyList(),
        ),
    )

    private fun guidesPage(versionId: Int, heroIds: List<Short>) = GuidesPage(
        gameVersion = GameVersion(versionId),
        guides = heroIds.map { guide(it) },
    )

    @Test
    fun `LoadInitial happy path with matching versions does not request refresh`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(guidesPage(174, listOf(1)))
            override suspend fun getHeroGuides(heroId: HeroId) =
                Result.success(guidesPage(174, listOf(heroId.raw)))
        }

        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        val state = store.state
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(1, state.guides.size)
        assertEquals(2, state.heroes.size)
        assertEquals(GameVersion(174), state.gameVersion)
        assertNull(resources.lastExpectedVersion)

        store.dispose()
    }

    @Test
    fun `LoadInitial mismatch triggers refresh with guides gameVersion`() = runTest {
        val resources = FakeResourcesRepository(
            constants = constants(174),
            refreshResult = Result.success(constants(175)),
        )
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(guidesPage(175, listOf(1)))
            override suspend fun getHeroGuides(heroId: HeroId) =
                Result.success(guidesPage(175, listOf(heroId.raw)))
        }

        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        assertEquals(GameVersion(175), resources.lastExpectedVersion)
        assertEquals(GameVersion(175), store.state.gameVersion)

        store.dispose()
    }

    @Test
    fun `LoadInitial failure sets error and clears loading`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides(): Result<GuidesPage> =
                Result.failure(RuntimeException("boom"))
            override suspend fun getHeroGuides(heroId: HeroId): Result<GuidesPage> =
                Result.success(guidesPage(174, emptyList()))
        }
        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isError)

        store.dispose()
    }

    @Test
    fun `OnFilterChipClick sets activePicker in state`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(guidesPage(174, listOf(1)))
            override suspend fun getHeroGuides(heroId: HeroId) =
                Result.success(guidesPage(174, listOf(heroId.raw)))
        }
        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        store.accept(Intent.OnFilterChipClick(GuidesFilterKind.Hero))

        assertEquals(GuidesFilterKind.Hero, store.state.activePicker)

        store.dispose()
    }

    @Test
    fun `OnFilterApply hero invokes FilterByHero action`() = runTest {
        val resources = FakeResourcesRepository(constants(174))
        var observed: HeroId? = null
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(guidesPage(174, listOf(1)))
            override suspend fun getHeroGuides(heroId: HeroId): Result<GuidesPage> {
                observed = heroId
                return Result.success(guidesPage(174, listOf(heroId.raw)))
            }
        }
        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        store.accept(Intent.OnFilterApply(FilterValue.Hero(HeroId(7))))

        assertEquals(HeroId(7), observed)
        assertEquals(HeroId(7), store.state.filters.heroId)
        assertNull(store.state.activePicker)

        store.dispose()
    }
}
