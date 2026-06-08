package dev.nonoxy.d2buildhelper.feature.guidedetails.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.error.D2ErrorView
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading.D2LoadingView
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.GuideDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GuideDetailScreen(
    matchId: Long,
    steamAccountId: Long,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: GuideDetailViewModel = koinViewModel { parametersOf(matchId, steamAccountId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> D2LoadingView(modifier = modifier.fillMaxSize())
        state.isError -> D2ErrorView(
            message = stringResource(MR.strings.error_loading),
            retryLabel = stringResource(MR.strings.retry),
            onRetry = viewModel::onRetry,
            modifier = modifier.fillMaxSize(),
        )

        else -> GuideDetailView(
            state = state,
            onBack = onBack,
            modifier = modifier.fillMaxSize(),
        )
    }
}
