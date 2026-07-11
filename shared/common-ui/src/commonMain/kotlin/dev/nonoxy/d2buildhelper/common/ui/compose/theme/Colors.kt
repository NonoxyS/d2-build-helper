package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.compose.colorResource
import dev.nonoxy.d2buildhelper.common.resources.MR

@Suppress("CompositionLocalAllowlist")
internal val LocalD2BuildHelperColorScheme = staticCompositionLocalOf<D2BuildHelperColorScheme> {
    error("CompositionLocal LocalD2BuildHelperColorScheme was not provided")
}

@Immutable
class D2BuildHelperColorScheme internal constructor(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val tintColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val outline: Color,
    val positive: Color,
    val negative: Color,
    val talentGold: Color,
    val talentEmpty: Color,
    val purchaseMarker: Color,
    val abilityQ: Color,
    val abilityW: Color,
    val abilityE: Color,
    val abilityR: Color,
)

@Composable
internal fun getDarkColorScheme(): D2BuildHelperColorScheme = D2BuildHelperColorScheme(
    background = colorResource(MR.colors.background),
    surface = colorResource(MR.colors.surface),
    surfaceVariant = colorResource(MR.colors.surfaceVariant),
    tintColor = colorResource(MR.colors.tintColor),
    textPrimary = colorResource(MR.colors.textPrimary),
    textSecondary = colorResource(MR.colors.textSecondary),
    outline = colorResource(MR.colors.outline),
    positive = colorResource(MR.colors.positive),
    negative = colorResource(MR.colors.negative),
    talentGold = colorResource(MR.colors.talentGold),
    talentEmpty = colorResource(MR.colors.talentEmpty),
    purchaseMarker = colorResource(MR.colors.purchaseMarker),
    abilityQ = colorResource(MR.colors.abilityQ),
    abilityW = colorResource(MR.colors.abilityW),
    abilityE = colorResource(MR.colors.abilityE),
    abilityR = colorResource(MR.colors.abilityR),
)
