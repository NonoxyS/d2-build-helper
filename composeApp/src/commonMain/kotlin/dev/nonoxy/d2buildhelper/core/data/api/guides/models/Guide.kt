package dev.nonoxy.d2buildhelper.core.data.api.guides.models

data class Guide(
    val heroId: Short,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: PlayerStats
)

data class PlayerStats(
    val position: MatchPlayerPositionType?,
    val isRadiant: Boolean?,
    val kills: Byte,
    val deaths: Byte,
    val assists: Byte,
    val impact: Short?,
    val endItem0Id: Short?,
    val endItem1Id: Short?,
    val endItem2Id: Short?,
    val endItem3Id: Short?,
    val endItem4Id: Short?,
    val endItem5Id: Short?,
    val endBackpack0Id: Short?,
    val endBackpack1Id: Short?,
    val endBackpack2Id: Short?,
    val endNeutralItemId: Short?,
    val itemPurchases: List<ItemPurchase?>?,
    val inventoryChanges: List<InventoryChange?>?,
)

data class ItemPurchase(
    val itemId: Int,
    val time: Int
)

data class InventoryChange(
    val item0: Item0?,
    val item1: Item1?,
    val item2: Item2?,
    val item3: Item3?,
    val item4: Item4?,
    val item5: Item5?,
    val backpack0: Backpack0?,
    val backpack1: Backpack1?,
    val backpack2: Backpack2?
)

data class Item0(
    val itemId: Int,
    val charges: Int?
)

data class Item1(
    val itemId: Int,
    val charges: Int?
)

data class Item2(
    val itemId: Int,
    val charges: Int?
)

data class Item3(
    val itemId: Int,
    val charges: Int?
)

data class Item4(
    val itemId: Int,
    val charges: Int?
)

data class Item5(
    val itemId: Int,
    val charges: Int?
)

data class Backpack0(
    val itemId: Int
)

data class Backpack1(
    val itemId: Int
)

data class Backpack2(
    val itemId: Int
)

data class AbilityLearnEvent(
    val abilityId: Short,
    val levelAbility: Int,
    val levelObtained: Int,
    val isTalent: Boolean?
)

enum class MatchPlayerPositionType(val title: String) {
    POSITION_1("POSITION_1"),
    POSITION_2("POSITION_2"),
    POSITION_3("POSITION_3"),
    POSITION_4("POSITION_4"),
    POSITION_5("POSITION_5"),
    UNKNOWN("UNKNOWN"),
    FILTERED("FILTERED"),
    ALL("ALL")
}
