package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.error.D2ErrorView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading.D2LoadingView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.sheet.D2FilterSheet
import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.HeroPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.PositionPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.SidePicker
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GuidesScreen(vm: GuidesViewModel = koinViewModel()) {
    val state by vm.state.collectAsState()

    when {
        state.isLoading && state.guides.isEmpty() -> D2LoadingView()
        state.isError && state.guides.isEmpty() -> D2ErrorView(
            message = stringResource(MR.strings.error_loading),
            retryLabel = stringResource(MR.strings.retry),
            onRetry = vm::onRetry,
        )
        else -> GuidesView(
            state = state,
            onFilterChipClick = vm::onFilterChipClick,
            onFilterReset = vm::onFilterReset,
        )
    }

    state.activePicker?.let { picker ->
        D2FilterSheet(onDismissRequest = vm::onPickerDismiss) {
            when (picker) {
                is UiFilterPicker.Hero -> HeroPicker(
                    picker = picker,
                    onSearchChange = vm::onPickerSearchChange,
                    onHeroSelected = vm::onHeroSelected,
                )
                is UiFilterPicker.Position -> PositionPicker(
                    picker = picker,
                    onPositionSelected = vm::onPositionSelected,
                )
                is UiFilterPicker.Side -> SidePicker(
                    picker = picker,
                    onSideSelected = vm::onSideSelected,
                )
            }
        }
    }
}
