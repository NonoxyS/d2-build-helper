package dev.nonoxy.d2buildhelper.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@Composable
internal fun D2BuildHelperTheme(
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState,
        LocalD2BuildHelperColor provides darkPalette,
        LocalD2BuildHelperTypography provides D2BuildHelperTypography(),
        content = {
            Box(modifier = Modifier.fillMaxSize().background(D2BuildHelperTheme.colors.primaryBackground)) {
                content.invoke()
            }
        }
    )
}

object D2BuildHelperTheme {
    val colors: D2BuildHelperColors
        @Composable
        get() = LocalD2BuildHelperColor.current
    val typography: D2BuildHelperTypography
        @Composable
        get() = LocalD2BuildHelperTypography.current
}

@Composable
internal expect fun SystemAppearance(isDark: Boolean)
