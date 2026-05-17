package dev.nonoxy.d2buildhelper.common.utils

import kotlinx.coroutines.channels.Channel

/**
 * One-shot events channel for MVI side-effect labels.
 *
 * `Channel.BUFFERED` (capacity 64) matches the KMMTemplate convention: avoids
 * dropping labels during navigation transitions but does not grow unbounded.
 * If you see suspended producers in profiling, the consumer side is gone —
 * fix that, don't switch to `UNLIMITED`.
 */
@Suppress("FunctionName")
fun <T> OneTimeEvent(): Channel<T> = Channel(Channel.BUFFERED)
