package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.ImageResources

@Composable
internal fun GuideListView(
    guides: List<Guide>,
    imageResources: ImageResources
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = guides, key = { Pair(it.matchId, it.steamAccountId) }) { guide ->
            GuideItemView(
                guide = guide,
                imageResources = imageResources
            )
        }
    }
}
