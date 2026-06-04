package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.common.utils.TimeConverter
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.core.match.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiHero
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiHeroFilter
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiNeutralItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val IMPACT_PROGRESS_DIVIDER = 50f
private const val SLOT_COUNT = 6

internal interface UiGuidesStateMapper : Mapper<GuidesStore.State, UiGuidesState>

internal class UiGuidesStateMapperImpl : UiGuidesStateMapper {

    override fun map(item: GuidesStore.State): UiGuidesState {
        val visibleGuides = item.guides
            .mapNotNull { guide -> guide.toUi(heroes = item.heroes, items = item.items) }
            .toImmutableList()

        return UiGuidesState(
            guides = visibleGuides,
            heroFilter = item.filters.heroId?.let { id -> item.heroes[id]?.toUiHeroFilter() },
            selectedPosition = item.filters.position?.toUi(),
            selectedSide = item.filters.isRadiant,
            isSideFilterAvailable = item.filters.heroId == null,
            heroPicker = item.activePicker
                ?.takeIf { it == GuidesFilterKind.Hero }
                ?.let { buildHeroPicker(item) },
            isLoading = item.isLoading,
            isError = item.isError,
            isRefreshing = item.isRefreshing,
            isLoadingMore = item.isLoadingMore,
            isLoadMoreError = item.isLoadMoreError,
            canLoadMore = item.pagination.hasMore,
        )
    }

    private fun Guide.toUi(heroes: Map<HeroId, Hero>, items: Map<ItemId, Item>): UiGuide? {
        val hero = heroes[heroId]
        return UiGuide(
            matchId = matchId,
            steamAccountId = steamAccountId,
            hero = hero?.toUi() ?: return null,
            position = playerStats.position?.toUi(),
            isRadiant = playerStats.isRadiant,
            durationFormatted = TimeConverter.convertSecondsToMinutesAndSeconds(durationSeconds),
            kills = playerStats.kills.toInt(),
            deaths = playerStats.deaths.toInt(),
            assists = playerStats.assists.toInt(),
            impactLabel = playerStats.impact.toSignedLabel(),
            impactProgress = (playerStats.impact / IMPACT_PROGRESS_DIVIDER).coerceIn(0f, 1f),
            items = buildItemSlots(playerStats.sortedEndItemPurchases, items),
            neutralItem = playerStats.endNeutralItemId?.let { id ->
                items[id]?.iconUrl?.let(::UiNeutralItem)
            },
        )
    }

    private fun buildItemSlots(
        purchases: List<ItemPurchase>,
        items: Map<ItemId, Item>,
    ): ImmutableList<UiItemPurchase> =
        (0 until SLOT_COUNT).map { i ->
            val purchase = purchases.getOrNull(i)
            UiItemPurchase(
                iconUrl = purchase?.itemId?.let { items[it]?.iconUrl },
                timeFormatted = purchase?.time?.let { TimeConverter.convertSecondsToMinutesAndSeconds(it) }.orEmpty(),
            )
        }.toImmutableList()

    private fun buildHeroPicker(state: GuidesStore.State): UiFilterPicker.Hero {
        val query = state.pickerSearch.trim()
        val filtered = state.heroes.values
            .filter { query.isEmpty() || it.displayName.contains(query, ignoreCase = true) }
            .sortedBy { it.displayName }
            .map { hero -> hero.toUi() }
            .toImmutableList()
        return UiFilterPicker.Hero(
            search = state.pickerSearch,
            heroes = filtered,
            selectedHeroId = state.filters.heroId,
        )
    }

    private fun Hero.toUiHeroFilter(): UiHeroFilter = UiHeroFilter(
        heroId = id,
        displayName = displayName,
        iconUrl = iconUrl,
    )

    private fun Hero.toUi(): UiHero {
        return UiHero(
            id = id,
            displayName = displayName,
            iconUrl = iconUrl
        )
    }
}

internal fun MatchPlayerPosition.toUi(): UiMatchPlayerPosition = when (this) {
    MatchPlayerPosition.POSITION_1 -> UiMatchPlayerPosition.POSITION_1
    MatchPlayerPosition.POSITION_2 -> UiMatchPlayerPosition.POSITION_2
    MatchPlayerPosition.POSITION_3 -> UiMatchPlayerPosition.POSITION_3
    MatchPlayerPosition.POSITION_4 -> UiMatchPlayerPosition.POSITION_4
    MatchPlayerPosition.POSITION_5 -> UiMatchPlayerPosition.POSITION_5
}

internal fun UiMatchPlayerPosition.toDomain(): MatchPlayerPosition = when (this) {
    UiMatchPlayerPosition.POSITION_1 -> MatchPlayerPosition.POSITION_1
    UiMatchPlayerPosition.POSITION_2 -> MatchPlayerPosition.POSITION_2
    UiMatchPlayerPosition.POSITION_3 -> MatchPlayerPosition.POSITION_3
    UiMatchPlayerPosition.POSITION_4 -> MatchPlayerPosition.POSITION_4
    UiMatchPlayerPosition.POSITION_5 -> MatchPlayerPosition.POSITION_5
}

private fun Short.toSignedLabel(): String = when {
    this > 0 -> "+$this"
    this < 0 -> "$this"
    else -> "0"
}
