package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import kotlinx.collections.immutable.ImmutableList

private val CHART_HEIGHT = 86.dp
private const val LINE_STROKE = 2f
private const val MARKER_RADIUS = 4f

@Composable
internal fun NetworthCard(
    networth: UiNetworthCurve,
    modifier: Modifier = Modifier,
) {
    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_economy))

        NetworthChart(
            points = networth.points,
            markerMinutes = networth.purchaseMarkerMinutes,
            modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT),
        )
        Space4()

        Text(
            text = stringResource(MR.strings.guide_detail_economy_caption),
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
        Space8()

        StatsRow(networth = networth)
    }
}

@Composable
private fun NetworthChart(
    points: ImmutableList<Int>,
    markerMinutes: ImmutableList<Int>,
    modifier: Modifier = Modifier,
) {
    if (points.size < 2) return

    val lineColor = NETWORTH_LINE_COLOR
    val markerColor = PURCHASE_MARKER_COLOR
    val maxValue = (points.maxOrNull() ?: 0).coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val stepX = size.width / (points.size - 1)
        fun pointOffset(index: Int): Offset {
            val value = points.getOrElse(index) { 0 }
            val x = stepX * index
            val y = size.height - (value.toFloat() / maxValue) * size.height
            return Offset(x, y)
        }

        val path = Path().apply {
            moveTo(pointOffset(0).x, pointOffset(0).y)
            for (index in 1 until points.size) {
                val offset = pointOffset(index)
                lineTo(offset.x, offset.y)
            }
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = LINE_STROKE))

        markerMinutes.forEach { minute ->
            if (minute in points.indices) {
                drawCircle(color = markerColor, radius = MARKER_RADIUS, center = pointOffset(minute))
            }
        }
    }
}

@Composable
private fun StatsRow(
    networth: UiNetworthCurve,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        StatColumn(value = networth.gpm?.toString(), label = stringResource(MR.strings.guide_detail_gpm))
        StatColumn(
            value = networth.networth?.let { formatNetworth(it) },
            label = stringResource(MR.strings.guide_detail_networth),
        )
        StatColumn(
            value = networth.lastHitsAt10?.toString(),
            label = stringResource(MR.strings.guide_detail_last_hits_at_10),
        )
    }
}

@Composable
private fun StatColumn(
    value: String?,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value ?: "—",
            color = D2BuildHelperTheme.colors.textPrimary,
            style = D2BuildHelperTheme.typography.bodyLG,
        )
        Text(
            text = label,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
    }
}

private const val THOUSAND = 1000

private fun formatNetworth(value: Int): String {
    if (value < THOUSAND) return value.toString()
    val thousands = value / THOUSAND
    val remainder = (value % THOUSAND) / 100
    return "$thousands.${remainder}k"
}
