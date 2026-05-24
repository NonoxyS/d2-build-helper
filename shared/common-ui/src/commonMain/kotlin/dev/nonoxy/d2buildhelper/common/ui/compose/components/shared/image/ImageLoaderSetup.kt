package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader

fun setupImageLoader() {
    SingletonImageLoader.setSafe { context -> createImageLoader(context) }
}

internal expect fun createImageLoader(context: PlatformContext): ImageLoader
