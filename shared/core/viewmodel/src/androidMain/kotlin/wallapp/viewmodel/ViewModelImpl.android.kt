package wallapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel as ViewModelAndroidX
import androidx.lifecycle.viewModelScope as viewModelScopeAndroidX

actual open class ViewModelImpl : ViewModelAndroidX(), ViewModelApi {

    actual override fun onCleared() {
        super.onCleared()
    }

    actual override val viewModelScope: CoroutineScope
        get() = this.viewModelScopeAndroidX

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
