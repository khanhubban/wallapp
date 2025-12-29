package wallapp.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


actual val CoroutineScopeDefault: CoroutineScope = CoroutineScope(Dispatchers.Main)

actual val CoroutineDispatcherDefault: CoroutineDispatcher
    get() = Dispatchers.Main.immediate