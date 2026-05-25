package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.FakeResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesExecutorTest {

    private fun hero(id: Short, name: String = "Hero$id") =
        Hero(id = id, shortName = name, displayName = name)

    private fun item(id: Short) = Item(id = id, shortName = "item_$id", displayName = "Item $id")

    private fun guide(heroId: Short) = Guide(
        hero = hero(heroId),
        steamAccountId = 1L,
        matchId = heroId.toLong(),
        durationSeconds = 100,
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

    @Test
    fun `LoadInitial success leaves state loaded with guides and sorted heroes`() = runTest {
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(listOf(guide(1)))
            override suspend fun getHeroGuides(heroId: Short) = Result.success(listOf(guide(heroId)))
        }
        val resources = FakeResourcesRepository(
            heroImages = Result.success(
                mapOf(
                    hero(1, "Anti-Mage") to "url_a",
                    hero(2, "Bristleback") to "url_b",
                ),
            ),
            itemImages = Result.success(mapOf(item(10) to "url_10")),
        )
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
        assertEquals(2, state.heroSearchFiltered.size)
        assertEquals(
            listOf("Anti-Mage", "Bristleback"),
            state.heroSearchFiltered.keys.map { it.displayName },
        )

        store.dispose()
    }

    @Test
    fun `LoadInitial failure sets error and clears loading`() = runTest {
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides(): Result<List<Guide>> =
                Result.failure(RuntimeException("boom"))

            override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> =
                Result.success(emptyList())
        }
        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = FakeResourcesRepository(),
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        val state = store.state

        assertFalse(state.isLoading)
        assertTrue(state.isError)

        store.dispose()
    }

    @Test
    fun `OnHeroSelect replaces guides with per-hero result`() = runTest {
        var observedHeroId: Short? = null
        val guidesRepo = object : GuidesRepository {
            override suspend fun getGuides() = Result.success(listOf(guide(1)))
            override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> {
                observedHeroId = heroId
                return Result.success(listOf(guide(heroId)))
            }
        }
        val resources = FakeResourcesRepository(
            heroImages = Result.success(mapOf(hero(1, "A") to "url")),
            itemImages = Result.success(emptyMap()),
        )
        val store = GuidesStoreFactory(
            storeFactory = DefaultStoreFactory(),
            guidesRepository = guidesRepo,
            resourcesRepository = resources,
            dispatchers = TestCoroutineDispatchers(),
        ).create()

        store.accept(Intent.OnHeroSelect(7))

        assertEquals(7.toShort(), observedHeroId)
        assertEquals(7.toShort(), store.state.guides.single().hero.id)

        store.dispose()
    }
}
