package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.error.D2ErrorView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading.D2LoadingView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.sheet.D2ModalBottomSheet
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.presentation.GuidesViewModel
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers.HeroPicker
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GuidesScreen(
    onGuideClick: (matchId: Long, steamAccountId: Long) -> Unit,
    viewModel: GuidesViewModel = koinViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
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
            onHeroClick = { viewModel.onFilterChipClick(GuidesFilterKind.Hero) },
            onHeroReset = { viewModel.onFilterReset(GuidesFilterKind.Hero) },
            onPositionToggle = viewModel::onPositionToggle,
            onSideToggle = viewModel::onSideToggle,
            onLoadMore = viewModel::onLoadMore,
            onRefresh = viewModel::onRefresh,
            onGuideClick = onGuideClick,
            modifier = Modifier.fillMaxSize(),
        )
    }

    state.heroPicker?.let { picker ->
        D2ModalBottomSheet(
            onDismissRequest = viewModel::onPickerDismiss,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.statusBars },
            modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp),
        ) {
            HeroPicker(
                picker = picker,
                onSearchChange = viewModel::onPickerSearchChange,
                onHeroClick = viewModel::onHeroSelected,
            )
        }
    }
}
