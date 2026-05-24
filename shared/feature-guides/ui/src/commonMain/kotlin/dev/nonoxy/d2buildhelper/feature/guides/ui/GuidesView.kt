package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.domain.ImageResources
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onHeroSearchDialogClick: () -> Unit,
) {
    Column(
        modifier = Modifier.systemBarsPadding().fillMaxSize(),
    ) {
        D2TopBar(
            title = stringResource(MR.strings.all_heroes),
            modifier = Modifier.clickable(onClick = onHeroSearchDialogClick),
            trailing = {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    tint = D2BuildHelperTheme.colors.textPrimary,
                    contentDescription = null,
                )
            },
        )
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
                ),
                isLoading = false,
                isError = false,
            ),
            onHeroSearchDialogClick = {},
        )
    }
}
