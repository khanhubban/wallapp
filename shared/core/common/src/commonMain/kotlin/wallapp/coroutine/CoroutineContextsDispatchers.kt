package wallapp.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext


object CoroutineContextsDispatchers : CoroutineContexts {
    override val main: CoroutineContext by lazy { Dispatchers.Main }
    override val mainImmediate: CoroutineContext by lazy { Dispatchers.Main.immediate }
    override val io: CoroutineContext by lazy { Dispatchers.IO }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val prefetch: CoroutineContext by lazy { Dispatchers.IO.limitedParallelism(6) }
}