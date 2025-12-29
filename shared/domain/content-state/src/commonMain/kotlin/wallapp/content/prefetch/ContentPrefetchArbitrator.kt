package wallapp.content.prefetch

import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.image.cache.ImageCacheSpec
import wallapp.image.cache.ImageCacheSpec.Companion.ImageCacheSpecCool
import wallapp.image.cache.ImageCacheSpec.Companion.ImageCacheSpecWarm
import wallapp.image.prefetch.ImagePrefetchData
import wallapp.image.prefetch.ImagePrefetchEntry
import wallapp.image.prefetch.combineImagePrefetchData
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.util.ScrollDirection
import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewVisibleState
import wallapp.util.getNextItems
import wallapp.util.getPreviousItems

object ContentPrefetchArbitrator {

    fun arbitratePrefetchEntries(
        data: List<ViewState>,
        visibleViews: List<ViewVisibleState>,
        lastScrollDirection: ScrollDirection?,
        maxWarmItemsToPrefetch: Int,
        maxCoolItemsToPrefetch: Int = 0,
    ): ImagePrefetchData? {
        if (data.isEmpty() || visibleViews.isEmpty()) {
            return null
        }

        val indexMap = visibleViews.mapNotNull { viewVisibleState ->
            if (viewVisibleState.viewId == null) return@mapNotNull null
            val dataIndex = data.indexOfFirst { it.viewId == viewVisibleState.viewId }
            viewVisibleState to dataIndex
        }

        return if (lastScrollDirection != null) {
            arbitrateDirection(
                data = data,
                indexMap = indexMap,
                direction = lastScrollDirection,
                maxWarmItems = maxWarmItemsToPrefetch,
                maxCoolItems = maxCoolItemsToPrefetch,
            )
        } else {
            arbitrateBothDirections(
                data = data,
                indexMap = indexMap,
                maxWarmItems = maxWarmItemsToPrefetch / 2,
                maxCoolItems = maxCoolItemsToPrefetch / 2,
            )
        }
    }

    private fun arbitrateDirection(
        data: List<ViewState>,
        indexMap: List<Pair<ViewVisibleState, Int>>,
        direction: ScrollDirection,
        maxWarmItems: Int,
        maxCoolItems: Int
    ): ImagePrefetchData? {
        val (warmItems, coolItems) = when (direction) {
            ScrollDirection.Ascending -> {
                val startIndex = indexMap.first().second
                data.getPreviousItems(startIndex, maxWarmItems) to
                        data.getPreviousItems(startIndex - maxWarmItems, maxCoolItems)
            }
            ScrollDirection.Descending -> {
                val startIndex = indexMap.last().second
                data.getNextItems(startIndex, maxWarmItems) to
                        data.getNextItems(startIndex + maxWarmItems, maxCoolItems)
            }
        }
        return combinePrefetchData(warmItems, coolItems)
    }

    private fun arbitrateBothDirections(
        data: List<ViewState>,
        indexMap: List<Pair<ViewVisibleState, Int>>,
        maxWarmItems: Int,
        maxCoolItems: Int
    ): ImagePrefetchData? {
        val startIndex = indexMap.first().second
        val endIndex = indexMap.last().second

        val warmItems = data.getPreviousItems(startIndex, maxWarmItems) +
                data.getNextItems(endIndex, maxWarmItems)
        val coolItems = data.getPreviousItems(startIndex - maxWarmItems, maxCoolItems) +
                data.getNextItems(endIndex + maxWarmItems, maxCoolItems)

        return combinePrefetchData(warmItems, coolItems)
    }

    private fun combinePrefetchData(
        warmItems: List<ViewState>,
        coolItems: List<ViewState>
    ): ImagePrefetchData? {
        val warmData = mapImagePrefetchData(warmItems, ImageCacheSpecWarm)
        val coolData = mapImagePrefetchData(coolItems, ImageCacheSpecCool)
        return combineImagePrefetchData(warmData, coolData)
    }

    fun mapImageViewStates(viewState: ViewState): List<ImageViewState>? {
        return when (viewState) {
            is WallpaperPreviewViewState -> listOf(viewState.imageViewState)
            is CollectionPreviewViewState -> listOf(viewState.layers.first().imageViewState)
            else -> null
        }
    }

    fun mapImagePrefetchData(viewStates: List<ViewState>, imageCacheSpec: ImageCacheSpec): ImagePrefetchData? {
        return viewStates
            .flatMap {
                (mapImageViewStates(it) ?: emptyList())
                    .mapNotNull { imageViewState ->
                        ImagePrefetchEntry.from(
                            id = it.viewId?.id,
                            imageViewState = imageViewState,
                            imageCacheSpec = imageCacheSpec,
                        )
                    }
            }
            .ifEmpty { null }
            ?.let { ImagePrefetchData(it) }
    }

}