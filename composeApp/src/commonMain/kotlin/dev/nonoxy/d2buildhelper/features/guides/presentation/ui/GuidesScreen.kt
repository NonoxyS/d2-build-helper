package dev.nonoxy.d2buildhelper.features.guides.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesErrorView
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesLoadingView
import dev.nonoxy.d2buildhelper.navigation.LocalNavHost

@Composable
fun GuidesScreen(
    guidesViewModel: GuidesViewModel = viewModel { GuidesViewModel() }
) {
    val externalNavHost = LocalNavHost.current
    val viewState by guidesViewModel.viewStates().collectAsState()
    val viewAction by guidesViewModel.viewActions().collectAsState(null)

    when (val currentState = viewState) {
        is GuidesViewState.Display -> {
            GuidesView(
                viewState = currentState,
                eventHandler = guidesViewModel::obtainEvent
            )
        }
        GuidesViewState.Loading -> GuidesLoadingView()
        GuidesViewState.Error -> GuidesErrorView()
    }
}