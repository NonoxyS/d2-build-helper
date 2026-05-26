package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.utils.navigationBarHeight
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun GuideListView(
    guides: ImmutableList<UiGuide>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = navigationBarHeight + 16.dp
        ),
    ) {
        items(
            items = guides,
            key = { guide -> guide.matchId to guide.steamAccountId }
        ) { guide ->
            GuideItemView(guide = guide)
        }
    }
}
