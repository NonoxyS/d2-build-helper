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
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.LineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.TalentSide
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiAbilitySummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiBuildHeader
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailState
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildEntry
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildPhase
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildSection
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineup
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthMarker
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrix
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixBottomCell
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixRow
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillSummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalentTier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

// 730 = special_bonus_attributes (generic +stats talent), verified vs live Stratz
internal const val STAT_ABILITY_ID_RAW: Short = 730

// 108 = item_ultimate_scepter; no dedicated flag in Stratz — derived from purchases/final items
internal const val ULTIMATE_SCEPTER_ITEM_ID_RAW: Short = 108

private const val MAX_LEVEL = UiSkillMatrix.LEVEL_COLUMNS
private const val MAX_ABILITY_ROWS = 4
private const val EARLY_LEVELS = 6
private const val SECONDS_PER_MINUTE = 60
private val TALENT_TIERS = listOf(10, 15, 20, 25)

private const val LANING_END_SECONDS = 600

private const val MID_GAME_END_SECONDS = 1500
private const val CONSUMABLE_QUALITY_PREFIX = "consumable"
private const val MAX_NETWORTH_MARKERS = 8

internal interface UiGuideDetailStateMapper : Mapper<GuideDetailStore.State, UiGuideDetailState>

internal class UiGuideDetailStateMapperImpl : UiGuideDetailStateMapper {

    override fun map(item: GuideDetailStore.State): UiGuideDetailState {
        val detail = item.detail
            ?: return UiGuideDetailState(isLoading = item.isLoading, isError = item.isError)

        val player = detail.player
        return UiGuideDetailState(
            header = buildHeader(detail, player, item.heroes),
            skillBuild = buildSkillBuild(player, item.abilities, item.heroes[player.heroId]),
            itemBuild = buildItemBuild(player, item.items),
            networth = buildNetworth(player, item.items),
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
            role = player.role?.toUi(),
            lane = player.lane?.toUi(),
            durationText = formatTime(detail.durationSeconds),
            level = player.level,
            isVictory = player.isVictory,
            kills = player.kills,
            deaths = player.deaths,
            assists = player.assists,
        )
    }

    private fun buildSkillBuild(
        player: BuildPlayer,
        abilities: Map<AbilityId, Ability>,
        hero: Hero?,
    ): UiSkillBuild? {
        val events = player.abilityLearnEvents
        if (events.isEmpty()) return null

        val slotByAbility = hero?.talents.orEmpty().associate { it.abilityId to it.slot }
        val talentEvents = events.filter { it.isTalent }
        val statEvents = events.filter { !it.isTalent && it.abilityId?.raw == STAT_ABILITY_ID_RAW }
        val skillEvents = events.filter { !it.isTalent && it.abilityId?.raw != STAT_ABILITY_ID_RAW }

        val orderedAbilityIds = skillEvents
            .mapNotNull { it.abilityId }
            .distinct()
            .take(MAX_ABILITY_ROWS)

        val abilityRows = orderedAbilityIds.map { abilityId ->
            UiSkillMatrixRow(
                iconUrl = abilities[abilityId]?.iconUrl,
                name = abilities[abilityId]?.label,
                isUltimate = skillEvents.isUltimate(abilityId),
                marks = levelMarks(skillEvents.filter { it.abilityId == abilityId }),
            )
        }

        val statLevels = statEvents.mapNotNull { it.level }.toSet()
        val talentEventByLevel = talentEvents
            .mapNotNull { event -> event.level?.let { it to event } }
            .toMap()
        val bottomCells = (1..MAX_LEVEL).map { level ->
            when {
                TALENT_TIERS.contains(level) && talentEventByLevel.containsKey(level) -> {
                    val event = talentEventByLevel.getValue(level)
                    UiSkillMatrixBottomCell.Talent(
                        tier = level,
                        side = talentSide(event, slotByAbility),
                        text = talentText(event, abilities),
                    )
                }
                statLevels.contains(level) -> UiSkillMatrixBottomCell.Stat
                else -> UiSkillMatrixBottomCell.Empty
            }
        }.toImmutableList()

        val summary = UiSkillSummary(
            talentTiers = TALENT_TIERS.map { tier ->
                val event = talentEvents.firstOrNull { it.level == tier }
                UiTalentTier(tier = tier, taken = event != null, side = event?.let { talentSide(it, slotByAbility) })
            }.toImmutableList(),
            abilities = orderedAbilityIds.map { abilityId ->
                UiAbilitySummary(
                    iconUrl = abilities[abilityId]?.iconUrl,
                    name = abilities[abilityId]?.label,
                    pointCount = skillEvents.count { it.abilityId == abilityId },
                    earlyPointCount = skillEvents.count {
                        it.abilityId == abilityId && (it.level ?: 0) in 1..EARLY_LEVELS
                    },
                    isUltimate = skillEvents.isUltimate(abilityId),
                )
            }.toImmutableList(),
            scepterPurchased = hasScepter(player),
        )

        return UiSkillBuild(
            summary = summary,
            matrix = UiSkillMatrix(abilityRows = abilityRows.toImmutableList(), bottomCells = bottomCells),
        )
    }

    private val Ability.label: String get() = displayName.ifEmpty { name }

    private fun List<AbilityLearnEvent>.isUltimate(abilityId: AbilityId): Boolean =
        any { it.abilityId == abilityId && it.isUltimate }

    private fun talentText(event: AbilityLearnEvent, abilities: Map<AbilityId, Ability>): String =
        event.abilityId?.let { abilities[it]?.label }.orEmpty()

    private fun talentSide(event: AbilityLearnEvent, slotByAbility: Map<AbilityId, Int>): TalentSide? =
        event.abilityId?.let { slotByAbility[it] }?.let { sideFromSlot(it) }

    // Stratz talent slot 0-7: even slot = left branch, odd = right (parity verified on-device)
    private fun sideFromSlot(slot: Int): TalentSide =
        if (slot % 2 == 0) TalentSide.LEFT else TalentSide.RIGHT

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

        val shown = reconstructBuild(player.itemPurchases, items)

        val sections = UiItemBuildPhase.entries
            .mapNotNull { phase ->
                val entries = shown
                    .filter { phaseOf(it.time) == phase }
                    .let { dedupeEntries(it, items) }
                    .takeIf { it.isNotEmpty() }
                    ?: return@mapNotNull null
                UiItemBuildSection(phase = phase, entries = entries.toImmutableList())
            }
            .toImmutableList()

        return UiItemBuild(neutralItem = neutral, sections = sections)
    }

    // Drop recipes, fold assembled items' components back into the result, and remove
    // post-laning consumables — leaving the real build path (see item-build model notes).
    // The backend resolves each item's full component tree from Stratz recipe metadata, so
    // when an assembled item is bought its components are consumed from earlier loose
    // purchases (greedy, earliest-first) and hidden.
    private fun reconstructBuild(
        purchases: List<ItemPurchase>,
        items: Map<ItemId, Item>,
    ): List<ItemPurchase> {
        val ordered = purchases
            .sortedBy { it.time ?: Int.MAX_VALUE }
            .filter { items[it.itemId]?.isRecipe != true }

        val consumed = BooleanArray(ordered.size)
        val available = HashMap<ItemId, ArrayDeque<Int>>()
        ordered.forEachIndexed { index, purchase ->
            items[purchase.itemId]?.components.orEmpty().forEach { componentId ->
                available[componentId]?.removeFirstOrNull()?.let { consumed[it] = true }
            }
            available.getOrPut(purchase.itemId) { ArrayDeque() }.addLast(index)
        }

        return ordered.filterIndexed { index, purchase ->
            when {
                consumed[index] -> false
                items[purchase.itemId]?.quality?.startsWith(CONSUMABLE_QUALITY_PREFIX) == true ->
                    phaseOf(purchase.time) == UiItemBuildPhase.LANING
                else -> true
            }
        }
    }

    private fun dedupeEntries(purchases: List<ItemPurchase>, items: Map<ItemId, Item>): List<UiItemBuildEntry> {
        val grouped = LinkedHashMap<ItemId, MutableList<ItemPurchase>>()
        purchases.forEach { grouped.getOrPut(it.itemId) { mutableListOf() }.add(it) }
        return grouped.values.map { group ->
            val first = group.first()
            UiItemBuildEntry(
                iconUrl = items[first.itemId]?.iconUrl,
                name = items[first.itemId]?.displayName,
                timeText = formatTime(first.time),
                count = group.size,
            )
        }
    }

    private fun phaseOf(time: Int?): UiItemBuildPhase = when {
        time == null -> UiItemBuildPhase.LATE_GAME
        time <= LANING_END_SECONDS -> UiItemBuildPhase.LANING
        time <= MID_GAME_END_SECONDS -> UiItemBuildPhase.MID_GAME
        else -> UiItemBuildPhase.LATE_GAME
    }

    private fun buildNetworth(player: BuildPlayer, items: Map<ItemId, Item>): UiNetworthCurve {
        val markers = reconstructBuild(player.itemPurchases, items)
            .filter { it.time != null }
            .filter { items[it.itemId]?.quality?.startsWith(CONSUMABLE_QUALITY_PREFIX) != true }
            .groupBy { it.itemId }
            .map { (_, purchases) -> purchases.minBy { it.time!! } }
            .sortedBy { it.time!! }
            .take(MAX_NETWORTH_MARKERS)
            .map { purchase ->
                UiNetworthMarker(
                    minute = purchase.time!! / SECONDS_PER_MINUTE,
                    iconUrl = items[purchase.itemId]?.iconUrl,
                    name = items[purchase.itemId]?.displayName,
                )
            }
            .toImmutableList()

        return UiNetworthCurve(
            points = player.networthPerMinute.toImmutableList(),
            markers = markers,
            gpm = player.goldPerMinute,
            networth = player.networth,
            xpm = player.experiencePerMinute,
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
        sortedBy { it.position?.ordinal ?: Int.MAX_VALUE }
            .map { member ->
                UiLineupMember(
                    heroIconUrl = member.heroId?.let { heroes[it]?.iconUrl },
                    heroName = member.heroId?.let { heroes[it]?.displayName },
                    isMe = member.steamAccountId == mySteamAccountId,
                    role = member.role?.toUi(),
                )
            }
            .toImmutableList()
}

// TimeConverter collapses negatives to "00:00", so we format pre-horn times here
private fun formatTime(seconds: Int?): String {
    if (seconds == null) return ""
    val sign = if (seconds < 0) "-" else ""
    val abs = kotlin.math.abs(seconds)
    val minutes = abs / 60
    val secs = abs % 60
    return "$sign$minutes:${secs.toString().padStart(2, '0')}"
}
