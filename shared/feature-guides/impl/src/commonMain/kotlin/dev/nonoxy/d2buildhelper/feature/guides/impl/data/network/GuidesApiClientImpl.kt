package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network

import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import io.ktor.client.request.parameter

internal class GuidesApiClientImpl(
    private val ktorClient: KtorClient,
) : GuidesApiClient {

    override suspend fun getGuides(
        heroId: Short?,
        position: String?,
        isRadiant: Boolean?,
        page: Int,
        pageSize: Int,
    ): Result<RemoteGuidesPageResponse> =
        ktorClient.executeQuery(
            query = {
                ktorClient.get(GuidesApiClient.URL_GUIDES) {
                    heroId?.let { parameter("heroId", it) }
                    position?.let { parameter("position", it) }
                    isRadiant?.let { parameter("isRadiant", it) }
                    parameter("page", page)
                    parameter("pageSize", pageSize)
                }
            },
            deserializer = RemoteGuidesPageResponse.serializer(),
            success = { it.wrapResultSuccess() },
            loggingErrorMessage = "GuidesApiClientImpl: failed to fetch guides",
        )
}
