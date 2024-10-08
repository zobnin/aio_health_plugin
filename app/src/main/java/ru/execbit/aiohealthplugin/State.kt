package ru.execbit.aiohealthplugin

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val _state = MutableStateFlow<State>(State.NotConnected)
val state: StateFlow<State> get() = _state.asStateFlow()

fun updateState(newState: State) {
    _state.value = newState
}

sealed class State {
    data object ServiceNotRunning : State()
    data object NotConnected : State()
    data object Connected : State()
    data object Loading : State()
    class DataAvailable(val data: HealthDataModel) : State()
    data object NotAvailable : State()
    data object NoPermission : State()
    data object ReallyBadError : State()
    class Error(val exception: Exception) : State()
}