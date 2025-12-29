package wallapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
//import androidx.lifecycle.ViewModel as ViewModelAndroidX
//import androidx.lifecycle.viewModelScope as viewModelScopeAndroidX

interface ViewModelApi {
    fun onCleared()

    val viewModelScope: CoroutineScope

    fun <T> Flow<T>.stateIn(
        initialValue: T,
        startWhileSubscribedNetwork: Boolean,
    ): StateFlow<T>

    fun <T> Flow<T>.stateIn(
        initialValue: T,
    ): StateFlow<T> {
        return stateIn(
            initialValue = initialValue,
            startWhileSubscribedNetwork = false,
        )
    }

    fun updateOnClearedCallback(onClearedCallback: () -> Unit)
}

expect open class ViewModelImpl() : ViewModelApi {
    override fun onCleared()

    override val viewModelScope: CoroutineScope

    override fun <T> Flow<T>.stateIn(
        initialValue: T,
        startWhileSubscribedNetwork: Boolean,
    ): StateFlow<T>

    override fun updateOnClearedCallback(onClearedCallback: () -> Unit)
}

typealias ViewModel = ViewModelImpl


//abstract class ViewModel : ViewModelAndroidX() {
//    public abstract override fun onCleared()
//
//    val viewModelScope: CoroutineScope
//        get() = this.viewModelScopeAndroidX
//
//    fun <T> Flow<T>.stateIn(
//        initialValue: T,
//        startWhileSubscribedNetwork: Boolean = false,
//    ): StateFlow<T> = stateIn(
//        scope = viewModelScope,
//        started = if (startWhileSubscribedNetwork) {
//            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
//        } else {
//            SharingStarted.WhileSubscribed()
//        },
//        initialValue = initialValue,
//    )
//}