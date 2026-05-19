package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models

internal class DetailGuideDto(
    val hero: HeroDto,
    val steamAccountId: Long,
    val matchId: Long,
    val durationSeconds: Int,
    val playerStats: DetailPlayerStatsDto
)

internal class DetailPlayerStatsDto(
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
    val itemPurchases: List<ItemPurchaseDto?>?,
    val inventoryChanges: List<InventoryChangeDto?>?,
    val abilityLearnEvents: List<AbilityLearnEventDto?>?
)

internal class InventoryChangeDto(
    val item0: Item0Dto?,
    val item1: Item1Dto?,
    val item2: Item2Dto?,
    val item3: Item3Dto?,
    val item4: Item4Dto?,
    val item5: Item5Dto?,
    val backpack0: Backpack0Dto?,
    val backpack1: Backpack1Dto?,
    val backpack2: Backpack2Dto?
)

internal class Item0Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Item1Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Item2Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Item3Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Item4Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Item5Dto(
    val itemId: Int,
    val charges: Int?
)

internal class Backpack0Dto(
    val itemId: Int
)

internal class Backpack1Dto(
    val itemId: Int
)

internal class Backpack2Dto(
    val itemId: Int
)

internal class AbilityLearnEventDto(
    val abilityId: Short,
    val levelAbility: Int,
    val levelObtained: Int,
    val isTalent: Boolean?
)
