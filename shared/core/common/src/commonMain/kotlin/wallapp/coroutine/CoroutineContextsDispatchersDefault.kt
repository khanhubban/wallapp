package wallapp.coroutine

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


object CoroutineContextsDispatchersDefault : CoroutineContexts {
    override val main: CoroutineContext by lazy { Dispatchers.Default }
    override val mainImmediate: CoroutineContext by lazy { Dispatchers.Default }
    override val io: CoroutineContext by lazy { Dispatchers.Default }
    override val prefetch: CoroutineContext by lazy { Dispatchers.Default }
}