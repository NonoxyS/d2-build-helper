package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading.D2LoadingView
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.common.ui.compose.utils.navigationBarHeight
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuide
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged

private const val PREFETCH_THRESHOLD = 5
private val LIST_SPACING = 16.dp
private val LIST_HORIZONTAL_PADDING = 16.dp
private val LIST_BOTTOM_EXTRA_PADDING = 16.dp
private val LOAD_MORE_PADDING = 16.dp

@Composable
internal fun GuideListView(
    guides: ImmutableList<UiGuide>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    isLoadMoreError: Boolean,
    onLoadMore: () -> Unit,
    onGuideClick: (matchId: Long, steamAccountId: Long) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val currentIsLoadingMore by rememberUpdatedState(isLoadingMore)

    LaunchedEffect(listState, canLoadMore) {
        if (!canLoadMore) return@LaunchedEffect
        snapshotFlow {
            val info = listState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: -1) to info.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (last, total) ->
                if (total > 0 && last >= total - PREFETCH_THRESHOLD && !currentIsLoadingMore) {
                    currentOnLoadMore()
                }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(LIST_SPACING),
        contentPadding = PaddingValues(
            start = LIST_HORIZONTAL_PADDING,
            end = LIST_HORIZONTAL_PADDING,
            bottom = navigationBarHeight + LIST_BOTTOM_EXTRA_PADDING,
        ),
    ) {
        items(
            items = guides,
            key = { guide -> guide.matchId to guide.steamAccountId },
        ) { guide ->
            GuideItemView(
                guide = guide,
                onClick = { onGuideClick(guide.matchId, guide.steamAccountId) },
            )
        }

        if (isLoadingMore) {
            item(key = "load_more_spinner") {
                D2LoadingView(modifier = Modifier.fillMaxWidth().padding(LOAD_MORE_PADDING))
            }
        }

        if (isLoadMoreError) {
            item(key = "load_more_retry") {
                TextButton(
                    onClick = { currentOnLoadMore() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = D2BuildHelperTheme.colors.tintColor,
                    ),
                ) {
                    Text(
                        text = stringResource(MR.strings.load_more_failed),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
