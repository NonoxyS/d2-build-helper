package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository

import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail

internal interface GuideDetailRepository {

    suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<GuideDetail>
}
