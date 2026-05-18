package dev.nonoxy.d2buildhelper.core.presentation.mvikotlin

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseExecutor<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext = mainContext) {

    final override fun executeAction(action: Action) {
        scope.launch {
            suspendExecuteAction(action)
        }
    }

    final override fun executeIntent(intent: Intent) {
        scope.launch {
            suspendExecuteIntent(intent)
        }
    }

    open suspend fun suspendExecuteIntent(intent: Intent) {
        // no-op
    }

    open suspend fun suspendExecuteAction(action: Action) {
        // no-op
    }
}
