package wallapp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import okio.Closeable
import wallapp.coroutine.CoroutineDispatcherDefault
import kotlin.coroutines.CoroutineContext


internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

open class ViewModelInternal {

    open fun onCleared() {
        val scope = _viewModelInternalScope
        if (scope is CloseableCoroutineScope) {
            scope.close()
        }
        _viewModelInternalScope = null
    }

    private var _viewModelInternalScope: CoroutineScope? = null
    val viewModelInternalScope: CoroutineScope
        get() {
            val scope = _viewModelInternalScope
            if (scope != null) {
                return scope
            }

            return CloseableCoroutineScope(SupervisorJob() + CoroutineDispatcherDefault).also {
                _viewModelInternalScope = it
            }
        }
}