package dev.nonoxy.d2buildhelper.theme

import androidx.compose.runtime.Composable
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

object D2BuildHelperTheme {
    val colors: D2BuildHelperColors
        @Composable
        get() = LocalD2BuildHelperColor.current
}

val LocalD2BuildHelperColor =
    staticCompositionLocalOf<D2BuildHelperColors> { error("No default implementation for colors") }
