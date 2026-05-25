package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun GuideListView(guides: ImmutableList<UiGuide>) {
    val bottomInset = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = bottomInset + 16.dp),
    ) {
        items(items = guides, key = { it.matchId to it.steamAccountId }) { guide ->
            GuideItemView(guide = guide)
        }
    }
}
