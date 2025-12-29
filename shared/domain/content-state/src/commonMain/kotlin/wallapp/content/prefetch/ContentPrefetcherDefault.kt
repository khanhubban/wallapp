package wallapp.content.prefetch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import wallapp.content.model.Id
import wallapp.content.prefetch.ContentPrefetchArbitrator.arbitratePrefetchEntries
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentCacheManager
import wallapp.image.prefetch.ImagePrefetchData
import wallapp.image.prefetch.ImagePrefetchManager
import wallapp.pixel.util.ScrollDirection
import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewVisibleState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.view.ViewIdMapper

class ContentPrefetcherDefault(
    private val screenId: Any,
    private val imagePrefetchManager: ImagePrefetchManager,
    private val viewIdMapper: ViewIdMapper,
    private val contentCacheManager: ContentCacheManager,
    coroutineScope: CoroutineScope,
) : ContentPrefetcher, ViewsVisibleListener {

    companion object {
        const val MaxWarmItemsToPrefetch = 4
        const val MaxCoolItemsToPrefetch = 50

//        private val Log = Logger("ContentPrefetchManager")
    }

    private val lastScrollDirection = MutableStateFlow<ScrollDirection?>(null)
    private val data = MutableStateFlow<List<ViewState>>(emptyList())
    private val visibleViews = MutableStateFlow<List<ViewVisibleState>>(emptyList())

    private val isUiSettled = MutableStateFlow(false)

    private fun List<ViewState>.mapViewIds(): List<String> {
        return map { state ->
            state.viewId?.id ?: "${state::class.simpleName}"
        }
    }

    private fun List<ViewVisibleState>.mapVisibleIdsNotNull(): List<Id> {
        return mapNotNull { state ->
            state.viewId?.let { viewIdMapper.unmap(it) as? Id }
        }
    }

    private fun createPrefetchData(
        data: List<ViewState>,
        visibleViews: List<ViewVisibleState>,
        lastScrollDirection: ScrollDirection?,
    ): ImagePrefetchData? {
        val firstViewId = visibleViews.firstOrNull()?.viewId
        val indexOfFirst = if (firstViewId != null) {
            data.indexOfFirst { it.viewId == firstViewId }
        } else {
            -1
        }
        val isAtOrNearTopOfFeed = indexOfFirst <= 3
        val (maxWarmItems, maxCoolItems) = if (isAtOrNearTopOfFeed) {
            MaxWarmItemsToPrefetch * 2 to MaxCoolItemsToPrefetch * 2
        } else {
            MaxWarmItemsToPrefetch to MaxCoolItemsToPrefetch
        }

        return arbitratePrefetchEntries(
            data = data,
            visibleViews = visibleViews,
            lastScrollDirection = lastScrollDirection,
            maxWarmItemsToPrefetch = maxWarmItems,
            maxCoolItemsToPrefetch = maxCoolItems,
        )
//            ?.also { prefetchData: ImagePrefetchData ->
//                Log.d("prefetchImageViewStates($screenId): isAtOrNearTopOfFeed: $isAtOrNearTopOfFeed, ${prefetchData.debugStringShort}")
//            }
    }

    private val imagePrefetchData: Flow<ImagePrefetchData?> by lazy {
        combine(
            data,
            visibleViews,
            lastScrollDirection
        ) { data, visibleViews, lastScrollDirection ->
            createPrefetchData(
                data = data,
                visibleViews = visibleViews.sortedBy { it.visibleIndex }, // iOS doesn't pass visible views in order
                lastScrollDirection = lastScrollDirection,
            )
        }.distinctUntilChanged()
    }

    override val viewsVisibleListener: ViewsVisibleListener
        get() = this

    override fun onDataUpdated(data: List<ViewState>) {
//        Log.v("onDataUpdated($screenId): size: ${data.size}, ${data.mapViewIds()}")
        this.data.value = data
    }

    override fun onVisibleViewsChanged(visibleViews: List<ViewVisibleState>) {
        this.visibleViews.value = visibleViews
        val visibleIds = visibleViews.mapVisibleIdsNotNull()
//        Log.v("onVisibleViewsChanged($screenId): size: ${visibleViews.size}, $visibleIds")
    }

    override fun onVisibleViewsSettled(visibleViews: List<ViewVisibleState>) {
        isUiSettled.value = true
        this.visibleViews.value = visibleViews

//        val visibleIds = visibleViews.mapVisibleIdsNotNull()
//        Log.v("onVisibleViewsSettled($screenId): size: ${visibleViews.size}, $visibleIds")
        visibleViews.forEach { visibleView ->
            val id = visibleView.viewId?.let { viewIdMapper.unmap(it) as? Id }
            if (id != null) {
                contentCacheManager.prefetch(id, isUiSettled, visibleView.visibleIndex)
            }
        }
    }

    override fun onScrollDirectionChanged(scrollDirection: ScrollDirection?) {
//        Log.v("onScrollDirectionChanged($screenId): ${lastScrollDirection.value} -> $scrollDirection")
        lastScrollDirection.value = scrollDirection
        if (scrollDirection != null) {
            isUiSettled.value = false
        }
    }

    init {
        imagePrefetchData.collectIn(coroutineScope) {
            imagePrefetchManager.updateScreenId(screenId)
            if (contentCacheManager.prefetchWallpaperImagesEnabled) {
                imagePrefetchManager.updateImagesToPrefetch(screenId, it)
            }
        }
    }
}
