package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.refresh.D2PullToRefreshBox
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideFilterBar
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.HeroFilterChip

private val EMPTY_STATE_PADDING = 32.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onHeroClick: () -> Unit,
    onHeroReset: () -> Unit,
    onPositionToggle: (UiMatchPlayerPosition) -> Unit,
    onSideToggle: (Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = D2BuildHelperTheme.colors.background,
        topBar = {
            D2TopBar(
                leading = {
                    HeroFilterChip(
                        heroFilter = state.heroFilter,
                        onClick = onHeroClick,
                        onReset = onHeroReset,
                    )
                },
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            GuideFilterBar(
                selectedPosition = state.selectedPosition,
                selectedSide = state.selectedSide,
                onPositionToggle = onPositionToggle,
                onSideToggle = onSideToggle,
                sideFilterAvailable = state.isSideFilterAvailable,
            )

            D2PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.guides.isEmpty() && !state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(MR.strings.guides_empty),
                            color = D2BuildHelperTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(EMPTY_STATE_PADDING),
                        )
                    }
                } else {
                    GuideListView(
                        guides = state.guides,
                        canLoadMore = state.canLoadMore,
                        isLoadingMore = state.isLoadingMore,
                        isLoadMoreError = state.isLoadMoreError,
                        onLoadMore = onLoadMore,
                    )
                }
            }
        }
    }
}
