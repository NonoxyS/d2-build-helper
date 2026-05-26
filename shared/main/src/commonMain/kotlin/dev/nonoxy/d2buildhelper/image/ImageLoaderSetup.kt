package dev.nonoxy.d2buildhelper.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader

internal const val MEMORY_IMAGE_CACHE_MAX_SIZE_BYTES = 128L * 1024 * 1024
internal const val DISK_IMAGE_CACHE_MAX_SIZE_BYTES = 256L * 1024 * 1024
internal const val DISK_IMAGE_CACHE_DIR = "image_cache"

fun setupImageLoader() {
    SingletonImageLoader.setSafe { context -> createImageLoader(context) }
}

internal expect fun createImageLoader(context: PlatformContext): ImageLoader
