package wallapp.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class CoroutineContextsPreset(private val coroutineScope: CoroutineScope) : CoroutineContexts {
    override val main: CoroutineContext
        get() = coroutineScope.coroutineContext
    override val mainImmediate: CoroutineContext
        get() = coroutineScope.coroutineContext
    override val io: CoroutineContext
        get() = coroutineScope.coroutineContext
    override val prefetch: CoroutineContext
        get() = coroutineScope.coroutineContext
}