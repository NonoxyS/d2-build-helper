package dev.nonoxy.d2buildhelper.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dota_2_build_helper.composeapp.generated.resources.Res
import dota_2_build_helper.composeapp.generated.resources.noto_sans_bold
import dota_2_build_helper.composeapp.generated.resources.noto_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
fun NotoSansFontFamily() = FontFamily(
    Font(resource = Res.font.noto_sans_regular, weight = FontWeight.Normal),
    Font(resource = Res.font.noto_sans_bold, weight = FontWeight.Bold)
)

data class D2BuildHelperTypography(
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
)

@Composable
fun D2BuildHelperTypography() = D2BuildHelperTypography(
    bodyLarge = TextStyle(
        fontFamily = NotoSansFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.04.sp
    )
)

val LocalD2BuildHelperTypography =
    staticCompositionLocalOf<D2BuildHelperTypography> { error("No default implementation for fonts") }