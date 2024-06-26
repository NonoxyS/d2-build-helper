package dev.nonoxy.d2buildhelper.features.guides.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nonoxy.d2buildhelper.common.topbar.GuidesTopBar
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun GuidesView(
    viewState: GuidesViewState.Display,
    eventHandler: (GuidesEvent) -> Unit
) {
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize()) {
        GuidesTopBar()
    }
}

@Composable
@Preview()
private fun GuidesView_Preview() {
    D2BuildHelperTheme {
        Surface {

        }
    }
}