package dev.nonoxy.d2buildhelper.core.resources.data.network

import dev.nonoxy.d2buildhelper.core.resources.data.network.models.RemoteConstantsResponse

internal interface ConstantsApiClient {

    suspend fun getConstants(): Result<RemoteConstantsResponse>

    companion object {
        const val URL_CONSTANTS = "/v1/constants"
    }
}
