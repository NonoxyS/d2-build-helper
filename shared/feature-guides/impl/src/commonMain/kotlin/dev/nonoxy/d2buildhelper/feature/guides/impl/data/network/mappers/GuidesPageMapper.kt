package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ItemId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesPage
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Pagination
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.PlayerStats
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidePlayerResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuideResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemotePaginationResponse

private const val DEFAULT_IMPACT: Short = 25

internal interface GuidesPageMapper : Mapper<RemoteGuidesPageResponse, GuidesPage>

internal class GuidesPageMapperImpl : GuidesPageMapper {

    override fun map(item: RemoteGuidesPageResponse): GuidesPage = GuidesPage(
        gameVersion = GameVersion(item.gameVersionId),
        guides = item.guides.map { it.toDomain() },
        pagination = item.pagination.toDomain(),
    )

    private fun RemotePaginationResponse.toDomain(): Pagination = Pagination(
        page = page,
        hasMore = hasMore,
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
            position = position?.toDomain(),
            isRadiant = isRadiant ?: true,
            kills = kills?.toByte() ?: 0,
            deaths = deaths?.toByte() ?: 0,
            assists = assists?.toByte() ?: 0,
            impact = impact?.toShort() ?: DEFAULT_IMPACT,
            endNeutralItemId = neutralItemId?.toShort()?.let(::ItemId),
            sortedEndItemPurchases = sortedEndItemPurchases,
        )
    }

    private fun RemoteMatchPlayerPosition.toDomain(): MatchPlayerPosition? = when (this) {
        RemoteMatchPlayerPosition.POSITION_1 -> MatchPlayerPosition.POSITION_1
        RemoteMatchPlayerPosition.POSITION_2 -> MatchPlayerPosition.POSITION_2
        RemoteMatchPlayerPosition.POSITION_3 -> MatchPlayerPosition.POSITION_3
        RemoteMatchPlayerPosition.POSITION_4 -> MatchPlayerPosition.POSITION_4
        RemoteMatchPlayerPosition.POSITION_5 -> MatchPlayerPosition.POSITION_5
        RemoteMatchPlayerPosition.UNKNOWN -> null
    }
}
