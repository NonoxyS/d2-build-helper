package dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import dev.nonoxy.d2buildhelper.common.utils.TimeConverter
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.guides.domain.models.*
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun GuideItemView(
    guide: GuideUI,
    imageResources: ImageResources
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(D2BuildHelperTheme.colors.primaryContainer)
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
            heroImageUrl = imageResources.heroImages[guide.hero.heroId] ?: "",
            positionImageUrl = imageResources.additionalImages[guide.playerStats.position.title] ?: ""
        )
        MatchStatsRow(
            guide = guide,
            sideImageUrl = imageResources.additionalImages[
                if (guide.playerStats.isRadiant) "radiant_square"
                else "dire_square"] ?: ""
        )
        
    }
}

@Composable
private fun HeroNameRow(
    hero: Hero,
    heroImageUrl: String,
    positionImageUrl: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = positionImageUrl,
            contentDescription = null,
            imageLoader = InjectProvider.getDependency(ImageLoader::class),
            modifier = Modifier.size(18.dp)
        )

        AsyncImage(
            model = heroImageUrl,
            contentDescription = null,
            imageLoader = InjectProvider.getDependency(ImageLoader::class),
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = hero.displayName,
            color = D2BuildHelperTheme.colors.primaryText
        )
    }
}

@Composable
private fun MatchStatsRow(
    guide: GuideUI,
    sideImageUrl: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = TimeConverter.convertSecondsToMinutesAndSeconds(guide.durationSeconds),
            color = D2BuildHelperTheme.colors.primaryText
        )

        Spacer(modifier = Modifier.width(16.dp))

        AsyncImage(
            model = sideImageUrl,
            contentDescription = null,
            imageLoader = InjectProvider.getDependency(ImageLoader::class),
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
                modifier = Modifier.widthIn(min = 15.dp)
            )

            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.primaryText.copy(alpha = .36f)
            )

            Text(
                text = guide.playerStats.deaths.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.primaryText,
                modifier = Modifier.widthIn(min = 15.dp)
            )

            Text(
                text = "/",
                color = D2BuildHelperTheme.colors.primaryText.copy(alpha = .36f)
            )

            Text(
                text = guide.playerStats.assists.toString(),
                textAlign = TextAlign.Center,
                color = D2BuildHelperTheme.colors.primaryText,
                modifier = Modifier.widthIn(min = 15.dp)
            )
        }

        Spacer(modifier = Modifier.width(48.dp))

        Text(
            text = "+${guide.playerStats.impact}",
            color = D2BuildHelperTheme.colors.primaryText
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
    guide: GuideUI,
    itemImageUrls: Map<Short, String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        
    }
}

@Composable
@Preview
private fun GuideItemView_Preview() {
    D2BuildHelperTheme {
        GuideItemView(
            guide = GuideUI(
                hero = Hero(
                    heroId = 1,
                    shortName = "antimage",
                    displayName = "Anti-Mage"
                ),
                steamAccountId = 76561197960287930,
                matchId = 1234567890,
                durationSeconds = 3600,
                playerStats = PlayerStatsUI(
                    position = MatchPlayerPositionType.POSITION_1,
                    isRadiant = true,
                    kills = 10,
                    deaths = 2,
                    assists = 8,
                    impact = 38,
                    endItem0Id = 1,
                    endItem1Id = 2,
                    endItem2Id = 3,
                    endItem3Id = 4,
                    endItem4Id = 5,
                    endItem5Id = 6,
                    endBackpack0Id = 7,
                    endBackpack1Id = 8,
                    endBackpack2Id = 9,
                    endNeutralItemId = 10,
                    sortedEndItemPurchases = listOf(
                        ItemPurchase(itemId = 1, time = 0),
                        ItemPurchase(itemId = 2, time = 600)
                    )
                )
            ),
            imageResources = ImageResources(
                heroImages = mapOf(
                    1.toShort() to "https://ojxuhaplumzopsbihjkf.supabase.co/storage/v1/object/public/d2bh_images/hero_icons/antimage_minimap_icon.png"
                ),
                itemImages = emptyMap(),
                abilityImages = emptyMap(),
                additionalImages = mapOf(
                    "POSITION_1" to "https://ojxuhaplumzopsbihjkf.supabase.co/storage/v1/object/public/d2bh_images/additional_icons/POSITION_1.png"
                )
            )
        )
    }
}