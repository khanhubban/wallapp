package wallapp.content.state.pager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Log
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.pager.PagerPersistableStateWrapper

class PagerPersistableStateController(coroutineScope: CoroutineScope) {

    private var lastPagerStateUpdateSink: LastPagerStateUpdateSink? = null

    val lastPagerStateUpdateFlow = callbackFlow {
        lastPagerStateUpdateSink = { event ->
            Log.d("[PagerState] lastPagerStateUpdateSink: $event")
            trySend(event)
        }
        awaitClose {
            Log.d("[PagerState] lastPagerStateUpdateFlow: awaitClose")
            lastPagerStateUpdateSink = null
        }
    }
        .map { it.state }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val pagerPersistableStateWrapper: PagerPersistableStateWrapper
        get() = PagerPersistableStateWrapper(
            lastPagerState = lastPagerStateUpdateFlow.value,
            lastPagerStateUpdateSink = lastPagerStateUpdateSink,
        )
}