package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.common.utils.TimeConverter
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
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

interface UiGuidesStateMapper : Mapper<GuidesStore.State, UiGuidesState>

class UiGuidesStateMapperImpl(
    private val positionMapper: UiMatchPlayerPositionMapper,
) : UiGuidesStateMapper {

    override fun map(item: GuidesStore.State): UiGuidesState {
        val visibleGuides = item.guides
            .applyClientFilters(item.filters.position, item.filters.isRadiant)
            .map { it.toUi(item.heroes, item.items) }
            .toImmutableList()

        val chips = buildChips(item)
        val picker = item.activePicker?.let { kind -> buildPicker(kind, item) }

        return UiGuidesState(
            guides = visibleGuides,
            filterChips = chips,
            activePicker = picker,
            isLoading = item.isLoading,
            isError = item.isError,
        )
    }

    private fun Guide.toUi(heroes: Map<HeroId, Hero>, items: Map<ItemId, Item>): UiGuide {
        val hero = heroes[heroId]
        return UiGuide(
            matchId = matchId,
            steamAccountId = steamAccountId,
            hero = hero?.let { UiHero(it.id, it.displayName, it.iconUrl) } ?: UiHero.Unknown,
            position = positionMapper.map(playerStats.position),
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

    private fun buildChips(state: GuidesStore.State): ImmutableList<UiFilterChip> {
        val heroLabel = state.filters.heroId
            ?.let { id -> state.heroes[id]?.displayName?.let { "Hero: $it" } }
            ?: "Hero"
        val positionLabel = state.filters.position?.let { "Position: ${it.name}" } ?: "Position"
        val sideLabel = state.filters.isRadiant?.let { if (it) "Side: Radiant" else "Side: Dire" } ?: "Side"

        return persistentListOf(
            UiFilterChip(GuidesFilterKind.Hero, heroLabel, isApplied = state.filters.heroId != null),
            UiFilterChip(GuidesFilterKind.Position, positionLabel, isApplied = state.filters.position != null),
            UiFilterChip(GuidesFilterKind.Side, sideLabel, isApplied = state.filters.isRadiant != null),
        )
    }

    private fun buildPicker(kind: GuidesFilterKind, state: GuidesStore.State): UiFilterPicker = when (kind) {
        GuidesFilterKind.Hero -> {
            val query = state.pickerSearch.trim()
            val filtered = state.heroes.values
                .filter { query.isEmpty() || it.displayName.contains(query, ignoreCase = true) }
                .sortedBy { it.displayName }
                .map { UiHero(it.id, it.displayName, it.iconUrl) }
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
            selected = state.filters.position?.let(positionMapper::map),
        )
        GuidesFilterKind.Side -> UiFilterPicker.Side(selected = state.filters.isRadiant)
    }
}

private fun Short.toSignedLabel(): String = when {
    this > 0 -> "+$this"
    this < 0 -> "$this"
    else -> "0"
}
