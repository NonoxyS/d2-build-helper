package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildEntry
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildPhase
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildSection

private val ITEM_WIDTH = 36.dp
private val ITEM_HEIGHT = 28.dp
private val NEUTRAL_SIZE = 26.dp

@Composable
internal fun ItemBuildCard(
    itemBuild: UiItemBuild,
    modifier: Modifier = Modifier,
) {
    var explained by remember { mutableStateOf<UiItemBuildEntry?>(null) }

    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_item_build))

        Column {
            itemBuild.sections.fastForEachIndexed { sectionIndex, section ->
                if (sectionIndex > 0) {
                    Space8()
                }
                ItemBuildSection(
                    section = section,
                    onItemClick = { entry -> explained = entry },
                )
            }

            itemBuild.neutralItem?.let { neutral ->
                Space8()

                NeutralItem(entry = neutral, onClick = { explained = neutral })
            }
        }
    }

    explained?.let { entry ->
        val fallbackTitle = stringResource(MR.strings.guide_detail_card_item_build)
        ExplainPopup(
            title = entry.name?.takeIf { it.isNotBlank() } ?: fallbackTitle,
            body = entry.timeText,
            onDismissRequest = { explained = null },
        )
    }
}

@Composable
private fun UiItemBuildPhase.toTitle(): String = stringResource(
    when (this) {
        UiItemBuildPhase.LANING -> MR.strings.guide_detail_phase_laning
        UiItemBuildPhase.MID_GAME -> MR.strings.guide_detail_phase_mid
        UiItemBuildPhase.LATE_GAME -> MR.strings.guide_detail_phase_late
    },
)

@Composable
private fun ItemBuildSection(
    section: UiItemBuildSection,
    onItemClick: (UiItemBuildEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = section.phase.toTitle(),
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
        Space4()

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.Bottom,
        ) {
            section.entries.fastForEachIndexed { index, entry ->
                if (index > 0) {
                    ArrowSeparator()
                }
                ItemWithTime(entry = entry, onClick = { onItemClick(entry) })
            }
        }
    }
}

@Composable
private fun ItemWithTime(
    entry: UiItemBuildEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = entry.iconUrl?.raw,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(ITEM_WIDTH)
                .height(ITEM_HEIGHT)
                .background(
                    color = D2BuildHelperTheme.colors.surfaceVariant,
                    shape = D2BuildHelperTheme.shapes.cornerRadius4,
                )
                .clip(D2BuildHelperTheme.shapes.cornerRadius4)
                .clickable(onClick = onClick),
        )
        Space4()

        Text(
            text = entry.timeText,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
    }
}

@Composable
private fun NeutralItem(
    entry: UiItemBuildEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = entry.iconUrl?.raw,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(NEUTRAL_SIZE)
            .background(color = D2BuildHelperTheme.colors.surfaceVariant, shape = CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
    )
}

@Composable
private fun ArrowSeparator(modifier: Modifier = Modifier) {
    Text(
        text = "›",
        color = D2BuildHelperTheme.colors.outline,
        style = D2BuildHelperTheme.typography.bodyLG,
        modifier = modifier.padding(horizontal = 4.dp),
    )
}
