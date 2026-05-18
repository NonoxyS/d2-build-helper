package dev.nonoxy.d2buildhelper.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.BindingsBuilder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.nonoxy.d2buildhelper.common.utils.OneTimeEvent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<State, Label>(
    initialState: State,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel(), BaseIosViewModel<State, Label> {

    private val mutableState = MutableStateFlow(initialState).cMutableStateFlow()
    private val mutableLabel = OneTimeEvent<Label>()

    override val state: CStateFlow<State>
        get() = mutableState.cStateFlow()

    override val label: CFlow<Label>
        get() = mutableLabel.receiveAsFlow().cFlow()

    private var binder: Binder? = null

    private var binderIsStarted = false

    init {
        Napier.v("VM $this init")
    }

    protected open fun acceptState(state: State) {
        mutableState.value = state
    }

    protected open fun acceptLabel(label: Label) {
        viewModelScope.launch {
            mutableLabel.send(label)
        }
    }

    protected fun bindAndStart(
        mainContext: CoroutineContext = mainDispatcher,
        builder: BindingsBuilder.() -> Unit,
    ) = bind(mainContext, builder).run {
        binder = this
        this@BaseViewModel.start()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        Napier.v("VM $this onCleared")
    }

    /** Only for iOS. No-op in Compose-on-iOS code paths. */
    override fun start() {
        if (binderIsStarted) return
        binderIsStarted = true
        Napier.v("VM $this start")
        binder?.start()
    }

    /** Only for iOS. No-op in Compose-on-iOS code paths. */
    override fun stop() {
        if (!binderIsStarted) return
        binderIsStarted = false
        Napier.v("VM $this stop")
        binder?.stop()
    }
}
