package dev.nonoxy.d2buildhelper.core.network.ktor

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.http.content.PartData
import kotlinx.serialization.DeserializationStrategy

interface KtorClient {

    suspend fun <R, D> executeQuery(
        query: suspend () -> HttpResponse,
        deserializer: DeserializationStrategy<D>? = null,
        success: (D) -> Result<R>,
        loggingErrorMessage: String,
    ): Result<R>

    suspend fun get(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse

    suspend fun post(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse

    suspend fun submitForm(
        urlString: String,
        formParameters: Parameters,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse

    suspend fun submitFormWithBinaryData(
        urlString: String,
        formData: List<PartData>,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse
}
