package wallapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import moe.tlaster.precompose.viewmodel.ViewModel as ViewModelPrecompose
import moe.tlaster.precompose.viewmodel.viewModelScope as viewModelScopePrecompose

actual open class ViewModelImpl : ViewModelPrecompose(), ViewModelApi {

    actual override fun onCleared() {
        super.onCleared()
    }

    actual override val viewModelScope: CoroutineScope
        get() = this.viewModelScopePrecompose

    actual override fun <T> Flow<T>.stateIn(
        initialValue: T,
        startWhileSubscribedNetwork: Boolean,
    ): StateFlow<T> = stateIn(
        scope = viewModelScope,
        started = if (startWhileSubscribedNetwork) {
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        } else {
            SharingStarted.WhileSubscribed()
        },
        initialValue = initialValue,
    )

    actual override fun updateOnClearedCallback(onClearedCallback: () -> Unit) {
        // no-op
    }
}
