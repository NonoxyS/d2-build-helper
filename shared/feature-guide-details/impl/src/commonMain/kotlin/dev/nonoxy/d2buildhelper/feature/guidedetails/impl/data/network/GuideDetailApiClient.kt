package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network

import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailResponse

internal interface GuideDetailApiClient {

    suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<RemoteGuideDetailResponse>

    companion object {
        const val URL_GUIDE_DETAIL = "/v1/guides"
    }
}
