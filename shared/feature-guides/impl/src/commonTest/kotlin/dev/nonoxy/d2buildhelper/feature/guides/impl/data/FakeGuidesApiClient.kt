package dev.nonoxy.d2buildhelper.feature.guides.impl.data

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemotePaginationResponse

internal fun emptyGuidesPage(): RemoteGuidesPageResponse = RemoteGuidesPageResponse(
    pagination = RemotePaginationResponse(page = 0, pageSize = 50, hasMore = false),
    guides = emptyList(),
)

internal class FakeGuidesApiClient(
    var guides: Result<RemoteGuidesPageResponse> = Result.success(emptyGuidesPage()),
    var heroGuides: Result<RemoteGuidesPageResponse> = Result.success(emptyGuidesPage()),
) : GuidesApiClient {
    override suspend fun getGuides(): Result<RemoteGuidesPageResponse> = guides

    override suspend fun getHeroGuides(heroId: Short): Result<RemoteGuidesPageResponse> = heroGuides
}
