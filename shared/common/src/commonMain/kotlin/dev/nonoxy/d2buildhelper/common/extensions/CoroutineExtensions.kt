package dev.nonoxy.d2buildhelper.common.extensions

import kotlinx.coroutines.CancellationException

suspend inline fun <T> coRunCatching(
    crossinline tryBlock: suspend () -> T,
    crossinline catchBlock: (Throwable) -> Result<T> = { it.wrapResultFailure() },
): Result<T> = try {
    Result.success(tryBlock())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    catchBlock(throwable)
}
