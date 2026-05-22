package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network

import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import io.ktor.client.request.parameter

private const val PAGE_SIZE = 50

internal class GuidesApiClientImpl(
    private val ktorClient: KtorClient,
) : GuidesApiClient {

    override suspend fun getGuides(): Result<RemoteGuidesPageResponse> =
        ktorClient.executeQuery(
            query = {
                ktorClient.get(GuidesApiClient.URL_GUIDES) {
                    parameter("pageSize", PAGE_SIZE)
                }
            },
            deserializer = RemoteGuidesPageResponse.serializer(),
            success = { it.wrapResultSuccess() },
            loggingErrorMessage = "GuidesApiClientImpl: failed to fetch guides",
        )

    override suspend fun getHeroGuides(heroId: Short): Result<RemoteGuidesPageResponse> =
        ktorClient.executeQuery(
            query = {
                ktorClient.get(GuidesApiClient.URL_GUIDES) {
                    parameter("heroId", heroId)
                    parameter("pageSize", PAGE_SIZE)
                }
            },
            deserializer = RemoteGuidesPageResponse.serializer(),
            success = { it.wrapResultSuccess() },
            loggingErrorMessage = "GuidesApiClientImpl: failed to fetch guides for hero $heroId",
        )
}
