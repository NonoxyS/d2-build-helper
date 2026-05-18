package dev.nonoxy.d2buildhelper.core.data.api.guides

import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto

internal interface GuidesApi {
    suspend fun getGuides(): Result<List<GuideDto>>

    suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>>

    suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto>
}
