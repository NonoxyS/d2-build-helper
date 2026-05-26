@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.progress

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun D2LinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = D2BuildHelperTheme.colors.tintColor,
    trackColor: Color = D2BuildHelperTheme.colors.outline,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
    drawStopIndicator: DrawScope.() -> Unit = {
        ProgressIndicatorDefaults.drawStopIndicator(
            drawScope = this,
            stopSize = ProgressIndicatorDefaults.LinearTrackStopIndicatorSize,
            color = color,
            strokeCap = strokeCap,
        )
    },
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
        strokeCap = strokeCap,
        gapSize = gapSize,
        drawStopIndicator = drawStopIndicator,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun D2LinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = D2BuildHelperTheme.colors.tintColor,
    trackColor: Color = D2BuildHelperTheme.colors.outline,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
) {
    LinearProgressIndicator(
        modifier = modifier,
        color = color,
        trackColor = trackColor,
        strokeCap = strokeCap,
        gapSize = gapSize,
    )
}
