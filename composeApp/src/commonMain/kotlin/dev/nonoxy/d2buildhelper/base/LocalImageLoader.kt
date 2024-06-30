package dev.nonoxy.d2buildhelper.base

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

val LocalImageLoader =
    staticCompositionLocalOf<ImageLoader> { error("No default implementation for image loader") }