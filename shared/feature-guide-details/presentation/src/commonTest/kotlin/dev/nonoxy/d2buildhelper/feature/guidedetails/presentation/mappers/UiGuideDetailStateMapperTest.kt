package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers

import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchLane
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerRole
import dev.nonoxy.d2buildhelper.core.match.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.MatchLane
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerRole
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.AbilityLearnEvent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.BuildPlayer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.LineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildPhase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UiGuideDetailStateMapperTest {

    private val mapper = UiGuideDetailStateMapperImpl()

    // Risk constants under test (see mapper — smoke-verify-needed).
    private val attributeBonusAbilityId = AbilityId(STAT_ABILITY_ID_RAW)
    private val scepterItemId = ItemId(ULTIMATE_SCEPTER_ITEM_ID_RAW)

    private fun hero(id: Short) = Hero(
        id = HeroId(id),
        shortName = "h$id",
        displayName = "H$id",
        iconUrl = ImageUrl("hero$id"),
    )

    private fun item(
        id: Short,
        quality: String? = null,
        isRecipe: Boolean = false,
        components: List<ItemId> = emptyList(),
    ) = Item(
        id = ItemId(id),
        shortName = "i$id",
        displayName = "I$id",
        iconUrl = ImageUrl("item$id"),
        quality = quality,
        isRecipe = isRecipe,
        components = components,
    )

    private fun ability(id: Short, name: String = "A$id", displayName: String = name) = Ability(
        id = AbilityId(id),
        name = name,
        displayName = displayName,
        iconUrl = ImageUrl("ab$id"),
    )

    private fun emptyPlayer(heroId: Short) = BuildPlayer(
        heroId = HeroId(heroId),
        isRadiant = true,
        isVictory = true,
        position = null,
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
    )

    private fun detail(player: BuildPlayer, lineup: List<LineupMember> = emptyList()) = GuideDetail(
        matchId = 1L,
        steamAccountId = 42L,
        didRadiantWin = true,
        durationSeconds = 2640,
        averageRank = 80,
        player = player,
        lineup = lineup,
    )

    private fun state(
        detail: GuideDetail?,
        heroes: Map<HeroId, Hero> = emptyMap(),
        items: Map<ItemId, Item> = emptyMap(),
        abilities: Map<AbilityId, Ability> = emptyMap(),
        isLoading: Boolean = false,
        isError: Boolean = false,
    ) = GuideDetailStore.State(
        detail = detail,
        heroes = heroes,
        items = items,
        abilities = abilities,
        isLoading = isLoading,
        isError = isError,
    )

    @Test
    fun `maps header - hero name, side, duration, W L, KDA, level`() {
        // NOTE: position is left null here. `UiMatchPlayerPosition.toUi()` touches
        // `MR.images`, whose generated Android `R$drawable` is not on the host-JVM
        // unit-test classpath (moko-resources limitation) — the same reason the
        // guides mapper test keeps position null. Position passthrough is the
        // pure `MatchPlayerPosition.toUi()` mapping, covered in common-ui.
        val player = emptyPlayer(heroId = 5).copy(
            isRadiant = false,
            isVictory = true,
            position = null,
            role = MatchPlayerRole.CORE,
            lane = MatchLane.OFF_LANE,
            level = 27,
            kills = 8,
            deaths = 6,
            assists = 21,
            impact = 9,
        )
        val ui = mapper.map(
            state(detail(player), heroes = mapOf(HeroId(5) to hero(5))),
        )

        val header = ui.header!!
        assertEquals("H5", header.heroName)
        assertEquals(ImageUrl("hero5"), header.heroIconUrl)
        assertEquals(false, header.isRadiant)
        assertNull(header.position)
        assertEquals(UiMatchPlayerRole.CORE, header.role)
        assertEquals(UiMatchLane.OFF_LANE, header.lane)
        assertEquals("44:00", header.durationText)
        assertEquals(27, header.level)
        assertEquals(true, header.isVictory)
        assertEquals(8, header.kills)
        assertEquals(6, header.deaths)
        assertEquals(21, header.assists)
    }

    @Test
    fun `skill matrix has exactly one mark per column and stats go to stats row`() {
        // Q at lvl 1, W at lvl 2, stats at lvl 3, R(ult) at lvl 6, talent at lvl 10.
        val q = ability(10)
        val w = ability(11)
        val r = ability(12)
        val talent = ability(99, "+6 strength")
        val player = emptyPlayer(heroId = 1).copy(
            abilityLearnEvents = listOf(
                AbilityLearnEvent(time = 0, abilityId = q.id, level = 1, isTalent = false, isUltimate = false),
                AbilityLearnEvent(time = 60, abilityId = w.id, level = 2, isTalent = false, isUltimate = false),
                AbilityLearnEvent(
                    time = 120,
                    abilityId = attributeBonusAbilityId,
                    level = 3,
                    isTalent = false,
                    isUltimate = false,
                ),
                AbilityLearnEvent(time = 360, abilityId = r.id, level = 6, isTalent = false, isUltimate = true),
                AbilityLearnEvent(time = 600, abilityId = talent.id, level = 10, isTalent = true, isUltimate = false),
            ),
        )
        val ui = mapper.map(
            state(
                detail(player),
                heroes = mapOf(HeroId(1) to hero(1)),
                abilities = mapOf(
                    q.id to q,
                    w.id to w,
                    r.id to r,
                    talent.id to talent,
                    attributeBonusAbilityId to ability(STAT_ABILITY_ID_RAW, "stats"),
                ),
            ),
        )

        val matrix = ui.skillBuild!!.matrix
        // exactly one mark per used column across all rows
        for (level in listOf(1, 2, 3, 6)) {
            val marksAtColumn = matrix.rows.count { it.marks[level - 1] }
            assertEquals(1, marksAtColumn, "column $level must have exactly one mark")
        }
        // talent does not occupy a matrix column (lvl 10 has no mark)
        assertEquals(0, matrix.rows.count { it.marks[9] })

        // stats row exists and carries the lvl-3 mark
        val statsRow = matrix.rows.single { it.isStat }
        assertTrue(statsRow.marks[2])
        assertNull(statsRow.iconUrl)
        assertFalse(statsRow.isUltimate)

        // R (lvl 6) is the ultimate row; Q/W are not; stats row never ultimate
        val ultimateRows = matrix.rows.filter { it.isUltimate }
        assertEquals(1, ultimateRows.size)
        assertEquals(ImageUrl("ab12"), ultimateRows.single().iconUrl) // R = ability(12)
        assertTrue(matrix.rows.single { it.iconUrl == ImageUrl("ab12") }.isUltimate)
        assertFalse(matrix.rows.single { it.iconUrl == ImageUrl("ab10") }.isUltimate) // Q
        assertFalse(matrix.rows.single { it.iconUrl == ImageUrl("ab11") }.isUltimate) // W

        // summary mirrors the ultimate flag on the R ability
        val summaryAbilities = ui.skillBuild!!.summary.abilities
        assertEquals(1, summaryAbilities.count { it.isUltimate })
        assertTrue(summaryAbilities.single { it.iconUrl == ImageUrl("ab12") }.isUltimate)
        assertFalse(summaryAbilities.single { it.iconUrl == ImageUrl("ab10") }.isUltimate)

        // ability names come from constants; stats matrix row carries no name
        assertEquals("A10", summaryAbilities.single { it.iconUrl == ImageUrl("ab10") }.name) // Q
        assertEquals("A12", summaryAbilities.single { it.iconUrl == ImageUrl("ab12") }.name) // R
        assertEquals("A12", matrix.rows.single { it.iconUrl == ImageUrl("ab12") }.name)
        assertNull(matrix.rows.single { it.isStat }.name)

        // talent is separated into talents list
        assertEquals(1, ui.skillBuild!!.talents.size)
        assertEquals(10, ui.skillBuild!!.talents.first().level)
        assertEquals("+6 strength", ui.skillBuild!!.talents.first().text)
    }

    @Test
    fun `summary point counts and scepter detection`() {
        val q = ability(10)
        val w = ability(11)
        val player = emptyPlayer(heroId = 1).copy(
            abilityLearnEvents = listOf(
                AbilityLearnEvent(0, q.id, 1, isTalent = false, isUltimate = false),
                AbilityLearnEvent(60, q.id, 2, isTalent = false, isUltimate = false),
                AbilityLearnEvent(120, w.id, 3, isTalent = false, isUltimate = false),
                AbilityLearnEvent(480, q.id, 8, isTalent = false, isUltimate = false),
            ),
            finalItemIds = listOf(scepterItemId, ItemId(3)),
        )
        val ui = mapper.map(
            state(
                detail(player),
                heroes = mapOf(HeroId(1) to hero(1)),
                abilities = mapOf(q.id to q, w.id to w),
            ),
        )

        val summary = ui.skillBuild!!.summary
        assertEquals(3, summary.abilities.first().pointCount) // Q learned at 1, 2, 8
        assertEquals(2, summary.abilities.first().earlyPointCount) // level 8 past the first 6
        assertEquals(1, summary.abilities[1].pointCount) // W learned once
        assertEquals(1, summary.abilities[1].earlyPointCount)
        assertTrue(summary.scepterPurchased)
    }

    @Test
    fun `item build maps neutral and sorts purchases with pre-horn negative time`() {
        val player = emptyPlayer(heroId = 1).copy(
            neutralItemId = ItemId(50),
            itemPurchases = listOf(
                ItemPurchase(itemId = ItemId(2), time = 183),
                ItemPurchase(itemId = ItemId(1), time = -89),
            ),
        )
        val ui = mapper.map(
            state(
                detail(player),
                heroes = mapOf(HeroId(1) to hero(1)),
                items = mapOf(ItemId(50) to item(50), ItemId(1) to item(1), ItemId(2) to item(2)),
            ),
        )

        val build = ui.itemBuild!!
        assertEquals(ImageUrl("item50"), build.neutralItem!!.iconUrl)
        assertEquals("I50", build.neutralItem!!.name) // neutral display name from constants
        // Both purchases (time=-89 and time=183) are in LANING (both ≤600s).
        // Section is sorted by time ascending: -89 first, then 183.
        assertEquals(1, build.sections.size)
        val laningSection = build.sections.single()
        assertEquals(UiItemBuildPhase.LANING, laningSection.phase)
        assertEquals(ImageUrl("item1"), laningSection.entries.first().iconUrl)
        assertEquals("I1", laningSection.entries.first().name) // item display name from constants
        assertEquals("I2", laningSection.entries[1].name)
        assertEquals("-1:29", laningSection.entries.first().timeText)
        assertEquals("3:03", laningSection.entries[1].timeText)
    }

    @Test
    fun `item build phase bucketing groups purchases into correct phases`() {
        // times: -60 (pre-horn, LANING), 120 (LANING), 800 (MID_GAME), 1600 (LATE_GAME), null (LATE_GAME)
        val player = emptyPlayer(heroId = 1).copy(
            itemPurchases = listOf(
                ItemPurchase(itemId = ItemId(1), time = -60),
                ItemPurchase(itemId = ItemId(2), time = 120),
                ItemPurchase(itemId = ItemId(3), time = 800),
                ItemPurchase(itemId = ItemId(4), time = 1600),
                ItemPurchase(itemId = ItemId(5), time = null),
            ),
        )
        val ui = mapper.map(state(detail(player)))

        val build = ui.itemBuild!!
        // Exactly 3 sections, in LANING → MID_GAME → LATE_GAME order.
        assertEquals(3, build.sections.size)
        assertEquals(UiItemBuildPhase.LANING, build.sections[0].phase)
        assertEquals(UiItemBuildPhase.MID_GAME, build.sections[1].phase)
        assertEquals(UiItemBuildPhase.LATE_GAME, build.sections[2].phase)

        // LANING: items with time -60 and 120, sorted ascending.
        val laning = build.sections[0]
        assertEquals(2, laning.entries.size)
        assertEquals("-1:00", laning.entries[0].timeText)
        assertEquals("2:00", laning.entries[1].timeText)

        // MID_GAME: item with time 800.
        val mid = build.sections[1]
        assertEquals(1, mid.entries.size)
        assertEquals("13:20", mid.entries[0].timeText)

        // LATE_GAME: items with time 1600 and null (null sorts last).
        val late = build.sections[2]
        assertEquals(2, late.entries.size)
        assertEquals("26:40", late.entries[0].timeText)
        assertEquals("", late.entries[1].timeText)
    }

    @Test
    fun `item build phase bucketing respects exact boundaries 600 and 1500`() {
        // time == 600 is the upper boundary of LANING (rule: time ≤ 600 → LANING).
        // time == 1500 is the upper boundary of MID_GAME (rule: time ≤ 1500 → MID_GAME).
        // An off-by-one in phaseOf (<  vs <=) would misplace these and fail the test.
        val player = emptyPlayer(heroId = 1).copy(
            itemPurchases = listOf(
                ItemPurchase(itemId = ItemId(1), time = 600),
                ItemPurchase(itemId = ItemId(2), time = 1500),
            ),
        )
        val ui = mapper.map(state(detail(player)))

        val build = ui.itemBuild!!
        assertEquals(2, build.sections.size)

        val laningSection = build.sections.single { it.phase == UiItemBuildPhase.LANING }
        assertEquals(1, laningSection.entries.size)
        assertEquals("10:00", laningSection.entries.single().timeText)

        val midSection = build.sections.single { it.phase == UiItemBuildPhase.MID_GAME }
        assertEquals(1, midSection.entries.size)
        assertEquals("25:00", midSection.entries.single().timeText)
    }

    @Test
    fun `item build folds components, drops recipes, keeps laning consumables only, dedups`() {
        // ogre(1)+mithril(2)+recipe(3) assemble bkb(4); the backend resolves bkb's full
        // component tree, so ingredients hang off bkb. blink(5) standalone; tango(6) consumable.
        val items = mapOf(
            ItemId(1) to item(1, quality = "component"),
            ItemId(2) to item(2, quality = "component"),
            ItemId(3) to item(3, isRecipe = true),
            ItemId(4) to item(4, quality = "rare", components = listOf(ItemId(1), ItemId(2))),
            ItemId(5) to item(5, quality = "component"),
            ItemId(6) to item(6, quality = "consumable"),
        )
        val player = emptyPlayer(heroId = 1).copy(
            itemPurchases = listOf(
                ItemPurchase(itemId = ItemId(6), time = 30),
                ItemPurchase(itemId = ItemId(6), time = 45),
                ItemPurchase(itemId = ItemId(1), time = 120),
                ItemPurchase(itemId = ItemId(2), time = 140),
                ItemPurchase(itemId = ItemId(3), time = 150),
                ItemPurchase(itemId = ItemId(4), time = 160),
                ItemPurchase(itemId = ItemId(5), time = 700),
                ItemPurchase(itemId = ItemId(6), time = 900),
            ),
        )
        val ui = mapper.map(state(detail(player), items = items))

        val build = ui.itemBuild!!
        val laning = build.sections.single { it.phase == UiItemBuildPhase.LANING }
        assertEquals(listOf("I6", "I4"), laning.entries.map { it.name })
        assertEquals(2, laning.entries.first().count)
        assertEquals(1, laning.entries[1].count)

        val mid = build.sections.single { it.phase == UiItemBuildPhase.MID_GAME }
        assertEquals(listOf("I5"), mid.entries.map { it.name })
    }

    @Test
    fun `lineup splits allies and enemies, sorts by position, flags isMe`() {
        val player = emptyPlayer(heroId = 1).copy(isRadiant = true)
        val lineup = listOf(
            LineupMember(
                steamAccountId = 42L,
                heroId = HeroId(1),
                isRadiant = true,
                position = MatchPlayerPosition.POSITION_2,
                role = MatchPlayerRole.CORE,
            ),
            LineupMember(
                steamAccountId = 7L,
                heroId = HeroId(2),
                isRadiant = true,
                position = MatchPlayerPosition.POSITION_1,
                role = MatchPlayerRole.CORE,
            ),
            LineupMember(
                steamAccountId = 8L,
                heroId = HeroId(3),
                isRadiant = false,
                position = MatchPlayerPosition.POSITION_3,
                role = MatchPlayerRole.CORE,
            ),
            LineupMember(
                steamAccountId = 9L,
                heroId = HeroId(4),
                isRadiant = false,
                position = MatchPlayerPosition.POSITION_1,
                role = MatchPlayerRole.CORE,
            ),
        )
        val ui = mapper.map(
            state(
                detail(player, lineup),
                heroes = mapOf(HeroId(1) to hero(1), HeroId(2) to hero(2), HeroId(3) to hero(3), HeroId(4) to hero(4)),
            ),
        )

        val lin = ui.lineup!!
        assertEquals(2, lin.allies.size)
        assertEquals(2, lin.enemies.size)
        // allies sorted by position 1 then 2 -> hero2 (carry) first, hero1 (me) second
        assertEquals(ImageUrl("hero2"), lin.allies.first().heroIconUrl)
        assertEquals(ImageUrl("hero1"), lin.allies[1].heroIconUrl)
        assertTrue(lin.allies[1].isMe)
        assertFalse(lin.allies.first().isMe)
        // enemies sorted by position: pos1 (hero4) then pos3 (hero3)
        assertEquals(ImageUrl("hero4"), lin.enemies.first().heroIconUrl)
        assertEquals(ImageUrl("hero3"), lin.enemies[1].heroIconUrl)
    }

    @Test
    fun `networth maps points, gpm, networth, xpm and purchase markers`() {
        val player = emptyPlayer(heroId = 1).copy(
            goldPerMinute = 412,
            networth = 18400,
            experiencePerMinute = 615,
            networthPerMinute = listOf(0, 100, 200, 300),
            lastHitsPerMinute = (0..11).map { it * 4 },
            finalItemIds = listOf(ItemId(1), ItemId(2)),
            itemPurchases = listOf(
                ItemPurchase(ItemId(1), time = 120), // minute 2 — significant (in finalItemIds)
                ItemPurchase(ItemId(2), time = 185), // minute 3 — significant (in finalItemIds)
            ),
        )
        val ui = mapper.map(
            state(
                detail(player),
                heroes = mapOf(HeroId(1) to hero(1)),
                items = mapOf(ItemId(1) to item(1), ItemId(2) to item(2)),
            ),
        )

        val nw = ui.networth!!
        assertEquals(412, nw.gpm)
        assertEquals(18400, nw.networth)
        assertEquals(615, nw.xpm)
        assertEquals(listOf(0, 100, 200, 300), nw.points)
        assertEquals(listOf(2, 3), nw.markers.map { it.minute })
        assertEquals(ImageUrl("item1"), nw.markers[0].iconUrl)
        assertEquals("I1", nw.markers[0].name)
        assertEquals(ImageUrl("item2"), nw.markers[1].iconUrl)
        assertEquals("I2", nw.markers[1].name)
    }

    @Test
    fun `networth markers derive from the build path - consumables, folded parts and rebuys excluded`() {
        // ogre(1)+mithril(2) fold into bkb(4); item(5) standalone; tango(6) consumable.
        val items = mapOf(
            ItemId(1) to item(1, quality = "component"),
            ItemId(2) to item(2, quality = "component"),
            ItemId(4) to item(4, quality = "rare", components = listOf(ItemId(1), ItemId(2))),
            ItemId(5) to item(5, quality = "epic"),
            ItemId(6) to item(6, quality = "consumable"),
        )
        val player = emptyPlayer(heroId = 1).copy(
            networthPerMinute = (0..30).map { it * 100 },
            itemPurchases = listOf(
                ItemPurchase(ItemId(6), time = 60), // consumable → not a marker
                ItemPurchase(ItemId(1), time = 120), // folded into bkb → excluded
                ItemPurchase(ItemId(2), time = 140), // folded into bkb → excluded
                ItemPurchase(ItemId(4), time = 300), // bkb @min5 → marker
                ItemPurchase(ItemId(5), time = 900), // standalone @min15 → marker
                ItemPurchase(ItemId(5), time = 1400), // rebuy → excluded (first kept)
            ),
        )
        val ui = mapper.map(state(detail(player), heroes = mapOf(HeroId(1) to hero(1)), items = items))

        val nw = ui.networth!!
        assertEquals(listOf(5, 15), nw.markers.map { it.minute })
        assertEquals(listOf("I4", "I5"), nw.markers.map { it.name })
    }

    @Test
    fun `null detail yields all null sub-models and passes loading-error through`() {
        val ui = mapper.map(state(detail = null, isLoading = true, isError = false))

        assertNull(ui.header)
        assertNull(ui.skillBuild)
        assertNull(ui.itemBuild)
        assertNull(ui.networth)
        assertNull(ui.lineup)
        assertTrue(ui.isLoading)
        assertFalse(ui.isError)
    }

    @Test
    fun `empty ability learn events yields null skill build but other cards present`() {
        val player = emptyPlayer(heroId = 1)
        val ui = mapper.map(state(detail(player), heroes = mapOf(HeroId(1) to hero(1))))

        assertNull(ui.skillBuild)
        assertTrue(ui.header != null)
        assertTrue(ui.itemBuild != null)
        assertTrue(ui.networth != null)
    }
}
