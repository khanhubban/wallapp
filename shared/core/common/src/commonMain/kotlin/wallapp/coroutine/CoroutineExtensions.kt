package wallapp.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


fun <T> Flow<T>.collectIn(scope: CoroutineScope, collector: FlowCollector<T>): Job = scope.launch {
    collect(collector)
}

fun <X, Y> StateFlow<X>.map(
    scope: CoroutineScope,
    transform: (X) -> (Y)
): StateFlow<Y> {
    val mutableStateFlow = MutableStateFlow(transform(value))
    scope.launch {
        this@map.map(transform).collect { newValue ->
            mutableStateFlow.value = newValue
        }
    }
    return mutableStateFlow
}

suspend fun <T> Flow<T>.getValue(): T? {
    var currentValue: T? = null
    collect { value ->
        currentValue = value
    }
    return currentValue
}

fun <T> MutableSharedFlow<T>.emitOn(value: T, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        emit(value)
    }
}

/**
 * Avoid accessing directly. Provided for tests and default arguments.
 */
expect val CoroutineScopeDefault: CoroutineScope
expect val CoroutineDispatcherDefault: CoroutineDispatcher
