package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import dev.nonoxy.d2buildhelper.common.utils.getImageCacheDirectory
import okio.Path.Companion.toPath

private const val MEMORY_CACHE_MAX_SIZE_BYTES = 64L * 1024 * 1024
private const val DISK_CACHE_MAX_SIZE_BYTES = 256L * 1024 * 1024

internal actual fun createImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizeBytes(MEMORY_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .apply {
            getImageCacheDirectory()?.let { cacheDir ->
                diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.toPath())
                        .maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
                        .build()
                }
            }
        }
        .crossfade(true)
        .build()
