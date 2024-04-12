@file:OptIn(ExperimentalGlideComposeApi::class)

package com.nonoxy.d2buildhelper.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.model.InventoryChange
import com.nonoxy.d2buildhelper.domain.model.ItemPurchase

@Composable
fun GuidesScreen(
    state: GuidesScreenViewModel.BuildsState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
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
                items(state.guides) { guide ->
                    GuideItem(
                        guide = guide,
                        build = state.heroBuilds[guide.heroId],
                        itemImageUrls = state.itemImageUrls,
                        heroImagesUrls = state.heroImageUrls,
                        heroNames = state.heroNames,
                        additionalImageUrls = state.additionalImageUrls,
                        itemPurchases = state.itemPurchases,
                        inventoryChanges = state.inventoryChanges,
                        sortedBuildEndItemsByTime = state.sortedBuildEndItemsByTime,
                        modifier = Modifier
                            .fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun GuideItem(
    guide: HeroGuideInfo,
    build: HeroGuideBuild?,
    itemImageUrls: MutableMap<Short, String>,
    heroImagesUrls: MutableMap<Short, String>,
    heroNames: MutableMap<Short, MutableMap<String, String>>,
    additionalImageUrls: MutableMap<String, String>,
    itemPurchases: MutableMap<Short, List<ItemPurchase>>,
    inventoryChanges: MutableMap<Short, List<InventoryChange>>,
    sortedBuildEndItemsByTime: MutableMap<Short, List<ItemPurchase>>,
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
                        model = heroImagesUrls[guide.heroId],
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = heroNames[guide.heroId]?.get("displayName").toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
                    )
                }

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = convertSecondsToMinutesAndSeconds(
                            seconds = build?.durationSeconds?: 0),
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

                ShowItemsRow(
                    guide.heroId,
                    build = build,
                    itemImageUrls = itemImageUrls,
                    sortedBuildEndItemsByTime
                )
            }
        }
    }
}

@Composable
fun ShowItemsRow(
    heroId: Short,
    build: HeroGuideBuild?,
    itemImageUrls: MutableMap<Short, String>,
    sortedBuildEndItemsByTime: MutableMap<Short, List<ItemPurchase>>
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(0) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(0)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(0) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }
            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(0) { null }?.time?: -404), // -404 - No item
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(1) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(1)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(1) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }
            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(1) { null }?.time?: -404), // -404 - No item
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(2) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(2)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(2) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }

            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(2) { null }?.time?: -404), // -404 - No item
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(3) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(3)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(3) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }
            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(3) { null }?.time?: -404), // -404 - No item
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(4) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(4)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(4) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }
            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(4) { null }?.time?: -404), // -404 - No item
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sortedBuildEndItemsByTime[heroId]?.getOrElse(5) { null } != null) {
                GlideImage(
                    model = itemImageUrls[
                        sortedBuildEndItemsByTime[heroId]?.get(5)?.itemId?.toShort()],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if ((sortedBuildEndItemsByTime[heroId]?.getOrElse(5) { null } == null)
                and (listOf(
                    build?.endItem0Id,
                    build?.endItem1Id,
                    build?.endItem2Id,
                    build?.endItem3Id,
                    build?.endItem4Id,
                    build?.endItem5Id).contains(117))) // 117 - Aegis
            {
                GlideImage(
                    model = itemImageUrls[117],
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(modifier = Modifier
                    .width(36.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.outline))
            }
            Text(
                text = convertSecondsToMinutesAndSeconds(
                    sortedBuildEndItemsByTime[heroId]
                        ?.getOrElse(5) { null }?.time?: -404), // -404 - No item or Aegis
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
            )
        }

        if (itemImageUrls[build?.endNeutralItemId] != "null") {
            GlideImage(
                model = itemImageUrls[build?.endNeutralItemId],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline))
        }
    }
}

fun convertSecondsToMinutesAndSeconds(seconds: Int): String =
    if (seconds > 0)
        "${ if (seconds / 60 >= 10) seconds / 60 else "0${seconds / 60}" }:" +
                "${ if (seconds % 60 >= 10) seconds % 60 else "0${seconds % 60}" }"
    else if (seconds == -404) ""
    else "00:00"
