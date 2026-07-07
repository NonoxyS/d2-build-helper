package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
internal class TestCoroutineDispatchers(
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : CoroutineDispatchers {
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val main: CoroutineDispatcher = dispatcher
}
