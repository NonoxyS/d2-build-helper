package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Suppress("CompositionLocalAllowlist")
internal val LocalD2BuildHelperTypography = staticCompositionLocalOf<D2BuildHelperTypography> {
    error("CompositionLocal LocalD2BuildHelperTypography was not provided")
}

@Immutable
class D2BuildHelperTypography internal constructor(
    val captionMD: TextStyle,
    val textMD: TextStyle,
    val bodyLG: TextStyle,
    val headlineMD: TextStyle,
)

@Composable
internal fun defaultTypography(): D2BuildHelperTypography = D2BuildHelperTypography(
    captionMD = TextStyle(
        fontFamily = fontNotoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.04.sp,
    ),
    textMD = TextStyle(
        fontFamily = fontNotoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLG = TextStyle(
        fontFamily = fontNotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    headlineMD = TextStyle(
        fontFamily = fontNotoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
)
