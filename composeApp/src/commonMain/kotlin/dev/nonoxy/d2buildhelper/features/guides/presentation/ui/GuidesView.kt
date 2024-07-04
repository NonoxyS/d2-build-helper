package dev.nonoxy.d2buildhelper.features.guides.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.topbar.GuidesTopBar
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuideListView
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun GuidesView(
    viewState: GuidesViewState.Display,
    eventHandler: (GuidesEvent) -> Unit
) {
    Column(
        modifier = Modifier.systemBarsPadding().fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GuidesTopBar(
            onSearchHeroClick = { eventHandler(GuidesEvent.HeroSearchDialogClicked) }
        )
        GuideListView(
            guides = viewState.guides,
            imageResources = viewState.imageResources
        )
    }
}

@Composable
@Preview
private fun GuidesView_Preview() {
    D2BuildHelperTheme {
        GuidesView(
            viewState = GuidesViewState.Display(
                heroSearchValue = "",
                heroSearchFiltered = emptyMap(),
                guides = emptyList(),
                imageResources = ImageResources(
                    heroImages = emptyMap(),
                    itemImages = emptyMap(),
                    abilityImages = emptyMap(),
                    additionalImages = emptyMap(),
                )
            ),
            eventHandler = {}
        )
    }
}