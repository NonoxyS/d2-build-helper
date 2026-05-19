package dev.nonoxy.d2buildhelper.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

internal class CoroutineDispatchersImpl : CoroutineDispatchers {
    override val io: CoroutineDispatcher = ioDispatcher
    override val default: CoroutineDispatcher = defaultDispatcher
    override val main: CoroutineDispatcher = mainDispatcher
}
