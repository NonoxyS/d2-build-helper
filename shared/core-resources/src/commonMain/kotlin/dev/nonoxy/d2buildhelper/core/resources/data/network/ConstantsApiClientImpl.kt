package dev.nonoxy.d2buildhelper.core.resources.data.network

import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClient
import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteConstantsResponse

internal class ConstantsApiClientImpl(
    private val ktorClient: KtorClient,
) : ConstantsApiClient {

    override suspend fun getConstants(): Result<RemoteConstantsResponse> =
        ktorClient.executeQuery(
            query = { ktorClient.get(ConstantsApiClient.URL_CONSTANTS) },
            deserializer = RemoteConstantsResponse.serializer(),
            success = { it.wrapResultSuccess() },
            loggingErrorMessage = "ConstantsApiClientImpl: failed to fetch constants",
        )
}
