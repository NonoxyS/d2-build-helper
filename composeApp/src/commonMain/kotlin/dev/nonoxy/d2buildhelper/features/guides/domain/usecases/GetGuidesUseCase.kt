package dev.nonoxy.d2buildhelper.features.guides.domain.usecases

import androidx.compose.ui.util.fastLastOrNull
import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesDataSource
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStats
import dev.nonoxy.d2buildhelper.core.data.map
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.guides.domain.models.GuideUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ItemPurchaseUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStatsUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class GetGuidesUseCase(
    private val guidesDataSource: GuidesApi = InjectProvider.getDependency(
        GuidesDataSource::class
    )
) {
    internal fun getGuides(): Flow<RequestResult<List<GuideUI>>> {
        return guidesDataSource.getGuides().map { result ->
            result.map { guides ->
                guides.map { guide ->
                    guide.toGuideUi()
                }
            }
        }.flowOn(Dispatchers.Default)
    }

    internal fun getHeroGuides(heroId: Short): Flow<RequestResult<List<GuideUI>>> {
        return guidesDataSource.getHeroGuides(heroId).map { result ->
            result.map { guides ->
                guides.map { guide ->
                    guide.toGuideUi()
                }
            }
        }.flowOn(Dispatchers.Default)
    }
}

internal fun Guide.toGuideUi(): GuideUI {
    return GuideUI(
        hero = HeroUI(
            heroId = hero.heroId,
            shortName = hero.shortName,
            displayName = hero.displayName
        ),
        steamAccountId = steamAccountId,
        matchId = matchId,
        durationSeconds = durationSeconds,
        playerStats = playerStats.toPlayerStatsUi()
    )
}

internal fun PlayerStats.toPlayerStatsUi(): PlayerStatsUI {
    val endItemIds =
        listOfNotNull(endItem0Id, endItem1Id, endItem2Id, endItem3Id, endItem4Id, endItem5Id)
    // Ищем itemPurchase для каждого предмета в финальной сборке.
    // Т.к. в АПИ могут быть предметы в финальной сборке, но не отображаться в списке itemPurchases,
    // то мы просто добавляем недостающие элементы со значением time = null и сортируем
    // полученный список по времени, все элементы с time = null будут располагаться в конце.
    val sortedEndItemPurchases = endItemIds.map { endItemId ->
        itemPurchases?.fastLastOrNull { it?.itemId?.toShort() == endItemId }?.let { itemPurchase ->
            ItemPurchaseUI(itemId = itemPurchase.itemId.toShort(), time = itemPurchase.time)
        } ?: ItemPurchaseUI(itemId = endItemId, time = null)
    }.sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStatsUI(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant ?: true,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = impact ?: 25,
        endNeutralItemId = endNeutralItemId,
        sortedEndItemPurchases = sortedEndItemPurchases
    )
}