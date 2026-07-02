package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthMarker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private val CHART_PLOT_HEIGHT = 84.dp
private val MARKER_LANE_HEIGHT = 28.dp
private val CONNECTOR_HEIGHT = 10.dp
private const val LINE_STROKE = 2.5f
private const val BASELINE_STROKE = 1f
private const val CONNECTOR_STROKE = 1f
private val MARKER_ICON_SIZE = 24.dp
private val MARKER_GAP = 4.dp
private val CHART_INSET = 12.dp
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
            modifier = Modifier.fillMaxWidth(),
        )
        Space8()

        StatsRow(networth = networth)
    }
}

// Space marker icon centers left->right with a minimum gap so early clustered
// purchases don't overlap; connector lines re-point each icon at its true minute.
internal fun declusterCenters(trueCenters: List<Float>, minGap: Float, maxCenter: Float): List<Float> {
    if (trueCenters.isEmpty()) return emptyList()
    val out = FloatArray(trueCenters.size)
    var prev = -Float.MAX_VALUE
    for (i in trueCenters.indices) {
        val c = maxOf(trueCenters[i], prev + minGap)
        out[i] = c
        prev = c
    }
    if (out.last() > maxCenter) {
        var next = maxCenter
        for (i in out.indices.reversed()) {
            out[i] = minOf(out[i], next)
            next = out[i] - minGap
        }
    }
    return out.toList()
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
    val connectorColor = D2BuildHelperTheme.colors.outline
    val maxValue = (points.maxOrNull() ?: 0).coerceAtLeast(1)
    val lastIndex = points.size - 1
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val insetPx = with(density) { CHART_INSET.toPx() }
        val iconPx = with(density) { MARKER_ICON_SIZE.toPx() }
        val gapPx = with(density) { MARKER_GAP.toPx() }
        val fullWidthPx = with(density) { maxWidth.toPx() }
        val plotWidthPx = (fullWidthPx - insetPx * 2f).coerceAtLeast(1f)

        val visibleMarkers = markers.filter { it.minute in 0..lastIndex }
        val trueX = visibleMarkers.map { insetPx + plotWidthPx * (it.minute.toFloat() / lastIndex) }
        val laidCenters = declusterCenters(
            trueCenters = trueX,
            minGap = iconPx + gapPx,
            maxCenter = fullWidthPx - iconPx / 2f,
        )

        Column {
            MarkerLane(
                markers = visibleMarkers.toImmutableList(),
                centersPx = laidCenters.toImmutableList(),
                iconPx = iconPx,
            )

            Canvas(modifier = Modifier.fillMaxWidth().height(CONNECTOR_HEIGHT)) {
                laidCenters.forEachIndexed { index, center ->
                    drawLine(
                        color = connectorColor,
                        start = Offset(center, 0f),
                        end = Offset(trueX[index], size.height),
                        strokeWidth = CONNECTOR_STROKE,
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CHART_PLOT_HEIGHT)
                    .padding(horizontal = CHART_INSET),
            ) {
                fun offsetAt(index: Int): Offset {
                    val value = points.getOrElse(index) { 0 }
                    val x = size.width * index / lastIndex
                    val y = size.height - (value.toFloat() / maxValue) * size.height
                    return Offset(x, y)
                }

                val line = Path().apply {
                    moveTo(offsetAt(0).x, offsetAt(0).y)
                    for (index in 1..lastIndex) {
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
        }
    }
}

@Composable
private fun MarkerLane(
    markers: ImmutableList<UiNetworthMarker>,
    centersPx: ImmutableList<Float>,
    iconPx: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Box(modifier = modifier.fillMaxWidth().height(MARKER_LANE_HEIGHT)) {
        markers.forEachIndexed { index, marker ->
            val leftDp = with(density) { (centersPx[index] - iconPx / 2f).toDp() }
            MarkerIcon(
                marker = marker,
                modifier = Modifier.offset(x = leftDp),
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
        StatColumn(value = networth.xpm?.toString(), label = stringResource(MR.strings.guide_detail_xpm))
        StatColumn(
            value = networth.networth?.let { formatNetworth(it) },
            label = stringResource(MR.strings.guide_detail_networth),
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
