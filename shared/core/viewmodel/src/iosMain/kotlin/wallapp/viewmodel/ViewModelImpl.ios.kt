package wallapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Log
import androidx.lifecycle.ViewModel as ViewModelJetpack
import androidx.lifecycle.viewModelScope as viewModelScopeJetpack

actual open class ViewModelImpl : ViewModelJetpack(), ViewModelApi {

    private var onClearedCallback: (() -> Unit)? = null

    actual override fun onCleared() {
        super.onCleared()
        Log.d("[ViewModelImpl] onCleared, this: $this")
        onClearedCallback?.invoke()
    }

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

    actual override val viewModelScope: CoroutineScope
        get() = this.viewModelScopeJetpack

    actual override fun updateOnClearedCallback(onClearedCallback: () -> Unit) {
        Log.d("[ViewModelImpl] updateOnClearedCallback")
        this.onClearedCallback = onClearedCallback
    }
}

