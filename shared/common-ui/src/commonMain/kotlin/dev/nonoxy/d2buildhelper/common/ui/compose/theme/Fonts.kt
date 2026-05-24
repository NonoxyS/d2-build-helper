package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.compose.asFont
import dev.nonoxy.d2buildhelper.common.resources.MR

val fontNotoSans: FontFamily
    @Composable get() = FontFamily(
        MR.fonts.notosans_regular.asFont(weight = FontWeight.Normal).requireNotNull(),
        MR.fonts.notosans_medium.asFont(weight = FontWeight.Medium).requireNotNull(),
        MR.fonts.notosans_bold.asFont(weight = FontWeight.Bold).requireNotNull(),
    )

private fun Font?.requireNotNull(): Font = requireNotNull(this)
