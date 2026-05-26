package dev.nonoxy.d2buildhelper.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.Path.Companion.toOkioPath

internal actual fun createImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizeBytes(MEMORY_IMAGE_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(DISK_IMAGE_CACHE_DIR).toOkioPath())
                .maxSizeBytes(DISK_IMAGE_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .crossfade(true)
        .build()
