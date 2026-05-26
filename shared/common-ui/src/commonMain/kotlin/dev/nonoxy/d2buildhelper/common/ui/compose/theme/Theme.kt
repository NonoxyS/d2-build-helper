package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Suppress("ModifierMissing")
@Composable
fun D2BuildHelperTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        CompositionLocalProvider(
            LocalD2BuildHelperColorScheme provides getDarkColorScheme(),
            LocalD2BuildHelperTypography provides defaultTypography(),
            LocalD2BuildHelperShapes provides D2BuildHelperShapes(),
        ) {
            ProvideTextStyle(
                value = D2BuildHelperTheme.typography.textMD.copy(
                    color = D2BuildHelperTheme.colors.textPrimary,
                ),
                content = content,
            )
        }
    }
}

object D2BuildHelperTheme {
    val colors: D2BuildHelperColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalD2BuildHelperColorScheme.current

    val typography: D2BuildHelperTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalD2BuildHelperTypography.current

    val shapes: D2BuildHelperShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalD2BuildHelperShapes.current
}
