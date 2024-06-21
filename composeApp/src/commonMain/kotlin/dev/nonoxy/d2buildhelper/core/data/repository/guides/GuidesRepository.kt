package dev.nonoxy.d2buildhelper.core.data.repository.guides

import com.apollographql.apollo3.ApolloClient
import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.repository.guides.mappers.toGuide
import dev.nonoxy.d2buildhelper.core.data.toRequestResult
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.graphql.GuidesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class GuidesRepository(
    private val apolloClient: ApolloClient = InjectProvider.getDependency(type = ApolloClient::class)
) : GuidesApi {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getGuides(): Flow<RequestResult<List<Guide>>> {
        val apiRequest = flow {
            emit(apolloClient.query(GuidesQuery()).execute())
        }.flowOn(Dispatchers.IO)
            .map { it.toRequestResult() }
            .flatMapConcat { requestResult ->
                when (requestResult) {
                    is RequestResult.Success -> {
                        val guidesList =
                            requestResult.data.heroStats?.guideFilterNotNull()?.flatMap { guide ->
                                guide.guidesFilterNotNull()?.map { guides ->
                                    guides.toGuide()
                                } ?: emptyList()
                            } ?: emptyList()
                        flowOf(RequestResult.Success(guidesList))
                    }

                    is RequestResult.InProgress -> flowOf(RequestResult.InProgress())
                    is RequestResult.Error -> flowOf(RequestResult.Error(requestResult.error))
                }
            }.flowOn(Dispatchers.Default)

        val start = flowOf<RequestResult<List<Guide>>>(RequestResult.InProgress())

        return merge(apiRequest, start)
    }
}