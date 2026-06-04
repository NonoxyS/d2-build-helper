package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.common.ui.match.toUi
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.AbilityLearnEvent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.BuildPlayer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.InventorySnapshot
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.LineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiAbilitySummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiBuildHeader
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailState
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiInventorySnapshot
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiInventoryTimeline
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildEntry
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineup
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrix
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixRow
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillSummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * RISK (spec §6.5 — smoke-verify-needed): the generic `attribute_bonus` ("+2 stats")
 * ability id. Best-guess from the well-known Dota/Stratz constants. Verify against
 * real `/v1/constants` data on first device smoke; if wrong, the "+" stats matrix row
 * will be empty and stat picks would leak into the skill rows.
 */
internal const val STAT_ABILITY_ID_RAW: Short = 730

/**
 * RISK (spec §6.5 / §3.2 — smoke-verify-needed): `item_ultimate_scepter`
 * (Aghanim's Scepter) item id. Best-guess from the well-known Dota/Stratz constants.
 * Scepter has no dedicated flag — it is derived from purchases/final items.
 * Verify against real constants; if wrong, the summary scepter chip stays grey.
 */
internal const val ULTIMATE_SCEPTER_ITEM_ID_RAW: Short = 108

private const val MAX_LEVEL = UiSkillMatrix.LEVEL_COLUMNS
private const val MAX_ABILITY_ROWS = 4
private const val SECONDS_PER_MINUTE = 60
private const val LAST_HITS_AT_MINUTE = 10
private val TALENT_TIERS = listOf(10, 15, 20, 25)

internal interface UiGuideDetailStateMapper : Mapper<GuideDetailStore.State, UiGuideDetailState>

internal class UiGuideDetailStateMapperImpl : UiGuideDetailStateMapper {

    override fun map(item: GuideDetailStore.State): UiGuideDetailState {
        val detail = item.detail
            ?: return UiGuideDetailState(isLoading = item.isLoading, isError = item.isError)

        val player = detail.player
        return UiGuideDetailState(
            header = buildHeader(detail, player, item.heroes),
            skillBuild = buildSkillBuild(player, item.abilities),
            itemBuild = buildItemBuild(player, item.items),
            inventory = buildInventory(player, item.items),
            networth = buildNetworth(player),
            lineup = buildLineup(detail, player, item.heroes),
            isLoading = item.isLoading,
            isError = item.isError,
        )
    }

    private fun buildHeader(
        detail: GuideDetail,
        player: BuildPlayer,
        heroes: Map<HeroId, Hero>,
    ): UiBuildHeader {
        val hero = heroes[player.heroId]
        return UiBuildHeader(
            heroIconUrl = hero?.iconUrl,
            heroName = hero?.displayName.orEmpty(),
            isRadiant = player.isRadiant,
            position = player.position?.toUi(),
            role = player.role,
            lane = player.lane,
            durationText = formatTime(detail.durationSeconds),
            level = player.level,
            isVictory = player.isVictory,
            kills = player.kills,
            deaths = player.deaths,
            assists = player.assists,
            impact = player.impact,
        )
    }

    private fun buildSkillBuild(
        player: BuildPlayer,
        abilities: Map<AbilityId, Ability>,
    ): UiSkillBuild? {
        val events = player.abilityLearnEvents
        if (events.isEmpty()) return null

        val talentEvents = events.filter { it.isTalent }
        val statEvents = events.filter { !it.isTalent && it.abilityId?.raw == STAT_ABILITY_ID_RAW }
        val skillEvents = events.filter { !it.isTalent && it.abilityId?.raw != STAT_ABILITY_ID_RAW }

        // Distinct ability ids in learn order → up to 4 ability rows (Q/W/E/R).
        val orderedAbilityIds = skillEvents
            .mapNotNull { it.abilityId }
            .distinct()
            .take(MAX_ABILITY_ROWS)

        val abilityRows = orderedAbilityIds.map { abilityId ->
            UiSkillMatrixRow(
                iconUrl = abilities[abilityId]?.iconUrl,
                name = abilities[abilityId]?.name,
                isStat = false,
                isUltimate = skillEvents.isUltimate(abilityId),
                marks = levelMarks(skillEvents.filter { it.abilityId == abilityId }),
            )
        }
        val statsRow = UiSkillMatrixRow(
            iconUrl = null,
            // Stats row uses the fixed "+" label rendered in the UI (no constants name).
            name = null,
            isStat = true,
            isUltimate = false,
            marks = levelMarks(statEvents),
        )

        val summary = UiSkillSummary(
            talentTierTaken = TALENT_TIERS.map { tier ->
                talentEvents.any { it.level == tier }
            }.toImmutableList(),
            abilities = orderedAbilityIds.map { abilityId ->
                UiAbilitySummary(
                    iconUrl = abilities[abilityId]?.iconUrl,
                    name = abilities[abilityId]?.name,
                    pointCount = skillEvents.count { it.abilityId == abilityId },
                    isUltimate = skillEvents.isUltimate(abilityId),
                )
            }.toImmutableList(),
            scepterPurchased = hasScepter(player),
        )

        return UiSkillBuild(
            summary = summary,
            matrix = UiSkillMatrix(rows = (abilityRows + statsRow).toImmutableList()),
            talents = talentEvents
                .mapNotNull { event -> event.toTalent(abilities) }
                .toImmutableList(),
        )
    }

    /** `true` if any learn event for [abilityId] is flagged as the ultimate (spec §2). */
    private fun List<AbilityLearnEvent>.isUltimate(abilityId: AbilityId): Boolean =
        any { it.abilityId == abilityId && it.isUltimate }

    private fun AbilityLearnEvent.toTalent(abilities: Map<AbilityId, Ability>): UiTalent? {
        // Talents are only learnable at 10/15/20/25, so match the exact tier —
        // same rule as `talentTierTaken` (no nearest-tier bucketing divergence).
        val level = level ?: return null
        val tier = TALENT_TIERS.firstOrNull { it == level } ?: return null
        return UiTalent(
            level = tier,
            text = abilityId?.let { abilities[it]?.name }.orEmpty(),
        )
    }

    /** A [MAX_LEVEL]-wide boolean mark list: `true` where this ability/stat was leveled. */
    private fun levelMarks(events: List<AbilityLearnEvent>): ImmutableList<Boolean> {
        val levels = events.mapNotNull { it.level }.toSet()
        return (1..MAX_LEVEL).map { it in levels }.toImmutableList()
    }

    private fun hasScepter(player: BuildPlayer): Boolean {
        val inFinal = player.finalItemIds.any { it.raw == ULTIMATE_SCEPTER_ITEM_ID_RAW }
        val inPurchases = player.itemPurchases.any { it.itemId.raw == ULTIMATE_SCEPTER_ITEM_ID_RAW }
        return inFinal || inPurchases
    }

    private fun buildItemBuild(player: BuildPlayer, items: Map<ItemId, Item>): UiItemBuild {
        val neutral = player.neutralItemId?.let { id ->
            UiItemBuildEntry(iconUrl = items[id]?.iconUrl, name = items[id]?.displayName, timeText = "")
        }
        val purchases = player.itemPurchases
            .sortedBy { it.time ?: Int.MAX_VALUE }
            .map { purchase -> purchase.toEntry(items) }
            .toImmutableList()
        return UiItemBuild(neutralItem = neutral, purchases = purchases)
    }

    private fun ItemPurchase.toEntry(items: Map<ItemId, Item>): UiItemBuildEntry =
        UiItemBuildEntry(
            iconUrl = items[itemId]?.iconUrl,
            name = items[itemId]?.displayName,
            timeText = formatTime(time),
        )

    private fun buildInventory(player: BuildPlayer, items: Map<ItemId, Item>): UiInventoryTimeline? {
        if (player.inventorySnapshots.isEmpty()) return null
        return UiInventoryTimeline(
            snapshots = player.inventorySnapshots
                .map { snapshot -> snapshot.toUi(items) }
                .toImmutableList(),
        )
    }

    private fun InventorySnapshot.toUi(items: Map<ItemId, Item>): UiInventorySnapshot =
        UiInventorySnapshot(
            itemIconUrls = itemIds.map { it?.let { id -> items[id]?.iconUrl } }.toImmutableList(),
            backpackIconUrls = backpackIds.map { it?.let { id -> items[id]?.iconUrl } }.toImmutableList(),
            neutralIconUrl = neutralId?.let { items[it]?.iconUrl },
        )

    private fun buildNetworth(player: BuildPlayer): UiNetworthCurve {
        // v1: no significance flag — mark each purchase's minute (time / 60).
        val markerMinutes = player.itemPurchases
            .mapNotNull { it.time }
            .map { it / SECONDS_PER_MINUTE }
            .distinct()
            .sorted()
            .toImmutableList()
        return UiNetworthCurve(
            points = player.networthPerMinute.toImmutableList(),
            purchaseMarkerMinutes = markerMinutes,
            gpm = player.goldPerMinute,
            networth = player.networth,
            lastHitsAt10 = player.lastHitsPerMinute.getOrNull(LAST_HITS_AT_MINUTE),
        )
    }

    private fun buildLineup(
        detail: GuideDetail,
        player: BuildPlayer,
        heroes: Map<HeroId, Hero>,
    ): UiLineup {
        val (allies, enemies) = detail.lineup.partition { it.isRadiant == player.isRadiant }
        return UiLineup(
            allies = allies.toUiMembers(detail.steamAccountId, heroes),
            enemies = enemies.toUiMembers(detail.steamAccountId, heroes),
        )
    }

    private fun List<LineupMember>.toUiMembers(
        mySteamAccountId: Long,
        heroes: Map<HeroId, Hero>,
    ): ImmutableList<UiLineupMember> =
        // sort by position 1..5, nulls last
        sortedBy { it.position?.ordinal ?: Int.MAX_VALUE }
            .map { member ->
                UiLineupMember(
                    heroIconUrl = member.heroId?.let { heroes[it]?.iconUrl },
                    isMe = member.steamAccountId == mySteamAccountId,
                    role = member.role,
                )
            }
            .toImmutableList()
}

/**
 * MM:SS, pre-horn negative times formatted as `-M:SS` (spec §2.3).
 * `TimeConverter` collapses negatives to "00:00", so format here.
 */
private fun formatTime(seconds: Int?): String {
    if (seconds == null) return ""
    val sign = if (seconds < 0) "-" else ""
    val abs = kotlin.math.abs(seconds)
    val minutes = abs / 60
    val secs = abs % 60
    return "$sign$minutes:${secs.toString().padStart(2, '0')}"
}
