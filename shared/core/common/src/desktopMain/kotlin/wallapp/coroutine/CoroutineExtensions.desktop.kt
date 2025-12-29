package wallapp.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual val CoroutineScopeDefault: CoroutineScope = CoroutineScope(CoroutineDispatcherDefault)

actual val CoroutineDispatcherDefault: CoroutineDispatcher
    get() = Dispatchers.Default
