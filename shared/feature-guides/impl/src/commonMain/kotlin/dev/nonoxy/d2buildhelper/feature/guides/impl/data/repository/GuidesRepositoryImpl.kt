package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesPage
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.mappers.GuidesPageMapper
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.network.GuidesApiClient
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

internal class GuidesRepositoryImpl(
    private val apiClient: GuidesApiClient,
    private val guidesPageMapper: GuidesPageMapper,
    private val coroutineDispatchers: CoroutineDispatchers,
) : GuidesRepository {

    override suspend fun getGuides(): Result<GuidesPage> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { guidesPageMapper.map(apiClient.getGuides().getOrThrow()) },
            catchBlock = { throwable ->
                Napier.e(
                    throwable = throwable,
                    message = "GuidesRepositoryImpl.getGuides failed"
                )
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getHeroGuides(heroId: HeroId): Result<GuidesPage> = withContext(coroutineDispatchers.io) {
        coRunCatching(
            tryBlock = { guidesPageMapper.map(apiClient.getHeroGuides(heroId.raw).getOrThrow()) },
            catchBlock = { throwable ->
                Napier.e(
                    throwable = throwable,
                    message = "GuidesRepositoryImpl.getHeroGuides($heroId) failed"
                )
                throwable.wrapResultFailure()
            },
        )
    }
}
