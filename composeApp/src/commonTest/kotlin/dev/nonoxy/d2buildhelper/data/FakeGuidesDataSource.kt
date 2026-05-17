package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.ItemPurchaseDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStatsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeGuidesDataSource : GuidesApi {
    override fun getGuides() = flowOf(
        RequestResult.Success(mockGuides)
    )

    override fun getHeroGuides(heroId: Short) = flowOf(
        RequestResult.Success(mockGuides)
    )

    override fun getDetailGuide(
        matchId: Long,
        steamAccountId: Long
    ): Flow<RequestResult<DetailGuideDto>> {
        return flowOf(RequestResult.InProgress())
    }
}

private val mockGuides = listOf(
    GuideDto(
        hero = HeroDto(heroId = 11, shortName = "nevermore", displayName = "Shadow Fiend"),
        steamAccountId = 299419908,
        matchId = 7837192981,
        durationSeconds = 2827,
        playerStats = PlayerStatsDto(
            position = MatchPlayerPositionType.POSITION_2,
            isRadiant = false,
            kills = 17,
            deaths = 9,
            assists = 18,
            impact = 32,
            endItem0Id = 1,
            endItem1Id = 110,
            endItem2Id = 123,
            endItem3Id = 116,
            endItem4Id = 277,
            endItem5Id = 48,
            endBackpack0Id = 41,
            endBackpack1Id = null,
            endBackpack2Id = null,
            endNeutralItemId = 362,
            itemPurchases = listOf(ItemPurchaseDto(itemId = 44, time = -89), ItemPurchaseDto(
                itemId = 216,
                time = -89
            ), ItemPurchaseDto(itemId = 237, time = -89), ItemPurchaseDto(
                itemId = 16,
                time = -89
            ), ItemPurchaseDto(itemId = 42, time = -89), ItemPurchaseDto(
                itemId = 43,
                time = -89
            ), ItemPurchaseDto(itemId = 34, time = -3), ItemPurchaseDto(
                itemId = 35,
                time = -3
            ), ItemPurchaseDto(itemId = 36, time = -2), ItemPurchaseDto(
                itemId = 41,
                time = 93
            ), ItemPurchaseDto(itemId = 29, time = 155), ItemPurchaseDto(
                itemId = 25,
                time = 224
            ), ItemPurchaseDto(itemId = 42, time = 254), ItemPurchaseDto(
                itemId = 46,
                time = 254
            ), ItemPurchaseDto(itemId = 18, time = 313), ItemPurchaseDto(
                itemId = 63,
                time = 337
            ), ItemPurchaseDto(itemId = 265, time = 346), ItemPurchaseDto(
                itemId = 42,
                time = 421
            ), ItemPurchaseDto(itemId = 43, time = 421), ItemPurchaseDto(
                itemId = 18,
                time = 440
            ), ItemPurchaseDto(itemId = 22, time = 536), ItemPurchaseDto(
                itemId = 169,
                time = 645
            ), ItemPurchaseDto(itemId = 170, time = 667), ItemPurchaseDto(
                itemId = 38,
                time = 673
            ), ItemPurchaseDto(itemId = 23, time = 715), ItemPurchaseDto(
                itemId = 19,
                time = 764
            ), ItemPurchaseDto(itemId = 38, time = 820), ItemPurchaseDto(
                itemId = 258,
                time = 846
            ), ItemPurchaseDto(itemId = 259, time = 873), ItemPurchaseDto(
                itemId = 277,
                time = 873
            ), ItemPurchaseDto(itemId = 265, time = 884), ItemPurchaseDto(
                itemId = 1,
                time = 1040
            ), ItemPurchaseDto(itemId = 38, time = 1042), ItemPurchaseDto(
                itemId = 8,
                time = 1192
            ), ItemPurchaseDto(itemId = 115, time = 1281), ItemPurchaseDto(
                itemId = 21,
                time = 1330
            ), ItemPurchaseDto(itemId = 116, time = 1347), ItemPurchaseDto(
                itemId = 46,
                time = 1350
            ), ItemPurchaseDto(itemId = 46, time = 1350), ItemPurchaseDto(
                itemId = 56,
                time = 1479
            ), ItemPurchaseDto(itemId = 57, time = 1479), ItemPurchaseDto(
                itemId = 69,
                time = 1479
            ), ItemPurchaseDto(itemId = 122, time = 1480), ItemPurchaseDto(
                itemId = 24,
                time = 1643
            ), ItemPurchaseDto(itemId = 123, time = 1651), ItemPurchaseDto(
                itemId = 46,
                time = 1653
            ), ItemPurchaseDto(itemId = 46, time = 1794), ItemPurchaseDto(
                itemId = 46,
                time = 1794
            ), ItemPurchaseDto(itemId = 60, time = 1940), ItemPurchaseDto(
                itemId = 23,
                time = 1943
            ), ItemPurchaseDto(itemId = 21, time = 1945), ItemPurchaseDto(
                itemId = 22,
                time = 1969
            ), ItemPurchaseDto(itemId = 108, time = 1974), ItemPurchaseDto(
                itemId = 109,
                time = 2443
            ), ItemPurchaseDto(itemId = 1125, time = 2469), ItemPurchaseDto(
                itemId = 279,
                time = 2469
            ), ItemPurchaseDto(itemId = 1802, time = 2469), ItemPurchaseDto(
                itemId = 110,
                time = 2483
            ), ItemPurchaseDto(itemId = 29, time = 2492), ItemPurchaseDto(
                itemId = 47,
                time = 2492
            ), ItemPurchaseDto(itemId = 46, time = 2570), ItemPurchaseDto(
                itemId = 270,
                time = 2697
            ), ItemPurchaseDto(itemId = 271, time = 2697))
        )
    ), GuideDto(
        hero = HeroDto(heroId = 19, shortName = "tiny", displayName = "Tiny"),
        steamAccountId = 420369951,
        matchId = 7837206286,
        durationSeconds = 2210,
        playerStats = PlayerStatsDto(
            position = MatchPlayerPositionType.POSITION_2,
            isRadiant = false,
            kills = 13,
            deaths = 4,
            assists = 16,
            impact = 21,
            endItem0Id = 1,
            endItem1Id = 116,
            endItem2Id = 50,
            endItem3Id = 1808,
            endItem4Id = 36,
            endItem5Id = 152,
            endBackpack0Id = null,
            endBackpack1Id = null,
            endBackpack2Id = null,
            endNeutralItemId = 1159,
            itemPurchases = listOf(ItemPurchaseDto(itemId = 44, time = -83), ItemPurchaseDto(
                itemId = 44,
                time = -81
            ), ItemPurchaseDto(itemId = 237, time = -80), ItemPurchaseDto(
                itemId = 16,
                time = -80
            ), ItemPurchaseDto(itemId = 41, time = 78), ItemPurchaseDto(
                itemId = 34,
                time = 147
            ), ItemPurchaseDto(itemId = 35, time = 147), ItemPurchaseDto(
                itemId = 36,
                time = 175
            ), ItemPurchaseDto(itemId = 29, time = 197), ItemPurchaseDto(
                itemId = 265,
                time = 237
            ), ItemPurchaseDto(itemId = 46, time = 321), ItemPurchaseDto(
                itemId = 43,
                time = 340
            ), ItemPurchaseDto(itemId = 42, time = 340), ItemPurchaseDto(
                itemId = 4,
                time = 382
            ), ItemPurchaseDto(itemId = 2, time = 419), ItemPurchaseDto(
                itemId = 50,
                time = 442
            ), ItemPurchaseDto(itemId = 46, time = 610), ItemPurchaseDto(
                itemId = 1,
                time = 731
            ), ItemPurchaseDto(itemId = 1122, time = 877), ItemPurchaseDto(
                itemId = 46,
                time = 878
            ), ItemPurchaseDto(itemId = 1106, time = 878), ItemPurchaseDto(
                itemId = 60,
                time = 1015
            ), ItemPurchaseDto(itemId = 1107, time = 1015), ItemPurchaseDto(
                itemId = 21,
                time = 1173
            ), ItemPurchaseDto(itemId = 39, time = 1174), ItemPurchaseDto(
                itemId = 8,
                time = 1285
            ), ItemPurchaseDto(itemId = 46, time = 1333), ItemPurchaseDto(
                itemId = 115,
                time = 1465
            ), ItemPurchaseDto(itemId = 116, time = 1500), ItemPurchaseDto(
                itemId = 4205,
                time = 1597
            ), ItemPurchaseDto(itemId = 5, time = 1605), ItemPurchaseDto(
                itemId = 2,
                time = 1619
            ), ItemPurchaseDto(itemId = 148, time = 1620), ItemPurchaseDto(
                itemId = 149,
                time = 1620
            ), ItemPurchaseDto(itemId = 1807, time = 1733), ItemPurchaseDto(
                itemId = 1808,
                time = 1757
            ), ItemPurchaseDto(itemId = 485, time = 1948), ItemPurchaseDto(
                itemId = 188,
                time = 1964
            ), ItemPurchaseDto(itemId = 215, time = 2162), ItemPurchaseDto(
                itemId = 3,
                time = 2162
            ), ItemPurchaseDto(itemId = 152, time = 2197))
        )
    )
)