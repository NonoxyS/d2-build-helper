package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.image.D2AsyncImage
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space16
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space48
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiItemPurchase

@Composable
internal fun GuideItemView(guide: UiGuide) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = D2BuildHelperTheme.colors.surface, shape = D2BuildHelperTheme.shapes.cornerRadius4)
            .border(
                width = 1.dp,
                color = D2BuildHelperTheme.colors.outline,
                shape = D2BuildHelperTheme.shapes.cornerRadius4,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeroNameRow(guide = guide)
        MatchStatsRow(guide = guide)
        ItemRow(guide = guide)
    }
}

@Composable
private fun HeroNameRow(guide: UiGuide) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val positionIcon = guide.position.iconResource
        if (positionIcon != null) {
            Image(
                painter = painterResource(positionIcon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        } else {
            Spacer(modifier = Modifier.size(18.dp))
        }
        D2AsyncImage(
            model = guide.hero.iconUrl,
            contentDescription = guide.hero.displayName,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = guide.hero.displayName,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.bodyLG,
        )
    }
}

@Composable
private fun MatchStatsRow(guide: UiGuide) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = guide.durationFormatted,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
        Space16()
        Image(
            painter = painterResource(
                if (guide.isRadiant) MR.images.radiant_square else MR.images.dire_square,
            ),
            contentDescription = null,
            modifier = Modifier.size(16.dp).clip(D2BuildHelperTheme.shapes.cornerRadius4),
        )
        Space16()
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = guide.kills.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
                modifier = Modifier.widthIn(min = 15.dp),
            )
            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.textSecondary.copy(alpha = .36f),
                style = D2BuildHelperTheme.typography.captionMD,
            )
            Text(
                text = guide.deaths.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
                modifier = Modifier.widthIn(min = 15.dp),
            )
            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.textSecondary.copy(alpha = .36f),
                style = D2BuildHelperTheme.typography.captionMD,
            )
            Text(
                text = guide.assists.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
                modifier = Modifier.widthIn(min = 15.dp),
            )
        }
        Space48()
        Text(
            text = guide.impactLabel,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
        Space4()
        LinearProgressIndicator(
            progress = { guide.impactProgress },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .height(8.dp)
                .clip(RoundedCornerShape(2.dp)),
        )
    }
}

@Composable
private fun ItemRow(guide: UiGuide) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        guide.items.forEach { purchase ->
            ItemWithBuyTime(purchase = purchase)
        }
        guide.neutralItem?.let { neutral ->
            D2AsyncImage(
                model = neutral.iconUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .background(color = D2BuildHelperTheme.colors.outline, shape = CircleShape)
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
private fun ItemWithBuyTime(purchase: UiItemPurchase) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        D2AsyncImage(
            model = purchase.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(36.dp)
                .height(28.dp)
                .background(
                    color = D2BuildHelperTheme.colors.outline,
                    shape = D2BuildHelperTheme.shapes.cornerRadius4,
                )
                .clip(D2BuildHelperTheme.shapes.cornerRadius4),
        )
        Space4()
        Text(
            text = purchase.timeFormatted,
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )
    }
}
