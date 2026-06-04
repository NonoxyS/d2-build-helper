package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data

import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailPlayerResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailResponse

internal fun guideDetailResponse(
    matchId: Long = 100L,
    steamAccountId: Long = 1L,
    player: RemoteGuideDetailPlayerResponse = RemoteGuideDetailPlayerResponse(heroId = 1),
): RemoteGuideDetailResponse = RemoteGuideDetailResponse(
    matchId = matchId,
    steamAccountId = steamAccountId,
    gameVersionId = 174,
    player = player,
)

internal class FakeGuideDetailApiClient(
    var detail: Result<RemoteGuideDetailResponse> = Result.success(guideDetailResponse()),
) : GuideDetailApiClient {
    var lastMatchId: Long = -1
    var lastSteamAccountId: Long = -1

    override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<RemoteGuideDetailResponse> {
        lastMatchId = matchId
        lastSteamAccountId = steamAccountId
        return detail
    }
}
