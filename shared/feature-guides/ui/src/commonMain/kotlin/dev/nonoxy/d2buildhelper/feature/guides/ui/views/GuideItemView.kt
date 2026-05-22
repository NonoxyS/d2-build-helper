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
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.LocalImageLoader
import dev.nonoxy.d2buildhelper.common.utils.TimeConverter
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.ImageResources
import dev.nonoxy.d2buildhelper.core.domain.Item
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.ItemPurchase
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.PlayerStats
import dev.nonoxy.d2buildhelper.common.ui.theme.D2BuildHelperTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun GuideItemView(
    guide: Guide,
    imageResources: ImageResources
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = D2BuildHelperTheme.colors.primaryContainer,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = D2BuildHelperTheme.colors.outline,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroNameRow(
            hero = guide.hero,
            heroImageUrl = imageResources.heroImages[guide.hero] ?: "",
            position = guide.playerStats.position
        )
        MatchStatsRow(
            guide = guide,
            isRadiant = guide.playerStats.isRadiant
        )
        ItemRow(
            guide = guide,
            itemImageUrls = imageResources.itemImages
        )
    }
}

@Composable
private fun HeroNameRow(
    hero: Hero,
    heroImageUrl: String,
    position: MatchPlayerPosition
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val positionIcon = position.iconResource()
        if (positionIcon != null) {
            Image(
                painter = painterResource(positionIcon),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(18.dp))
        }

        AsyncImage(
            model = heroImageUrl,
            contentDescription = null,
            imageLoader = LocalImageLoader.current,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = hero.displayName,
            color = D2BuildHelperTheme.colors.primaryText,
            style = D2BuildHelperTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun MatchStatsRow(
    guide: Guide,
    isRadiant: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = TimeConverter.convertSecondsToMinutesAndSeconds(guide.durationSeconds),
            color = D2BuildHelperTheme.colors.primaryText,
            style = D2BuildHelperTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            painter = painterResource(
                if (isRadiant) MR.images.radiant_square else MR.images.dire_square
            ),
            contentDescription = null,
            modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = guide.playerStats.kills.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.primaryText,
                style = D2BuildHelperTheme.typography.bodySmall,
                modifier = Modifier.widthIn(min = 15.dp)
            )

            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.primaryText.copy(alpha = .36f),
                style = D2BuildHelperTheme.typography.bodySmall
            )

            Text(
                text = guide.playerStats.deaths.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.primaryText,
                style = D2BuildHelperTheme.typography.bodySmall,
                modifier = Modifier.widthIn(min = 15.dp)
            )

            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.primaryText.copy(alpha = .36f),
                style = D2BuildHelperTheme.typography.bodySmall
            )

            Text(
                text = guide.playerStats.assists.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.primaryText,
                style = D2BuildHelperTheme.typography.bodySmall,
                modifier = Modifier.widthIn(min = 15.dp)
            )
        }

        Spacer(modifier = Modifier.width(48.dp))

        Text(
            text = "+${guide.playerStats.impact}",
            color = D2BuildHelperTheme.colors.primaryText,
            style = D2BuildHelperTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.width(4.dp))

        LinearProgressIndicator(
            progress = { guide.playerStats.impact.div(50f) },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .height(8.dp)
                .clip(RoundedCornerShape(2.dp)),
        )
    }
}

@Composable
private fun ItemRow(
    guide: Guide,
    itemImageUrls: Map<Item, String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0..5) {
            val item = guide.playerStats.sortedEndItemPurchases.getOrNull(i)
            ItemWithBuyTime(
                itemPurchase = item,
                itemImageUrl = item?.itemId?.let { itemImageUrls.findByItemId(it) } ?: ""
            )
        }
        AsyncImage(
            model = guide.playerStats.endNeutralItemId?.let { itemImageUrls.findByItemId(it) },
            contentDescription = null,
            imageLoader = LocalImageLoader.current,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = D2BuildHelperTheme.colors.outline,
                    shape = CircleShape
                ).clip(CircleShape)
        )
    }
}

private fun Map<Item, String>.findByItemId(id: Short): String? =
    entries.firstOrNull { it.key.id == id }?.value

private fun MatchPlayerPosition.iconResource(): ImageResource? = when (this) {
    MatchPlayerPosition.POSITION_1 -> MR.images.position_1
    MatchPlayerPosition.POSITION_2 -> MR.images.position_2
    MatchPlayerPosition.POSITION_3 -> MR.images.position_3
    MatchPlayerPosition.POSITION_4 -> MR.images.position_4
    MatchPlayerPosition.POSITION_5 -> MR.images.position_5
    MatchPlayerPosition.UNKNOWN,
    MatchPlayerPosition.FILTERED,
    MatchPlayerPosition.ALL -> null
}

@Composable
private fun ItemWithBuyTime(
    itemPurchase: ItemPurchase?,
    itemImageUrl: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = itemImageUrl,
            contentDescription = null,
            imageLoader = LocalImageLoader.current,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(36.dp)
                .height(28.dp)
                .background(
                    color = D2BuildHelperTheme.colors.outline,
                    shape = RoundedCornerShape(4.dp)
                ).clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = itemPurchase?.let { itemPurchase ->
                TimeConverter.convertSecondsToMinutesAndSeconds(itemPurchase.time)
            } ?: "",
            color = D2BuildHelperTheme.colors.primaryText,
            style = D2BuildHelperTheme.typography.bodySmall,
        )
    }
}

@Composable
@Preview
private fun GuideItemView_Preview() {
    D2BuildHelperTheme {
        GuideItemView(
            guide = Guide(
                hero = Hero(
                    heroId = 1,
                    shortName = "antimage",
                    displayName = "Anti-Mage"
                ),
                steamAccountId = 76561197960287930,
                matchId = 1234567890,
                durationSeconds = 3600,
                playerStats = PlayerStats(
                    position = MatchPlayerPosition.POSITION_1,
                    isRadiant = true,
                    kills = 10,
                    deaths = 2,
                    assists = 8,
                    impact = 38,
                    endNeutralItemId = 10,
                    sortedEndItemPurchases = listOf(
                        ItemPurchase(itemId = 1, time = 0),
                        ItemPurchase(itemId = 2, time = 600)
                    )
                )
            ),
            imageResources = ImageResources(
                heroImages = mapOf(
                    Hero(
                        heroId = 1,
                        shortName = "antimage",
                        "Anti-Mage"
                    ) to "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/icons/antimage.png"
                ),
                itemImages = emptyMap(),
                abilityImages = emptyMap(),
            )
        )
    }
}
