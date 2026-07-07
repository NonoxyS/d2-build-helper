package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.GuideDetailApiClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

internal class GuideDetailRepositoryImpl(
    private val apiClient: GuideDetailApiClient,
    private val guideDetailMapper: GuideDetailMapper,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GuideDetailRepository {

    override suspend fun getGuideDetail(matchId: Long, steamAccountId: Long): Result<GuideDetail> =
        withContext(coroutineDispatchers.io) {
            coRunCatching(
                tryBlock = {
                    apiClient.getGuideDetail(matchId, steamAccountId).map(guideDetailMapper::map)
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "GuideDetailRepositoryImpl.getGuideDetail failed")
                    throwable.wrapResultFailure()
                },
            )
        }
}
