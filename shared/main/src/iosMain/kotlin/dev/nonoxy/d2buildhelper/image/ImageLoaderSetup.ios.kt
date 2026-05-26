package dev.nonoxy.d2buildhelper.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

internal actual fun createImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizeBytes(MEMORY_IMAGE_CACHE_MAX_SIZE_BYTES)
                .build()
        }
        .apply {
            getImageCacheDirectory()?.let { cacheDir ->
                diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.toPath())
                        .maxSizeBytes(DISK_IMAGE_CACHE_MAX_SIZE_BYTES)
                        .build()
                }
            }
        }
        .crossfade(true)
        .build()

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun getImageCacheDirectory(): String? {
    return try {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSCachesDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        )
        val cachePath = (paths.firstOrNull() as? String)
            ?: error("Unable to get cache directory path")
        val imageCachePath = "$cachePath/$DISK_IMAGE_CACHE_DIR"

        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(imageCachePath)) {
            memScoped {
                val error = alloc<ObjCObjectVar<NSError?>>()
                val success = fileManager.createDirectoryAtPath(
                    path = imageCachePath,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = error.ptr,
                )
                if (!success) {
                    val nsError = error.value
                    error(
                        "Failed to create image cache directory: " +
                            (nsError?.localizedDescription ?: "Unknown error"),
                    )
                }
            }
        }
        imageCachePath
    } catch (throwable: Throwable) {
        Napier.e("Get image cache directory error", throwable = throwable)
        null
    }
}
