package dev.nonoxy.d2buildhelper.core.data

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation

sealed class RequestResult<out E: Any>(open val data: E? = null) {

    class InProgress<E: Any> : RequestResult<E>()
    class Success<E: Any>(override val data: E) : RequestResult<E>(data)
    class Error<E: Any>(val error: Any? = null) : RequestResult<E>()
}

fun <I: Any, O: Any> RequestResult<I>.map(mapper: (I) -> O): RequestResult<O> {
    return when (this) {
        is RequestResult.Success -> RequestResult.Success(mapper(data))
        is RequestResult.InProgress -> RequestResult.InProgress()
        is RequestResult.Error -> RequestResult.Error(error)
    }
}

internal fun <T : Operation.Data> ApolloResponse<T>.toRequestResult(): RequestResult<T> {
    return when {
        hasErrors() -> RequestResult.Error(errors)
        exception != null -> RequestResult.Error(exception)
        data != null -> RequestResult.Success(dataOrThrow())
        else -> error("Impossible branch")
    }
}