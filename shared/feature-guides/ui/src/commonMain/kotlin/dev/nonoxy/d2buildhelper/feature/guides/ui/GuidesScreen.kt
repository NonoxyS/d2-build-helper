package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesLabel
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuidesErrorView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuidesLoadingView
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.HeroFilterDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GuidesScreen(vm: GuidesViewModel = koinViewModel()) {
    val state by vm.state.collectAsState()
    var showHeroDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.label.collect { label ->
            when (label) {
                UiGuidesLabel.ShowHeroSearchDialog -> showHeroDialog = true
            }
        }
    }

    when {
        state.isLoading -> GuidesLoadingView()
        state.isError -> GuidesErrorView(onRetry = vm::onRetry)
        else -> GuidesView(
            state = state,
            onHeroSearchDialogClick = vm::onHeroSearchDialogClick,
        )
    }

    if (showHeroDialog) {
        HeroFilterDialog(
            filteredHeroImageUrls = state.heroSearchFiltered,
            heroSearchValue = state.heroSearchValue,
            onSearchValueChanged = vm::onHeroSearchValueChange,
            onHeroSelect = {
                vm.onHeroSelect(it)
                showHeroDialog = false
            },
            onDismiss = { showHeroDialog = false },
        )
    }
}
