package com.nonoxy.d2buildhelper.presentation.hero_guides

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nonoxy.d2buildhelper.domain.model.GuideInfo
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.InventoryChange
import com.nonoxy.d2buildhelper.domain.model.ItemPurchase
import com.nonoxy.d2buildhelper.presentation.filterview.HeroFilterDialog
import com.nonoxy.d2buildhelper.presentation.filterview.HeroFilterViewModel
import com.nonoxy.d2buildhelper.presentation.guides.ItemsRow
import com.nonoxy.d2buildhelper.presentation.utils.TimeConverter

@Composable
fun HeroGuidesScreen(
    navController: NavController,
    buildsState: HeroGuidesScreenViewModel.BuildsState,
    heroFilterState: HeroFilterViewModel.HeroFilterState,
    onSelectHero: (Short) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (buildsState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                var expanded by remember { mutableStateOf(false) }
                Row(modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color(red = 255, green = 255, blue = 255, alpha = 14))
                    .clickable(
                        onClick = { expanded = !expanded }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Все герои",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC),
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                top = 9.dp,
                                bottom = 9.dp
                            )
                    )
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 1.dp,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 14)
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            tint = Color(red = 255, green = 255, blue = 255, alpha = 0xCC),
                            contentDescription = null,
                        )
                    }
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp
                        )
                ) {
                    Log.d("HGTestTime", "heroGuides first => ${buildsState.heroGuides.first()}")
                    items(buildsState.heroGuides.firstNotNullOf { it.guidesInfo }) { guide ->
                        val guideIterator = buildsState.heroGuides.firstNotNullOf {
                            it.guidesInfo?.indexOf(guide)
                        }
                        GuideItem(
                            heroId = buildsState.heroGuides.first().heroId,
                            guide = guide,
                            build = buildsState.heroBuilds.firstNotNullOf { it.value[guideIterator] },
                            itemImageUrls = buildsState.itemImageUrls,
                            heroImagesUrls = buildsState.heroImageUrls,
                            heroDetails = buildsState.heroDetails,
                            additionalImageUrls = buildsState.additionalImageUrls,
                            itemPurchases = buildsState.itemPurchases,
                            inventoryChanges = buildsState.inventoryChanges,
                            sortedBuildEndItemsByTime = buildsState.sortedBuildEndItemsByTime[guideIterator.toShort()]
                                ?: emptyList(),
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

                if (expanded) {
                    HeroFilterDialog(
                        heroFilterState = heroFilterState,
                        onDismiss = { expanded = false },
                        onItemClick = { clickedHero ->
                            Log.d("HeroFilter", "HGS | Clicked hero => $clickedHero")
                            onSelectHero(clickedHero)
                            expanded = false
                            navController.navigate("heroGuides/$clickedHero") {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun GuideItem(
    heroId: Short,
    guide: GuideInfo?,
    build: HeroGuideBuild?,
    itemImageUrls: MutableMap<Short, String>,
    heroImagesUrls: MutableMap<Short, String>,
    heroDetails: MutableMap<Short, MutableMap<String, String>>,
    additionalImageUrls: MutableMap<String, String>,
    itemPurchases: MutableMap<Short, List<ItemPurchase>>,
    inventoryChanges: MutableMap<Short, List<InventoryChange>>,
    sortedBuildEndItemsByTime: List<ItemPurchase>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        model = additionalImageUrls[build?.position?.name],
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    GlideImage(
                        model = heroImagesUrls[heroId],
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = heroDetails[heroId]?.get("displayName").toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                }

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = TimeConverter.convertSecondsToMinutesAndSeconds(
                            seconds = build?.durationSeconds ?: 0
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    GlideImage(
                        model =
                        if (build?.isRadiant == true) additionalImageUrls["radiant_square"]
                        else additionalImageUrls["dire_square"],
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = build?.kills.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "/",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0x5C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = build?.deaths.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "/",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0x5C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = build?.assists.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                    Spacer(modifier = Modifier.width(48.dp))

                    Text(
                        text = "+${build?.impact}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    LinearProgressIndicator(
                        progress = { build?.impact?.div(50f) ?: 20f },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(2.dp)),
                    )
                }
                ItemsRow(
                    heroId = heroId,
                    build = build,
                    itemImageUrls = itemImageUrls,
                    sortedBuildEndItemsByTime = sortedBuildEndItemsByTime
                )
            }
        }
    }
}
