package dev.nonoxy.d2buildhelper.feature.guides.impl.data

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemotePaginationResponse

internal fun emptyGuidesPage(gameVersionId: Int = 0): RemoteGuidesPageResponse = RemoteGuidesPageResponse(
    pagination = RemotePaginationResponse(page = 0, pageSize = 20, hasMore = false),
    gameVersionId = gameVersionId,
    guides = emptyList(),
)

internal class FakeGuidesApiClient(
    var guides: Result<RemoteGuidesPageResponse> = Result.success(emptyGuidesPage()),
) : GuidesApiClient {
    var lastHeroId: Short? = null
    var lastPosition: String? = null
    var lastIsRadiant: Boolean? = null
    var lastPage: Int = -1

    override suspend fun getGuides(
        heroId: Short?,
        position: String?,
        isRadiant: Boolean?,
        page: Int,
        pageSize: Int,
    ): Result<RemoteGuidesPageResponse> {
        lastHeroId = heroId
        lastPosition = position
        lastIsRadiant = isRadiant
        lastPage = page
        return guides
    }
}
