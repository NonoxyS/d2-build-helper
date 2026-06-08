package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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

private val CHART_HEIGHT = 86.dp
private const val LINE_STROKE = 2f
private const val MARKER_RADIUS = 4f
private val MARKER_ICON_SIZE = 28.dp

@Composable
internal fun NetworthCard(
    networth: UiNetworthCurve,
    modifier: Modifier = Modifier,
) {
    var explained by remember { mutableStateOf<UiNetworthMarker?>(null) }

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

        if (networth.markers.isNotEmpty()) {
            Space8()
            MarkerLegend(
                markers = networth.markers,
                onMarkerClick = { marker -> explained = marker },
            )
        }

        Space8()

        StatsRow(networth = networth)
    }

    explained?.let { marker ->
        val fallbackTitle = stringResource(MR.strings.guide_detail_card_economy)
        ExplainPopup(
            title = marker.name?.takeIf { it.isNotBlank() } ?: fallbackTitle,
            body = "${marker.minute}'",
            onDismissRequest = { explained = null },
        )
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

        markers.forEach { marker ->
            if (marker.minute in points.indices) {
                drawCircle(color = markerColor, radius = MARKER_RADIUS, center = pointOffset(marker.minute))
            }
        }
    }
}

@Composable
private fun MarkerLegend(
    markers: ImmutableList<UiNetworthMarker>,
    onMarkerClick: (UiNetworthMarker) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.Top,
    ) {
        markers.forEachIndexed { index, marker ->
            if (index > 0) {
                Space8()
            }
            MarkerItem(marker = marker, onClick = { onMarkerClick(marker) })
        }
    }
}

@Composable
private fun MarkerItem(
    marker: UiNetworthMarker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = marker.iconUrl?.raw,
            contentDescription = marker.name,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(MARKER_ICON_SIZE)
                .background(
                    color = D2BuildHelperTheme.colors.surfaceVariant,
                    shape = D2BuildHelperTheme.shapes.cornerRadius4,
                )
                .clip(D2BuildHelperTheme.shapes.cornerRadius4)
                .clickable(onClick = onClick),
        )
        Space4()

        Text(
            text = "${marker.minute}'",
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
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
