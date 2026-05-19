package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.GuideDto

internal interface GuidesApi {
    suspend fun getGuides(): Result<List<GuideDto>>

    suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>>

    suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto>
}
