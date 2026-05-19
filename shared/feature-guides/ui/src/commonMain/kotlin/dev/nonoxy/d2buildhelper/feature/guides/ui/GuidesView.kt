package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.core.domain.ImageResources
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuidesTopBar
import dev.nonoxy.d2buildhelper.common.ui.theme.D2BuildHelperTheme

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onHeroSearchDialogClick: () -> Unit,
) {
    Column(
        modifier = Modifier.systemBarsPadding().fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GuidesTopBar(onSearchHeroClick = onHeroSearchDialogClick)
        val imageResources = state.imageResources
        if (imageResources != null) {
            GuideListView(
                guides = state.guides,
                imageResources = imageResources,
            )
        }
    }
}

@Composable
@Preview
private fun GuidesView_Preview() {
    D2BuildHelperTheme {
        GuidesView(
            state = UiGuidesState(
                heroSearchValue = "",
                heroSearchFiltered = emptyMap(),
                guides = emptyList(),
                imageResources = ImageResources(
                    heroImages = emptyMap(),
                    itemImages = emptyMap(),
                    abilityImages = emptyMap(),
                    additionalImages = emptyMap(),
                ),
                isLoading = false,
                isError = false,
            ),
            onHeroSearchDialogClick = {},
        )
    }
}
