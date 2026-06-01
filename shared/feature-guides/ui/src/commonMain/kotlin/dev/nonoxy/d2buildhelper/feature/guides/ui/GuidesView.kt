package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideFilterBar
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.HeroFilterChip

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onHeroClick: () -> Unit,
    onHeroReset: () -> Unit,
    onPositionToggle: (UiMatchPlayerPosition) -> Unit,
    onSideToggle: (Boolean) -> Unit,
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
            )

            GuideListView(guides = state.guides)
        }
    }
}
