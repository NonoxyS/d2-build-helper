package dev.nonoxy.d2buildhelper.core.data.api.guides

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto
import kotlinx.coroutines.flow.Flow

internal interface GuidesApi {
    fun getGuides(): Flow<RequestResult<List<GuideDto>>>

    fun getHeroGuides(heroId: Short): Flow<RequestResult<List<GuideDto>>>

    fun getDetailGuide(matchId: Long, steamAccountId: Long): Flow<RequestResult<DetailGuideDto>>
}