package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Hero
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.ItemPurchase
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStats
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
    ): Flow<RequestResult<DetailGuide>> {
        return flowOf(RequestResult.InProgress())
    }
}

private val mockGuides = listOf(
    Guide(
        hero = Hero(heroId = 11, shortName = "nevermore", displayName = "Shadow Fiend"),
        steamAccountId = 299419908,
        matchId = 7837192981,
        durationSeconds = 2827,
        playerStats = PlayerStats(
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
            itemPurchases = listOf(ItemPurchase(itemId = 44, time = -89), ItemPurchase(
                itemId = 216,
                time = -89
            ), ItemPurchase(itemId = 237, time = -89), ItemPurchase(
                itemId = 16,
                time = -89
            ), ItemPurchase(itemId = 42, time = -89), ItemPurchase(
                itemId = 43,
                time = -89
            ), ItemPurchase(itemId = 34, time = -3), ItemPurchase(
                itemId = 35,
                time = -3
            ), ItemPurchase(itemId = 36, time = -2), ItemPurchase(
                itemId = 41,
                time = 93
            ), ItemPurchase(itemId = 29, time = 155), ItemPurchase(
                itemId = 25,
                time = 224
            ), ItemPurchase(itemId = 42, time = 254), ItemPurchase(
                itemId = 46,
                time = 254
            ), ItemPurchase(itemId = 18, time = 313), ItemPurchase(
                itemId = 63,
                time = 337
            ), ItemPurchase(itemId = 265, time = 346), ItemPurchase(
                itemId = 42,
                time = 421
            ), ItemPurchase(itemId = 43, time = 421), ItemPurchase(
                itemId = 18,
                time = 440
            ), ItemPurchase(itemId = 22, time = 536), ItemPurchase(
                itemId = 169,
                time = 645
            ), ItemPurchase(itemId = 170, time = 667), ItemPurchase(
                itemId = 38,
                time = 673
            ), ItemPurchase(itemId = 23, time = 715), ItemPurchase(
                itemId = 19,
                time = 764
            ), ItemPurchase(itemId = 38, time = 820), ItemPurchase(
                itemId = 258,
                time = 846
            ), ItemPurchase(itemId = 259, time = 873), ItemPurchase(
                itemId = 277,
                time = 873
            ), ItemPurchase(itemId = 265, time = 884), ItemPurchase(
                itemId = 1,
                time = 1040
            ), ItemPurchase(itemId = 38, time = 1042), ItemPurchase(
                itemId = 8,
                time = 1192
            ), ItemPurchase(itemId = 115, time = 1281), ItemPurchase(
                itemId = 21,
                time = 1330
            ), ItemPurchase(itemId = 116, time = 1347), ItemPurchase(
                itemId = 46,
                time = 1350
            ), ItemPurchase(itemId = 46, time = 1350), ItemPurchase(
                itemId = 56,
                time = 1479
            ), ItemPurchase(itemId = 57, time = 1479), ItemPurchase(
                itemId = 69,
                time = 1479
            ), ItemPurchase(itemId = 122, time = 1480), ItemPurchase(
                itemId = 24,
                time = 1643
            ), ItemPurchase(itemId = 123, time = 1651), ItemPurchase(
                itemId = 46,
                time = 1653
            ), ItemPurchase(itemId = 46, time = 1794), ItemPurchase(
                itemId = 46,
                time = 1794
            ), ItemPurchase(itemId = 60, time = 1940), ItemPurchase(
                itemId = 23,
                time = 1943
            ), ItemPurchase(itemId = 21, time = 1945), ItemPurchase(
                itemId = 22,
                time = 1969
            ), ItemPurchase(itemId = 108, time = 1974), ItemPurchase(
                itemId = 109,
                time = 2443
            ), ItemPurchase(itemId = 1125, time = 2469), ItemPurchase(
                itemId = 279,
                time = 2469
            ), ItemPurchase(itemId = 1802, time = 2469), ItemPurchase(
                itemId = 110,
                time = 2483
            ), ItemPurchase(itemId = 29, time = 2492), ItemPurchase(
                itemId = 47,
                time = 2492
            ), ItemPurchase(itemId = 46, time = 2570), ItemPurchase(
                itemId = 270,
                time = 2697
            ), ItemPurchase(itemId = 271, time = 2697))
        )
    ), Guide(
        hero = Hero(heroId = 19, shortName = "tiny", displayName = "Tiny"),
        steamAccountId = 420369951,
        matchId = 7837206286,
        durationSeconds = 2210,
        playerStats = PlayerStats(
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
            itemPurchases = listOf(ItemPurchase(itemId = 44, time = -83), ItemPurchase(
                itemId = 44,
                time = -81
            ), ItemPurchase(itemId = 237, time = -80), ItemPurchase(
                itemId = 16,
                time = -80
            ), ItemPurchase(itemId = 41, time = 78), ItemPurchase(
                itemId = 34,
                time = 147
            ), ItemPurchase(itemId = 35, time = 147), ItemPurchase(
                itemId = 36,
                time = 175
            ), ItemPurchase(itemId = 29, time = 197), ItemPurchase(
                itemId = 265,
                time = 237
            ), ItemPurchase(itemId = 46, time = 321), ItemPurchase(
                itemId = 43,
                time = 340
            ), ItemPurchase(itemId = 42, time = 340), ItemPurchase(
                itemId = 4,
                time = 382
            ), ItemPurchase(itemId = 2, time = 419), ItemPurchase(
                itemId = 50,
                time = 442
            ), ItemPurchase(itemId = 46, time = 610), ItemPurchase(
                itemId = 1,
                time = 731
            ), ItemPurchase(itemId = 1122, time = 877), ItemPurchase(
                itemId = 46,
                time = 878
            ), ItemPurchase(itemId = 1106, time = 878), ItemPurchase(
                itemId = 60,
                time = 1015
            ), ItemPurchase(itemId = 1107, time = 1015), ItemPurchase(
                itemId = 21,
                time = 1173
            ), ItemPurchase(itemId = 39, time = 1174), ItemPurchase(
                itemId = 8,
                time = 1285
            ), ItemPurchase(itemId = 46, time = 1333), ItemPurchase(
                itemId = 115,
                time = 1465
            ), ItemPurchase(itemId = 116, time = 1500), ItemPurchase(
                itemId = 4205,
                time = 1597
            ), ItemPurchase(itemId = 5, time = 1605), ItemPurchase(
                itemId = 2,
                time = 1619
            ), ItemPurchase(itemId = 148, time = 1620), ItemPurchase(
                itemId = 149,
                time = 1620
            ), ItemPurchase(itemId = 1807, time = 1733), ItemPurchase(
                itemId = 1808,
                time = 1757
            ), ItemPurchase(itemId = 485, time = 1948), ItemPurchase(
                itemId = 188,
                time = 1964
            ), ItemPurchase(itemId = 215, time = 2162), ItemPurchase(
                itemId = 3,
                time = 2162
            ), ItemPurchase(itemId = 152, time = 2197))
        )
    )
)