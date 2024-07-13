package dev.nonoxy.d2buildhelper.core.data.api.guides

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import kotlinx.coroutines.flow.Flow

interface GuidesApi {
    fun getGuides(): Flow<RequestResult<List<Guide>>>

    fun getHeroGuides(heroId: Short): Flow<RequestResult<List<Guide>>>

    fun getDetailGuide(matchId: Long, steamAccountId: Long): Flow<RequestResult<DetailGuide>>
}