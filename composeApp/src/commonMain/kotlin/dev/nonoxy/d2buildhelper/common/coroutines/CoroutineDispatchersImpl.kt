package dev.nonoxy.d2buildhelper.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

internal class CoroutineDispatchersImpl : CoroutineDispatchers {
    override val io: CoroutineDispatcher get() = ioDispatcher
    override val default: CoroutineDispatcher get() = defaultDispatcher
    override val main: CoroutineDispatcher get() = mainDispatcher
}
