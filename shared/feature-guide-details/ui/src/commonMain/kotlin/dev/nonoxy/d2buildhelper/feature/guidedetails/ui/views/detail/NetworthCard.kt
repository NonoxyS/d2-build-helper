package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthMarker
import kotlinx.collections.immutable.ImmutableList

private val CHART_HEIGHT = 96.dp
private const val LINE_STROKE = 2.5f
private const val BASELINE_STROKE = 1f
private val MARKER_ICON_SIZE = 28.dp
private val CHART_INSET = 16.dp
private const val AREA_ALPHA = 0.28f

@Composable
internal fun NetworthCard(
    networth: UiNetworthCurve,
    modifier: Modifier = Modifier,
) {
    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_economy))

        NetworthChart(
            points = networth.points,
            markers = networth.markers,
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
    markers: ImmutableList<UiNetworthMarker>,
    modifier: Modifier = Modifier,
) {
    if (points.size < 2) return

    val lineColor = NETWORTH_LINE_COLOR
    val baselineColor = D2BuildHelperTheme.colors.outline
    val maxValue = (points.maxOrNull() ?: 0).coerceAtLeast(1)
    val stepFraction = 1f / (points.size - 1)

    BoxWithConstraints(modifier = modifier) {
        val plotWidth = maxWidth - CHART_INSET * 2
        val plotHeight = maxHeight - CHART_INSET * 2

        Canvas(modifier = Modifier.matchParentSize().padding(CHART_INSET)) {
            fun offsetAt(index: Int): Offset {
                val value = points.getOrElse(index) { 0 }
                val x = size.width * index * stepFraction
                val y = size.height - (value.toFloat() / maxValue) * size.height
                return Offset(x, y)
            }

            val line = Path().apply {
                moveTo(offsetAt(0).x, offsetAt(0).y)
                for (index in 1 until points.size) {
                    val offset = offsetAt(index)
                    lineTo(offset.x, offset.y)
                }
            }
            val area = Path().apply {
                addPath(line)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = area,
                brush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = AREA_ALPHA), lineColor.copy(alpha = 0f)),
                ),
            )
            drawLine(
                color = baselineColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = BASELINE_STROKE,
            )
            drawPath(path = line, color = lineColor, style = Stroke(width = LINE_STROKE))
        }

        markers.forEach { marker ->
            if (marker.minute !in points.indices) return@forEach
            val value = points[marker.minute]
            MarkerIcon(
                marker = marker,
                modifier = Modifier.offset(
                    x = CHART_INSET + plotWidth * (marker.minute * stepFraction) - MARKER_ICON_SIZE / 2,
                    y = CHART_INSET + plotHeight * (1f - value.toFloat() / maxValue) - MARKER_ICON_SIZE / 2,
                ),
            )
        }
    }
}

@Composable
private fun MarkerIcon(
    marker: UiNetworthMarker,
    modifier: Modifier = Modifier,
) {
    val fallbackTitle = stringResource(MR.strings.guide_detail_card_economy)
    ExplainAnchor(
        title = marker.name?.takeIf { it.isNotBlank() } ?: fallbackTitle,
        body = "${marker.minute}'",
        modifier = modifier,
    ) { onClick ->
        AsyncImage(
            model = marker.iconUrl?.raw,
            contentDescription = marker.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(MARKER_ICON_SIZE)
                .clip(D2BuildHelperTheme.shapes.cornerRadius4)
                .background(
                    color = D2BuildHelperTheme.colors.surfaceVariant,
                    shape = D2BuildHelperTheme.shapes.cornerRadius4,
                )
                .border(
                    width = 1.dp,
                    color = D2BuildHelperTheme.colors.outline,
                    shape = D2BuildHelperTheme.shapes.cornerRadius4,
                )
                .clickable(onClick = onClick),
        )
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
