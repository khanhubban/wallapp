package wallapp.content.state.toolbar

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Log

class CollapsingToolbarStateController(coroutineScope: CoroutineScope) {

    private var lastCollapsingToolbarStateUpdateSink: LastCollapsingToolbarStateUpdateSink? = null

    val lastCollapsingToolbarStateUpdateFlow = callbackFlow {
        lastCollapsingToolbarStateUpdateSink = { event ->
            Log.d("[CollapsingToolbarState] lastCollapsingToolbarStateUpdateSink: $event")
            trySend(event)
        }
        awaitClose {
            Log.d("[CollapsingToolbarState] lastCollapsingToolbarStateUpdateFlow: awaitClose")
            lastCollapsingToolbarStateUpdateSink = null
        }
    }
        .map { it.state }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper
        get() = CollapsingToolbarStateWrapper(
            lastCollapsingToolbarState = lastCollapsingToolbarStateUpdateFlow.value,
            lastCollapsingToolbarStateUpdateSink = lastCollapsingToolbarStateUpdateSink,
        )
}