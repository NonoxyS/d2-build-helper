package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space2
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.common.ui.match.label
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiBuildHeader

private val HERO_ICON_SIZE = 54.dp
private val POSITION_ICON_SIZE = 16.dp

@Composable
internal fun BuildHeaderCard(
    header: UiBuildHeader,
    modifier: Modifier = Modifier,
) {
    DetailCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = header.heroIconUrl?.raw,
                contentDescription = header.heroName,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(D2BuildHelperTheme.colors.surfaceVariant),
                error = ColorPainter(D2BuildHelperTheme.colors.surfaceVariant),
                modifier = Modifier
                    .size(HERO_ICON_SIZE)
                    .clip(D2BuildHelperTheme.shapes.cornerRadius8),
            )
            Space8()

            Column(modifier = Modifier.weight(1f)) {
                HeroNameRow(header = header)
                Space2()

                MetaRow(header = header)
            }
            Space8()

            KdaColumn(header = header)
        }
    }
}

@Composable
private fun HeroNameRow(
    header: UiBuildHeader,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        header.position?.let { position ->
            Image(
                painter = painterResource(position.iconResource),
                contentDescription = null,
                modifier = Modifier.size(POSITION_ICON_SIZE),
            )
        }
        Text(
            text = header.heroName,
            color = D2BuildHelperTheme.colors.textPrimary,
            style = D2BuildHelperTheme.typography.bodyLG,
        )
        header.isVictory?.let { WinLossBadge(isVictory = it) }
    }
}

@Composable
private fun MetaRow(
    header: UiBuildHeader,
    modifier: Modifier = Modifier,
) {
    val side = header.isRadiant?.let { radiant ->
        stringResource(if (radiant) MR.strings.side_radiant else MR.strings.side_dire)
    }
    val parts = listOfNotNull(
        side,
        header.role?.label(),
        header.lane?.label(),
        header.durationText.takeIf { it.isNotBlank() },
        header.level?.let { "lvl $it" },
    )
    Text(
        text = parts.joinToString(separator = " · "),
        color = D2BuildHelperTheme.colors.textSecondary,
        style = D2BuildHelperTheme.typography.captionMD,
        modifier = modifier,
    )
}

@Composable
private fun KdaColumn(
    header: UiBuildHeader,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(
            text = "${header.kills ?: 0}/${header.deaths ?: 0}/${header.assists ?: 0}",
            color = D2BuildHelperTheme.colors.textPrimary,
            style = D2BuildHelperTheme.typography.bodyLG,
            textAlign = TextAlign.End,
        )
        Text(
            text = stringResource(MR.strings.guide_detail_kda),
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
    }
}

@Composable
private fun WinLossBadge(
    isVictory: Boolean,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(
        if (isVictory) MR.strings.guide_detail_win else MR.strings.guide_detail_loss,
    )
    val color = if (isVictory) WIN_COLOR else LOSS_COLOR
    Text(
        text = label,
        color = color,
        style = D2BuildHelperTheme.typography.captionMD,
        modifier = modifier
            .background(color = color.copy(alpha = 0.16f), shape = D2BuildHelperTheme.shapes.cornerRadius4)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
