package wallapp.view.feed

import wallapp.ad.AdManager
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.feed.FeedContentPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.pixel.render.RenderViewIdFactory
import wallapp.pixel.view.FixedPositionViews
import wallapp.pixel.view.View
import wallapp.random.RandomManager
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecFactory
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerBottomDefault
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerBottomNavigationBar
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerHorizontalDefault
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopDefault
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopToolbar
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopToolbarOnly
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerVerticalDefault
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerVerticalNone
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerVerticalSmall
import kotlin.jvm.JvmName

class FeedFormatterDefault(
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val renderViewIdFactory: RenderViewIdFactory,
    private val adManager: AdManager,
    private val randomManager: RandomManager,
) : FeedFormatter {

    private val randomBoolean
        get() = randomManager.deterministicRandom.nextBoolean()

    private val itemsPerChunkBetweenAds = 24

    override fun format(
        views: List<View>,
        spacerStart: View?,
        spacerEnd: View?,
        storiesPreview: List<View>?,
        canExpandItemWidth: Boolean,
        canInsertAds: Boolean,
        distinctByViewId: Boolean,
    ): List<View> {
        val finalViews = if (distinctByViewId) {
            distinctByViewId(views, keepNullIdItems = true)
        } else {
            views
        }
        val feedSpacerStart = when (spacerStart) {
            FeedSpacerTopDefault -> { viewFactory.feedSpacerTop }
            FeedSpacerTopToolbar -> { viewFactory.feedSpacerTopWithToolbar }
            FeedSpacerTopToolbarOnly -> { viewFactory.feedSpacerTopWithToolbarOnly }
            FeedSpacerHorizontalDefault -> { viewFactory.feedSpacerHorizontalDefault }
            FeedSpacerVerticalSmall -> { viewFactory.feedSpacerVerticalSmall }
            FeedSpacerVerticalNone -> { viewFactory.feedSpacerVerticalNone }
            FeedSpacerVerticalDefault -> { viewFactory.feedSpacerVerticalDefault }
            else -> { spacerStart }
        }

        val feedSpacerEnd = when (spacerEnd) {
            FeedSpacerBottomDefault -> { viewFactory.feedSpacerBottom }
            FeedSpacerBottomNavigationBar -> { viewFactory.feedSpacerBottomWithNavBar }
            FeedSpacerHorizontalDefault -> { viewFactory.feedSpacerHorizontalDefault }
            FeedSpacerVerticalSmall -> { viewFactory.feedSpacerVerticalSmall }
            FeedSpacerVerticalNone -> { viewFactory.feedSpacerVerticalNone }
            FeedSpacerVerticalDefault -> { viewFactory.feedSpacerVerticalDefault }
            else -> { spacerEnd }
        }

        return mutableListOf<View>().apply {
            if (storiesPreview != null) {
                addAll(storiesPreview)
            }
            addAll(finalViews)
        }.format(
            canExpandItemWidth = canExpandItemWidth,
            canInsertAds = canInsertAds,
        ).apply {
            if (feedSpacerStart != null) {
                add(0, feedSpacerStart)
            }
            if (feedSpacerEnd != null) {
                add(size, feedSpacerEnd)
            }
        }
    }

    private fun MutableList<View>.format(
        canExpandItemWidth: Boolean,
        canInsertAds: Boolean,
    ): MutableList<View> {
        val insertAds = canInsertAds && showAds
        return this
            .let { if (canExpandItemWidth) it.expandItemWidth(insertAds) else it }
            .let { if (canInsertAds) it.insertAds() else it }
    }

    private fun MutableList<View>.expandItemWidth(insertAds: Boolean): MutableList<View> {
        return this.mapIndexed { index, view ->
            val base = if (insertAds) 12 else 7
            val mapToFullWidth = ((index + 3) % base == 0 && index > 4) || this.size < 5

            if (view.viewSpec is FeedContentPreviewViewSpec && mapToFullWidth) {
                View(
                    viewSpec = viewSpecFactory.feedContentPreviewViewSpecWide,
                    viewState = view.viewState,
                )
            } else {
                view
            }
        }
            .toMutableList()
    }

    private val showAds: Boolean
        get() = adManager.feedAdsEnabled.value

    private fun MutableList<View>.insertAds(): MutableList<View> {
        if (!showAds) return this

        adManager.resetRandomness()
        return this.mapIndexed { index, view ->
            val insertAd = index % itemsPerChunkBetweenAds == 0 && index > 4
            if (insertAd) {
                adManager.createFeedAd(index)?.let {
                    return@mapIndexed listOf(view, it)
                }
            }
            listOf(view)
        }
            .flatten()
            .toMutableList()
    }

    override fun insertSpacing(
        views: List<View>,
        spacer: View,
        spacerStart: View?,
        spacerEnd: View?
    ): List<View> {
        return views.flatMap {
            listOf(it, spacer)
        }.dropLast(1).toMutableList().apply {
            if (spacerStart != null) {
                add(0, spacerStart)
            }
            if (spacerEnd != null) {
                add(size, spacerEnd)
            }
        }
    }

    override fun insertFixedPositionViews(
        views: List<View>,
        fixedPositionViews: FixedPositionViews,
    ): List<View> = insertFixedPositionViews(
        views = views,
        fixedPositionViews = listOf(fixedPositionViews),
    )

    override fun insertFixedPositionViews(
        views: List<View>,
        fixedPositionViews: List<FixedPositionViews>,
    ): List<View> {
        return views.toMutableList().apply {
            for (items in fixedPositionViews) {
                addAll(items.feedPosition, items.views)
            }
        }
    }

    override fun distinctByViewId(views: List<View>, keepNullIdItems: Boolean): List<View> {
        return views
            .groupBy { renderViewIdFactory.getRenderViewId(it, index = null) }
            .map {
                it.value.first()
            }
    }

    override fun groupByChunks(
        views: List<View>,
        chunkSize: Int,
        forceIncludeDanglingViews: Boolean,
    ): List<View> {
        return views.groupByChunks(chunkSize).let { grouped ->
            // If there are dangling views append them to the end of the list. Workaround for #2421.
            if (forceIncludeDanglingViews) {
                val missingViews = views.filter { it !in grouped }
                grouped + missingViews
            } else {
                grouped
            }
        }
    }

    /**
     * Group views by chunks of 24 items, alternating between [CollectionPreviewViewState] and
     * [WallpaperPreviewViewState]. This is hardcoded to only support Collections and Wallpaper
     * Singles. The same number of items appear in each column so the total height of each column
     * is the same.
     *
     * Note: this may result in items being dropped if they don't align with the chunk size. #2421.
     */
    @JvmName("groupByChunksExt")
    private fun List<View>.groupByChunks(chunkSize: Int): List<View> {
        require(chunkSize == 24) { "Only chunk size of 24 is supported" }
        val listViews = arrayListOf<View>()
        var chunk = 1
        var collectionCount = 0
        var wallpaperCount = 0

        val collectionPreviewViewStateList = this.filter { it.viewState is CollectionPreviewViewState }
        val wallpaperPreviewViewStateList = this.filter { it.viewState is WallpaperPreviewViewState }

        if (this.isNotEmpty()) {
            listViews.add(this[0]) // this is for the CarouselView
        }

        var startWithCollection = randomBoolean

        for (i in this.indices) {
            chunk += when (chunk) {
                in getChunkIndices(startWithCollection) -> {
                    if (collectionCount < collectionPreviewViewStateList.size) {
                        listViews.add(collectionPreviewViewStateList[collectionCount])
                        collectionCount += 1
                    }
                    1
                }
                else -> {
                    if (wallpaperCount < wallpaperPreviewViewStateList.size) {
                        listViews.add(wallpaperPreviewViewStateList[wallpaperCount])
                        wallpaperCount += 1
                    }
                    1
                }
            }
            if (chunk == 25) {
                chunk = 1
                startWithCollection = !startWithCollection
            }
        }

        return listViews
    }

    private fun getChunkIndices(startWithCollection: Boolean): List<Int> {
        return if (startWithCollection) {
            listOf(1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24)
        } else {
            listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24)
        }
    }
}
