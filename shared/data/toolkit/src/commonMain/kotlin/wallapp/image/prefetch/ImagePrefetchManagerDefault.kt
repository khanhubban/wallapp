package wallapp.image.prefetch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import wallapp.coroutine.CoroutineScopes
import wallapp.util.combine

class ImagePrefetchManagerDefault(
    private val imagePrefetcher: ImagePrefetcher,
    private val coroutineScopes: CoroutineScopes,
) : ImagePrefetchManager {

    private val currentScreenId: MutableStateFlow<Any?> = MutableStateFlow(null)

    private val updateTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 64)

    private val coroutineScopePrefetch: CoroutineScope
        get() = coroutineScopes.prefetch

    override fun updateImagesToPrefetch(screenId: Any, data: ImagePrefetchData?) {
        if (data == null) {
            cacheMap.remove(screenId)
        } else {
            cacheMap[screenId] = data
        }
        if (currentScreenId.value == screenId) {
            updateTrigger.tryEmit(Unit)
        }
    }

    private val cacheMap = mutableMapOf<Any, ImagePrefetchData?>()

    override fun updateScreenId(screenId: Any) {
        currentScreenId.value = screenId
    }

    init {
        combine(currentScreenId, updateTrigger) { screenId, _ ->
            screenId?.let { cacheMap[it] }?.also {
                imagePrefetcher.prefetch(it)
            }
        }
            .distinctUntilChanged()
            .shareIn(coroutineScopePrefetch, started = SharingStarted.Eagerly)
    }
}