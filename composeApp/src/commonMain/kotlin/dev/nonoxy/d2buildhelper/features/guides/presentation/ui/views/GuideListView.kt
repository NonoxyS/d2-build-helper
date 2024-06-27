package dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.domain.models.GuideUI

@Composable
internal fun GuideListView(
    guides: List<GuideUI>,
    imageResources: ImageResources
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = guides, key = { it.matchId }) { guide ->
            GuideItemView(
                guide = guide,
                imageResources = imageResources
            )
        }
    }
}