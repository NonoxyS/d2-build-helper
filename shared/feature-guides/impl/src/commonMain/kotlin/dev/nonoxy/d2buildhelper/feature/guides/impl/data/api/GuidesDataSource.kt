package dev.nonoxy.d2buildhelper.feature.guides.impl.data.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.mappers.toGuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.GuideDto
import dev.nonoxy.d2buildhelper.graphql.GuidesQuery
import dev.nonoxy.d2buildhelper.graphql.HeroGuidesQuery
import io.github.aakira.napier.Napier
import kotlinx.coroutines.withContext

internal class GuidesDataSource(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineDispatchers,
) : GuidesApi {

    override suspend fun getGuides(): Result<List<GuideDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                val response = apolloClient.query(GuidesQuery()).execute()
                val ex = response.exception
                when {
                    ex != null -> throw ex
                    response.hasErrors() -> error("GraphQL errors: ${response.errors}")
                    else -> response.dataOrThrow().heroStats?.guideFilterNotNull()?.flatMap { guide ->
                        guide.guidesFilterNotNull()?.map { it.toGuideDto() } ?: emptyList()
                    } ?: emptyList()
                }
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getGuides failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>> = withContext(dispatchers.io) {
        coRunCatching(
            tryBlock = {
                val response = apolloClient
                    .query(HeroGuidesQuery(heroId = Optional.present(heroId.toInt())))
                    .execute()
                val ex = response.exception
                when {
                    ex != null -> throw ex
                    response.hasErrors() -> error("GraphQL errors: ${response.errors}")
                    else -> response.dataOrThrow().heroStats?.guideFilterNotNull()?.flatMap { guide ->
                        guide.guidesFilterNotNull()?.map { it.toGuideDto() } ?: emptyList()
                    } ?: emptyList()
                }
            },
            catchBlock = { throwable ->
                Napier.e(throwable = throwable, message = "getHeroGuides($heroId) failed")
                throwable.wrapResultFailure()
            },
        )
    }

    override suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto> {
        TODO("Not yet implemented — detail guide feature is a stub")
    }
}
