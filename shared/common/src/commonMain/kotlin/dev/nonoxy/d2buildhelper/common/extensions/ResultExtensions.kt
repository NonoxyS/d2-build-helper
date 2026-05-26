package dev.nonoxy.d2buildhelper.common.extensions

fun <T> T.wrapResultSuccess(): Result<T> = Result.success(this)

fun <T> Throwable.wrapResultFailure(): Result<T> = Result.failure(this)

fun <A, B> combineResults(a: Result<A>, b: Result<B>): Result<Pair<A, B>> {
    val aError = a.exceptionOrNull()
    val bError = b.exceptionOrNull()
    return when {
        aError != null && bError != null -> {
            aError.addSuppressed(bError)
            Result.failure(aError)
        }
        aError != null -> Result.failure(aError)
        bError != null -> Result.failure(bError)
        else -> Result.success(a.getOrThrow() to b.getOrThrow())
    }
}
