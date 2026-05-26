package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.error.D2ErrorView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading.D2LoadingView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.sheet.D2ModalBottomSheet
import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.HeroPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.PositionPicker
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.SidePicker
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GuidesScreen(viewModel: GuidesViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when {
        state.isLoading && state.guides.isEmpty() -> D2LoadingView(modifier = Modifier.fillMaxSize())
        state.isError && state.guides.isEmpty() -> D2ErrorView(
            message = stringResource(MR.strings.error_loading),
            retryLabel = stringResource(MR.strings.retry),
            onRetry = viewModel::onRetry,
            modifier = Modifier.fillMaxSize(),
        )

        else -> GuidesView(
            state = state,
            onFilterChipClick = viewModel::onFilterChipClick,
            onFilterReset = viewModel::onFilterReset,
        )
    }

    state.activePicker?.let { picker ->
        D2ModalBottomSheet(
            onDismissRequest = viewModel::onPickerDismiss,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.statusBars },
            modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp),
        ) {
            when (picker) {
                is UiFilterPicker.Hero -> HeroPicker(
                    picker = picker,
                    onSearchChange = viewModel::onPickerSearchChange,
                    onHeroClick = viewModel::onHeroSelected,
                )

                is UiFilterPicker.Position -> PositionPicker(
                    picker = picker,
                    onPositionClick = viewModel::onPositionSelected,
                )

                is UiFilterPicker.Side -> SidePicker(
                    picker = picker,
                    onSideClick = viewModel::onSideSelected,
                )
            }
        }
    }
}
