package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@Composable
fun <T> Flow<T>.CollectFlow(collector: FlowCollector<T>) {
    LaunchedEffect(Unit) {
        collect(collector = collector)
    }
}

// https://twitter.github.io/compose-rules/rules/#ordering-composable-parameters-properly
@Suppress("ComposableParametersOrdering", "ComposableParamOrder")
@Composable
fun <T> Flow<T>.CollectFlowWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            collect(collector = collector)
        }
    }
}
