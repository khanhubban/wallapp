package wallapp.coroutine

import kotlin.coroutines.CoroutineContext

interface CoroutineContexts {
    val main: CoroutineContext
    val mainImmediate: CoroutineContext
    val io: CoroutineContext
    val prefetch: CoroutineContext
}
