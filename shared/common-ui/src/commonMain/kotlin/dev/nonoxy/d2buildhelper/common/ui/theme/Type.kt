package dev.nonoxy.d2buildhelper.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.asFont
import dev.nonoxy.d2buildhelper.common.resources.MR

@Composable
fun notoSansFontFamily(): FontFamily {
    val regular = MR.fonts.notosans_regular.asFont(weight = FontWeight.Normal)
    val bold = MR.fonts.notosans_bold.asFont(weight = FontWeight.Bold)
    return when {
        regular != null && bold != null -> FontFamily(regular, bold)
        regular != null -> FontFamily(regular)
        bold != null -> FontFamily(bold)
        else -> FontFamily.Default
    }
}

data class D2BuildHelperTypography(
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
)

@Composable
fun D2BuildHelperTypography() = D2BuildHelperTypography(
    bodyLarge = TextStyle(
        fontFamily = notoSansFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = notoSansFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = notoSansFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.04.sp
    )
)

val LocalD2BuildHelperTypography =
    staticCompositionLocalOf<D2BuildHelperTypography> { error("No default implementation for fonts") }
