package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun ColorResource.asCompose(): Color = colorResource(this)

val Color.isLight: Boolean
    @Composable get() = when (val luminance = luminance()) {
        in 0f..0.5f -> false
        in 0.51f..1f -> true
        else -> error("Unreachable luminance $luminance")
    }
