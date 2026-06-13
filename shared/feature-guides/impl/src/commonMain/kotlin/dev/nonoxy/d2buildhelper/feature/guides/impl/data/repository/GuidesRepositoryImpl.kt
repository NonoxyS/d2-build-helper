package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesPage
import dev.nonoxy.d2buildhelper.core.match.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers.GuidesPageMapper
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 20

internal class GuidesRepositoryImpl(
    private val apiClient: GuidesApiClient,
    private val guidesPageMapper: GuidesPageMapper,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(filters: GuidesFilters, page: Int): Result<GuidesPage> =
        withContext(coroutineDispatchers.io) {
            coRunCatching(
                tryBlock = {
                    apiClient.getGuides(
                        heroId = filters.heroId?.raw,
                        position = filters.position?.toWire(),
                        isRadiant = filters.isRadiant,
                        page = page,
                        pageSize = PAGE_SIZE,
                    ).map(guidesPageMapper::map)
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "GuidesRepositoryImpl.getGuides failed")
                    throwable.wrapResultFailure()
                },
            )
        }
}

private fun MatchPlayerPosition.toWire(): String = name
