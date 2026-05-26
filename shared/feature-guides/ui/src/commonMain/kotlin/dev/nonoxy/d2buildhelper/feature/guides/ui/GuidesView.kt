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
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideFilterChipsView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onFilterChipClick: (GuidesFilterKind) -> Unit,
    onFilterReset: (GuidesFilterKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = D2BuildHelperTheme.colors.background,
        topBar = { D2TopBar(title = stringResource(MR.strings.all_heroes)) },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            GuideFilterChipsView(
                chips = state.filterChips,
                onFilterChipClick = onFilterChipClick,
                onFilterReset = onFilterReset,
            )

            GuideListView(guides = state.guides)
        }
    }
}
