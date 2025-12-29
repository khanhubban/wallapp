package wallapp.content.state.feed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Log
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.feed.LastScrollStateUpdateSink

class FeedScrollStateController(coroutineScope: CoroutineScope) {

    private var lastScrollStateUpdateSink: LastScrollStateUpdateSink? = null
    val lastScrollStateUpdateFlow = callbackFlow {
        lastScrollStateUpdateSink = { event ->
            Log.d("[ScrollState] lastScrollStateUpdateSink: $event")
            trySend(event)
        }
        awaitClose {
            Log.d("[ScrollState] lastScrollStateUpdateFlow: awaitClose")
            lastScrollStateUpdateSink = null
        }
    }
        .map { it.lastScrollState }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val scrollStateWrapper: FeedScrollStateWrapper
        get() = FeedScrollStateWrapper(
            lastScrollState = lastScrollStateUpdateFlow.value,
            lastScrollStateUpdateSink = lastScrollStateUpdateSink,
        )
}