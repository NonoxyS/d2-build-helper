package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.common.utils.TimeConverter
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterChip
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiHero
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiNeutralItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val IMPACT_PROGRESS_DIVIDER = 50f
private const val SLOT_COUNT = 6

internal interface UiGuidesStateMapper : Mapper<GuidesStore.State, UiGuidesState>

internal class UiGuidesStateMapperImpl : UiGuidesStateMapper {

    override fun map(item: GuidesStore.State): UiGuidesState {
        val visibleGuides = item.guides
            .applyClientFilters(item.filters.position, item.filters.isRadiant)
            .mapNotNull { guide -> guide.toUi(heroes = item.heroes, items = item.items) }
            .toImmutableList()

        return UiGuidesState(
            guides = visibleGuides,
            filterChips = buildChips(item),
            activePicker = item.activePicker?.let { kind -> buildPicker(kind, item) },
            isLoading = item.isLoading,
            isError = item.isError,
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

    private fun List<Guide>.applyClientFilters(
        position: MatchPlayerPosition?,
        isRadiant: Boolean?,
    ): List<Guide> = filter { guide ->
        (position == null || guide.playerStats.position == position) &&
            (isRadiant == null || guide.playerStats.isRadiant == isRadiant)
    }

    private fun buildChips(state: GuidesStore.State): ImmutableList<UiFilterChip> = persistentListOf(
        UiFilterChip.Hero(appliedHeroName = state.filters.heroId?.let { state.heroes[it]?.displayName }),
        UiFilterChip.Position(appliedPosition = state.filters.position?.toUi()),
        UiFilterChip.Side(appliedIsRadiant = state.filters.isRadiant),
    )

    private fun buildPicker(kind: GuidesFilterKind, state: GuidesStore.State): UiFilterPicker = when (kind) {
        GuidesFilterKind.Hero -> {
            val query = state.pickerSearch.trim()
            val filtered = state.heroes.values
                .filter { query.isEmpty() || it.displayName.contains(query, ignoreCase = true) }
                .sortedBy { it.displayName }
                .map { hero -> hero.toUi() }
                .toImmutableList()
            UiFilterPicker.Hero(search = state.pickerSearch, heroes = filtered, selectedHeroId = state.filters.heroId)
        }

        GuidesFilterKind.Position -> UiFilterPicker.Position(
            options = persistentListOf(
                UiMatchPlayerPosition.POSITION_1,
                UiMatchPlayerPosition.POSITION_2,
                UiMatchPlayerPosition.POSITION_3,
                UiMatchPlayerPosition.POSITION_4,
                UiMatchPlayerPosition.POSITION_5,
            ),
            selected = state.filters.position?.toUi(),
        )

        GuidesFilterKind.Side -> UiFilterPicker.Side(selected = state.filters.isRadiant)
    }

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
