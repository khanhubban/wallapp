package wallapp.preference

import kotlinx.coroutines.flow.StateFlow
import wallapp.settings.ObservableValueFlow

val <T : Any> ObservableValue<T>.stateFlow: StateFlow<T>
    get() = when (this) {
        is ObservableValueFlow<T> -> this.mutableStateFlow
        else -> throw IllegalStateException("ObservableValue is not an instance of ObservableValueFlow")
    }
