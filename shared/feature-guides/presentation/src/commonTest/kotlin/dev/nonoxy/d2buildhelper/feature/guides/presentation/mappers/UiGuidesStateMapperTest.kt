package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Pagination
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.PlayerStats
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class UiGuidesStateMapperTest {

    private val mapper = UiGuidesStateMapperImpl()

    private fun hero(id: Short) = Hero(
        id = HeroId(id),
        shortName = "H$id",
        displayName = "H$id",
        iconUrl = ImageUrl("u$id"),
    )

    private fun guide(
        id: Long,
        heroId: Short,
        position: MatchPlayerPosition?,
        isRadiant: Boolean,
    ) = Guide(
        matchId = id,
        steamAccountId = id,
        durationSeconds = 100,
        heroId = HeroId(heroId),
        playerStats = PlayerStats(
            position = position,
            isRadiant = isRadiant,
            kills = 0.toByte(),
            deaths = 0.toByte(),
            assists = 0.toByte(),
            impact = 0.toShort(),
            endNeutralItemId = null,
            sortedEndItemPurchases = emptyList(),
        ),
    )

    @Test
    fun `does not filter guides client-side - server already filtered`() {
        // Two guides: one radiant, one dire — with isRadiant=true filter set.
        // Old applyClientFilters would keep only the radiant one (1 guide).
        // New behaviour (server-side): both guides pass through (2 guides).
        val heroId: Short = 1
        val state = GuidesStore.State(
            guides = listOf(
                guide(1, heroId, null, isRadiant = true),
                guide(2, heroId, null, isRadiant = false),
            ),
            heroes = mapOf(HeroId(heroId) to hero(heroId)),
            filters = GuidesFilters(isRadiant = true),
            isLoading = false,
        )

        val ui = mapper.map(state)

        assertEquals(2, ui.guides.size)
    }

    @Test
    fun `maps pagination and process flags`() {
        val state = GuidesStore.State(
            guides = emptyList(),
            pagination = Pagination(page = 1, hasMore = true),
            isLoadingMore = true,
            isRefreshing = true,
            isLoadMoreError = true,
            isLoading = false,
        )

        val ui = mapper.map(state)

        assertEquals(true, ui.canLoadMore)
        assertEquals(true, ui.isLoadingMore)
        assertEquals(true, ui.isRefreshing)
        assertEquals(true, ui.isLoadMoreError)
    }

    @Test
    fun `side filter available when no hero selected`() {
        val state = GuidesStore.State(filters = GuidesFilters(heroId = null))
        assertTrue(mapper.map(state).isSideFilterAvailable)
    }

    @Test
    fun `side filter unavailable when a hero is selected`() {
        val state = GuidesStore.State(filters = GuidesFilters(heroId = HeroId(7)))
        assertFalse(mapper.map(state).isSideFilterAvailable)
    }
}
