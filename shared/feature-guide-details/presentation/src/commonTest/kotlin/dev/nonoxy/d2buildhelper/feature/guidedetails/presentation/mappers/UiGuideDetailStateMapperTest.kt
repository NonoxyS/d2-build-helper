package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers

import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.AbilityId
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.core.domain.models.Item
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.core.match.ItemPurchase
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.AbilityLearnEvent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.BuildPlayer
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.InventorySnapshot
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.LineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
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

    private fun item(id: Short) = Item(
        id = ItemId(id),
        shortName = "i$id",
        displayName = "I$id",
        iconUrl = ImageUrl("item$id"),
    )

    private fun ability(id: Short, name: String = "A$id") = Ability(
        id = AbilityId(id),
        name = name,
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
        finalItemIds = emptyList(),
        backpackItemIds = emptyList(),
        neutralItemId = null,
        abilityLearnEvents = emptyList(),
        itemPurchases = emptyList(),
        inventorySnapshots = emptyList(),
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
    fun `maps header - hero name, side, duration, W L, KDA, level, impact`() {
        // NOTE: position is left null here. `UiMatchPlayerPosition.toUi()` touches
        // `MR.images`, whose generated Android `R$drawable` is not on the host-JVM
        // unit-test classpath (moko-resources limitation) — the same reason the
        // guides mapper test keeps position null. Position passthrough is the
        // pure `MatchPlayerPosition.toUi()` mapping, covered in common-ui.
        val player = emptyPlayer(heroId = 5).copy(
            isRadiant = false,
            isVictory = true,
            position = null,
            role = "offlane",
            lane = "off",
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
        assertEquals("offlane", header.role)
        assertEquals("44:00", header.durationText)
        assertEquals(27, header.level)
        assertEquals(true, header.isVictory)
        assertEquals(8, header.kills)
        assertEquals(6, header.deaths)
        assertEquals(21, header.assists)
        assertEquals(9, header.impact)
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
        assertEquals(2, summary.abilities.first().pointCount) // Q learned twice
        assertEquals(1, summary.abilities[1].pointCount) // W learned once
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
        // sorted by time ascending: -89 first, then 183
        assertEquals(ImageUrl("item1"), build.purchases.first().iconUrl)
        assertEquals("-1:29", build.purchases.first().timeText)
        assertEquals("3:03", build.purchases[1].timeText)
    }

    @Test
    fun `inventory snapshots map item slots and preserve null empty slots`() {
        val player = emptyPlayer(heroId = 1).copy(
            inventorySnapshots = listOf(
                InventorySnapshot(
                    itemIds = listOf(ItemId(1), null, ItemId(2), null, null, null),
                    backpackIds = listOf(null, ItemId(3), null),
                    neutralId = ItemId(50),
                ),
            ),
        )
        val ui = mapper.map(
            state(
                detail(player),
                heroes = mapOf(HeroId(1) to hero(1)),
                items = mapOf(
                    ItemId(1) to item(1),
                    ItemId(2) to item(2),
                    ItemId(3) to item(3),
                    ItemId(50) to item(50),
                ),
            ),
        )

        val snap = ui.inventory!!.snapshots.single()
        assertEquals(ImageUrl("item1"), snap.itemIconUrls[0])
        assertNull(snap.itemIconUrls[1])
        assertEquals(ImageUrl("item2"), snap.itemIconUrls[2])
        assertNull(snap.backpackIconUrls[0])
        assertEquals(ImageUrl("item3"), snap.backpackIconUrls[1])
        assertEquals(ImageUrl("item50"), snap.neutralIconUrl)
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
                role = "mid",
            ),
            LineupMember(
                steamAccountId = 7L,
                heroId = HeroId(2),
                isRadiant = true,
                position = MatchPlayerPosition.POSITION_1,
                role = "carry",
            ),
            LineupMember(
                steamAccountId = 8L,
                heroId = HeroId(3),
                isRadiant = false,
                position = MatchPlayerPosition.POSITION_3,
                role = "off",
            ),
            LineupMember(
                steamAccountId = 9L,
                heroId = HeroId(4),
                isRadiant = false,
                position = MatchPlayerPosition.POSITION_1,
                role = "carry",
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
    fun `networth maps points, gpm, networth, last hits at 10 and purchase markers`() {
        val player = emptyPlayer(heroId = 1).copy(
            goldPerMinute = 412,
            networth = 18400,
            networthPerMinute = listOf(0, 100, 200, 300),
            lastHitsPerMinute = (0..11).map { it * 4 },
            itemPurchases = listOf(
                ItemPurchase(ItemId(1), time = 120), // minute 2
                ItemPurchase(ItemId(2), time = 185), // minute 3
            ),
        )
        val ui = mapper.map(state(detail(player), heroes = mapOf(HeroId(1) to hero(1))))

        val nw = ui.networth!!
        assertEquals(412, nw.gpm)
        assertEquals(18400, nw.networth)
        assertEquals(40, nw.lastHitsAt10) // index 10 -> 40
        assertEquals(listOf(0, 100, 200, 300), nw.points)
        assertEquals(listOf(2, 3), nw.purchaseMarkerMinutes)
    }

    @Test
    fun `null detail yields all null sub-models and passes loading-error through`() {
        val ui = mapper.map(state(detail = null, isLoading = true, isError = false))

        assertNull(ui.header)
        assertNull(ui.skillBuild)
        assertNull(ui.itemBuild)
        assertNull(ui.inventory)
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
        assertNull(ui.inventory) // no snapshots
        assertTrue(ui.header != null)
        assertTrue(ui.itemBuild != null)
        assertTrue(ui.networth != null)
    }
}
