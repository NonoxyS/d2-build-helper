package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesPage
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidePlayerResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

private const val DEFAULT_IMPACT: Short = 25

internal class GuidesRepositoryImpl(
    private val apiClient: GuidesApiClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(): Result<GuidesPage> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { apiClient.getGuides().getOrThrow().toDomain() },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "GuidesRepositoryImpl.getGuides failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getHeroGuides(heroId: HeroId): Result<GuidesPage> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { apiClient.getHeroGuides(heroId.raw).getOrThrow().toDomain() },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "GuidesRepositoryImpl.getHeroGuides($heroId) failed")
                throwable.wrapResultFailure()
            },
        )
    }
}

private fun RemoteGuidesPageResponse.toDomain(): GuidesPage = GuidesPage(
    gameVersion = GameVersion(gameVersionId),
    guides = guides.map { it.toDomain() },
)

private fun RemoteGuideResponse.toDomain(): Guide = Guide(
    matchId = matchId,
    steamAccountId = steamAccountId,
    durationSeconds = durationSeconds ?: 0,
    heroId = HeroId(heroId.toShort()),
    playerStats = player.toDomain(),
)

private fun RemoteGuidePlayerResponse.toDomain(): PlayerStats {
    val sortedEndItemPurchases = finalItemIds
        .map { itemId ->
            val purchaseTime = itemPurchases.lastOrNull { it.itemId == itemId }?.time
            ItemPurchase(itemId = ItemId(itemId.toShort()), time = purchaseTime)
        }
        .sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStats(
        position = position.toMatchPlayerPosition(),
        isRadiant = isRadiant ?: true,
        kills = kills?.toByte() ?: 0,
        deaths = deaths?.toByte() ?: 0,
        assists = assists?.toByte() ?: 0,
        impact = impact?.toShort() ?: DEFAULT_IMPACT,
        endNeutralItemId = neutralItemId?.toShort()?.let(::ItemId),
        sortedEndItemPurchases = sortedEndItemPurchases,
    )
}

private fun String?.toMatchPlayerPosition(): MatchPlayerPosition =
    this
        ?.let { name -> MatchPlayerPosition.entries.firstOrNull { it.name == name } }
        ?: MatchPlayerPosition.UNKNOWN
