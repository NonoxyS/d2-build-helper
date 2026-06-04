package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network

import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailResponse

internal class GuideDetailApiClientImpl(
    private val ktorClient: KtorClient,
) : GuideDetailApiClient {

    override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<RemoteGuideDetailResponse> =
        ktorClient.executeQuery(
            query = {
                ktorClient.get("${GuideDetailApiClient.URL_GUIDE_DETAIL}/$matchId/$steamAccountId")
            },
            deserializer = RemoteGuideDetailResponse.serializer(),
            success = { it.wrapResultSuccess() },
            loggingErrorMessage = "GuideDetailApiClientImpl: failed to fetch guide detail",
        )
}
