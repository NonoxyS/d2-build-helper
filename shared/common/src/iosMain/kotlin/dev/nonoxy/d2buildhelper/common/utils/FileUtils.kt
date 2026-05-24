package dev.nonoxy.d2buildhelper.common.utils

import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

private const val IMAGE_CACHE_DIR = "image_cache"

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun getImageCacheDirectory(): String? {
    return try {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSCachesDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        )
        val cachePath = (paths.firstOrNull() as? String)
            ?: error("Unable to get cache directory path")
        val imageCachePath = "$cachePath/$IMAGE_CACHE_DIR"

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
