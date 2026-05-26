package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Suppress("ModifierMissing")
@Composable
fun D2BuildHelperTheme(content: @Composable () -> Unit) {
    val colors = getDarkColorScheme()
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = colors.tintColor,
            onPrimary = colors.textPrimary,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.outline,
            error = colors.tintColor, // no dedicated error token, temporary
            onError = colors.textPrimary,
        ),
    ) {
        CompositionLocalProvider(
            LocalD2BuildHelperColorScheme provides colors,
            LocalD2BuildHelperTypography provides defaultTypography(),
            LocalD2BuildHelperShapes provides D2BuildHelperShapes(),
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = colors.tintColor,
                backgroundColor = colors.tintColor.copy(alpha = 0.4f),
            ),
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
