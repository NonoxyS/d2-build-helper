package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.Path.Companion.toOkioPath

private const val MEMORY_CACHE_MAX_SIZE_BYTES = 64L * 1024 * 1024
private const val DISK_CACHE_MAX_SIZE_BYTES = 256L * 1024 * 1024
private const val DISK_CACHE_DIR = "image_cache"

internal actual fun createImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizeBytes(MEMORY_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(DISK_CACHE_DIR).toOkioPath())
                .maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .crossfade(true)
        .build()
