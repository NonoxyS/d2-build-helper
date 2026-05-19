package dev.nonoxy.d2buildhelper.core.presentation.viewmodel

import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.CStateFlow

interface BaseIosViewModel<State, Label> {

    val state: CStateFlow<State>
    val label: CFlow<Label>

    fun onCleared()

    fun start()

    fun stop()
}
