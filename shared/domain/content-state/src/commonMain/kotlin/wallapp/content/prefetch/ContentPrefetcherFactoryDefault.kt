package wallapp.content.prefetch

import kotlinx.coroutines.CoroutineScope
import wallapp.data.content.ContentCacheManager
import wallapp.image.prefetch.ImagePrefetchManager
import wallapp.view.ViewIdMapper

class ContentPrefetcherFactoryDefault(
    private val imagePrefetchManager: ImagePrefetchManager,
    private val viewIdMapper: ViewIdMapper,
    private val contentCacheManager: ContentCacheManager,
) : ContentPrefetcherFactory {

    override fun createContentPrefetcher(
        screenId: Any,
        coroutineScope: CoroutineScope,
    ): ContentPrefetcher {
        return ContentPrefetcherDefault(
            screenId = screenId,
            imagePrefetchManager = imagePrefetchManager,
            viewIdMapper = viewIdMapper,
            contentCacheManager = contentCacheManager,
            coroutineScope = coroutineScope,
        )
    }
}