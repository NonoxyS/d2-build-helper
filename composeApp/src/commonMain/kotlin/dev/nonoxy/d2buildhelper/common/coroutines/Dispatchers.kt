@file:Suppress("UnusedImport")
/*
 * `import kotlinx.coroutines.IO` is REQUIRED for Kotlin/Native.
 *
 * On Native targets, `Dispatchers.IO` is `internal` inside
 * `kotlinx.coroutines.Dispatchers` and only exposed through a top-level
 * extension property in `kotlinx-coroutines-core` 1.10.x. The import
 * brings that extension into scope; without it, JVM/Android continue to
 * compile but iOS fails. IDE "optimize imports" will mark the import as
 * unused — do not remove it.
 */
package dev.nonoxy.d2buildhelper.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
val mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
