package dev.nonoxy.d2buildhelper.feature.guides.impl.data.network

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.models.RemoteGuidesPageResponse

internal interface GuidesApiClient {

    suspend fun getGuides(
        heroId: Short?,
        position: String?,
        isRadiant: Boolean?,
        page: Int,
        pageSize: Int,
    ): Result<RemoteGuidesPageResponse>

    companion object {
        const val URL_GUIDES = "/v1/guides"
    }
}
