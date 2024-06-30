package dev.nonoxy.d2buildhelper.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class D2BuildHelperColors(
    val primaryText: Color,
    val primaryBackground: Color,
    val primaryContainer: Color,
    val secondaryText: Color,
    val secondaryBackground: Color,
    val tintColor: Color,
    val outline: Color,
)

val LocalD2BuildHelperColor =
    staticCompositionLocalOf<D2BuildHelperColors> { error("No default implementation for colors") }
