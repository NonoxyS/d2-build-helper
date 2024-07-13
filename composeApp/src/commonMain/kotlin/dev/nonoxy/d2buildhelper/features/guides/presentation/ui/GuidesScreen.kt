package dev.nonoxy.d2buildhelper.features.guides.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesAction
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesEvent
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesErrorView
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.GuidesLoadingView
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.views.HeroFilterDialog
import dev.nonoxy.d2buildhelper.navigation.LocalNavHost

@Composable
internal fun GuidesScreen(
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

    when (viewAction) {
        GuidesAction.ShowHeroSearchDialog -> {
            val currentState = viewState as? GuidesViewState.Display
            var isShowHeroSelectDialog by rememberSaveable { mutableStateOf(true) }
            if (isShowHeroSelectDialog) {
                HeroFilterDialog(
                    filteredHeroImageUrls = currentState?.heroSearchFiltered ?: emptyMap(),
                    heroSearchValue = currentState?.heroSearchValue ?: "",
                    onSearchValueChanged = {
                        guidesViewModel.obtainEvent(GuidesEvent.HeroSearchValueChanged(it))
                    },
                    onDismiss = { isShowHeroSelectDialog = false },
                    onHeroSelect = { guidesViewModel.obtainEvent(GuidesEvent.SelectHeroInSearchDialog(it)) }
                )
            } else guidesViewModel.clearAction()
        }

        null -> {}
    }
}