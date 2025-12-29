package wallapp.content.prefetch

import kotlinx.coroutines.CoroutineScope

interface ContentPrefetcherFactory {

    /**
     * [screenId] - typically the ViewModel's simple class name
     * [coroutineScope] - typically the ViewModel's scope
     */
    fun createContentPrefetcher(screenId: Any, coroutineScope: CoroutineScope): ContentPrefetcher
}