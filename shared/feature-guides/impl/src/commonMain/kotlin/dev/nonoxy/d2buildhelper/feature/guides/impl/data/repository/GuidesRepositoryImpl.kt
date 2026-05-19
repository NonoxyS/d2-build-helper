package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.GuidesApi
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.GuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import kotlinx.coroutines.withContext

internal class GuidesRepositoryImpl(
    private val guidesApi: GuidesApi,
    private val dispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(): Result<List<Guide>> =
        guidesApi.getGuides().mapCatching { dtos ->
            withContext(dispatchers.default) { dtos.map(GuideDto::toDomain) }
        }

    override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> =
        guidesApi.getHeroGuides(heroId).mapCatching { dtos ->
            withContext(dispatchers.default) { dtos.map(GuideDto::toDomain) }
        }
}

private fun GuideDto.toDomain(): Guide = Guide(
    hero = Hero(
        heroId = hero.heroId,
        shortName = hero.shortName,
        displayName = hero.displayName,
    ),
    steamAccountId = steamAccountId,
    matchId = matchId,
    durationSeconds = durationSeconds,
    playerStats = playerStats.toDomain(),
)

private fun PlayerStatsDto.toDomain(): PlayerStats {
    val endItemIds = listOfNotNull(endItem0Id, endItem1Id, endItem2Id, endItem3Id, endItem4Id, endItem5Id)
    val sortedEndItemPurchases = endItemIds.map { endItemId ->
        itemPurchases?.lastOrNull { it?.itemId?.toShort() == endItemId }?.let { itemPurchase ->
            ItemPurchase(itemId = itemPurchase.itemId.toShort(), time = itemPurchase.time)
        } ?: ItemPurchase(itemId = endItemId, time = null)
    }.sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStats(
        position = MatchPlayerPosition.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant ?: true,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = impact ?: 25,
        endNeutralItemId = endNeutralItemId,
        sortedEndItemPurchases = sortedEndItemPurchases,
    )
}
