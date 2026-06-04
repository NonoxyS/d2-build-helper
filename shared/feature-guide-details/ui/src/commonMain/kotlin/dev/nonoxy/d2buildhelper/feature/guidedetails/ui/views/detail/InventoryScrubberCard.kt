package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space6
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiInventorySnapshot
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiInventoryTimeline

private val SLOT_WIDTH = 30.dp
private val SLOT_HEIGHT = 26.dp
private val BACKPACK_WIDTH = 18.dp
private val BACKPACK_HEIGHT = 13.dp
private val NEUTRAL_SIZE = 26.dp

@Composable
internal fun InventoryScrubberCard(
    inventory: UiInventoryTimeline,
    modifier: Modifier = Modifier,
) {
    val lastIndex = (inventory.snapshots.size - 1).coerceAtLeast(0)
    var selectedMinute by remember(inventory) { mutableStateOf(lastIndex) }
    val snapshot = inventory.snapshots.getOrNull(selectedMinute)

    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_inventory))

        snapshot?.let { InventoryGrid(snapshot = it) }
        Space8()

        if (lastIndex > 0) {
            Scrubber(
                selectedMinute = selectedMinute,
                lastMinute = lastIndex,
                onMinuteChange = { selectedMinute = it },
            )
        } else {
            Text(
                text = formatMinute(0),
                color = D2BuildHelperTheme.colors.textPrimary,
                style = D2BuildHelperTheme.typography.textMD,
            )
        }
    }
}

@Composable
private fun InventoryGrid(
    snapshot: UiInventorySnapshot,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(2) { rowIndex ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { colIndex ->
                        val url = snapshot.itemIconUrls.getOrNull(rowIndex * 3 + colIndex)
                        ItemSlot(iconUrl = url, width = SLOT_WIDTH, height = SLOT_HEIGHT)
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                val url = snapshot.backpackIconUrls.getOrNull(index)
                ItemSlot(iconUrl = url, width = BACKPACK_WIDTH, height = BACKPACK_HEIGHT)
            }
        }

        NeutralSlot(iconUrl = snapshot.neutralIconUrl)
    }
}

@Composable
private fun ItemSlot(
    iconUrl: ImageUrl?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = iconUrl?.raw,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                color = D2BuildHelperTheme.colors.surfaceVariant,
                shape = D2BuildHelperTheme.shapes.cornerRadius4,
            )
            .clip(D2BuildHelperTheme.shapes.cornerRadius4),
    )
}

@Composable
private fun NeutralSlot(
    iconUrl: ImageUrl?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = iconUrl?.raw,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(NEUTRAL_SIZE)
            .background(color = D2BuildHelperTheme.colors.surfaceVariant, shape = CircleShape)
            .clip(CircleShape),
    )
}

@Composable
private fun Scrubber(
    selectedMinute: Int,
    lastMinute: Int,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = formatMinute(selectedMinute),
            color = D2BuildHelperTheme.colors.textPrimary,
            style = D2BuildHelperTheme.typography.textMD,
        )
        Space6()

        Slider(
            value = selectedMinute.toFloat(),
            onValueChange = { onMinuteChange(it.toInt().coerceIn(0, lastMinute)) },
            valueRange = 0f..lastMinute.toFloat(),
            steps = (lastMinute - 1).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = D2BuildHelperTheme.colors.textPrimary,
                activeTrackColor = D2BuildHelperTheme.colors.tintColor,
                inactiveTrackColor = D2BuildHelperTheme.colors.surfaceVariant,
                activeTickColor = D2BuildHelperTheme.colors.tintColor,
                inactiveTickColor = D2BuildHelperTheme.colors.surfaceVariant,
            ),
            modifier = Modifier.weight(1f),
        )
        Space6()

        Text(
            text = formatMinute(lastMinute),
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
    }
}

private fun formatMinute(minute: Int): String {
    val mm = minute.toString().padStart(2, '0')
    return "$mm:00"
}
