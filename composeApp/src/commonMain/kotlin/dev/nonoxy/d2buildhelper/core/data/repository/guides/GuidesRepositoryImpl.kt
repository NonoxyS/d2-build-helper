package dev.nonoxy.d2buildhelper.core.data.repository.guides

import androidx.compose.ui.util.fastLastOrNull
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStatsDto
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ItemPurchase
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStats
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
        itemPurchases?.fastLastOrNull { it?.itemId?.toShort() == endItemId }?.let { itemPurchase ->
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
