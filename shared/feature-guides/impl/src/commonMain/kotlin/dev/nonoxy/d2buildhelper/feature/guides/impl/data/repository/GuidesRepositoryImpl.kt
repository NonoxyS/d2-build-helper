package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidePlayerResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

private const val DEFAULT_IMPACT: Short = 25

internal class GuidesRepositoryImpl(
    private val apiClient: GuidesApiClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(): Result<List<Guide>> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { apiClient.getGuides().getOrThrow().guides.map { it.toDomain() } },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "GuidesRepositoryImpl.getGuides failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getHeroGuides(heroId: Short): Result<List<Guide>> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { apiClient.getHeroGuides(heroId).getOrThrow().guides.map { it.toDomain() } },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "GuidesRepositoryImpl.getHeroGuides($heroId) failed")
                throwable.wrapResultFailure()
            },
        )
    }
}

private fun RemoteGuideResponse.toDomain(): Guide = Guide(
    hero = Hero(
        id = hero.id.toShort(),
        shortName = hero.shortName.orEmpty(),
        displayName = hero.displayName.orEmpty(),
    ),
    steamAccountId = steamAccountId,
    matchId = matchId,
    durationSeconds = durationSeconds ?: 0,
    playerStats = player.toDomain(),
)

private fun RemoteGuidePlayerResponse.toDomain(): PlayerStats {
    val sortedEndItemPurchases = finalItemIds
        .map { itemId ->
            val purchaseTime = itemPurchases.lastOrNull { it.itemId == itemId }?.time
            ItemPurchase(itemId = itemId.toShort(), time = purchaseTime)
        }
        .sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStats(
        position = position.toMatchPlayerPosition(),
        isRadiant = isRadiant ?: true,
        kills = kills?.toByte() ?: 0,
        deaths = deaths?.toByte() ?: 0,
        assists = assists?.toByte() ?: 0,
        impact = impact?.toShort() ?: DEFAULT_IMPACT,
        endNeutralItemId = neutralItemId?.toShort(),
        sortedEndItemPurchases = sortedEndItemPurchases,
    )
}

private fun String?.toMatchPlayerPosition(): MatchPlayerPosition =
    this
        ?.let { name -> MatchPlayerPosition.entries.firstOrNull { it.name == name } }
        ?: MatchPlayerPosition.UNKNOWN
