package wallapp.view.feed

import wallapp.pixel.view.FixedPositionViews
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState

interface FeedFormatter {

    fun format(
        views: List<View>,
        spacerStart: View? = FeedSpacerTopDefault,
        spacerEnd: View? = FeedSpacerBottomDefault,
        storiesPreview: List<View>? = null,
        canExpandItemWidth: Boolean = false,
        canInsertAds: Boolean = false,
        distinctByViewId: Boolean = true,
    ): List<View>

    fun insertSpacing(
        views: List<View>,
        spacer: View = FeedSpacerHorizontalDefault,
        spacerStart: View? = FeedSpacerTopDefault,
        spacerEnd: View? = FeedSpacerBottomDefault,
    ): List<View>

    fun insertFixedPositionViews(
        views: List<View>,
        fixedPositionViews: List<FixedPositionViews>,
    ): List<View>

    fun insertFixedPositionViews(
        views: List<View>,
        fixedPositionViews: FixedPositionViews,
    ): List<View>

    /**
     * Removes views with the same viewId. Required to work around ids existing more than once due
     * to not using data paging (#14).
     */
    fun distinctByViewId(views: List<View>, keepNullIdItems: Boolean): List<View>

    fun groupByChunks(
        views: List<View>,
        chunkSize: Int = ItemsPerChunkBetweenAds,
        forceIncludeDanglingViews: Boolean = false,
    ): List<View>

    companion object {

        val FeedSpacerTopDefault = View(viewState = object : ViewState {})
        val FeedSpacerTopToolbar = View(viewState = object : ViewState {})
        val FeedSpacerTopToolbarOnly = View(viewState = object : ViewState {})
        val FeedSpacerHorizontalDefault = View(viewState = object : ViewState {})
        val FeedSpacerVerticalSmall = View(viewState = object : ViewState {})
        val FeedSpacerVerticalNone = View(viewState = object : ViewState {})
        val FeedSpacerVerticalDefault = View(viewState = object : ViewState {})
        val FeedSpacerBottomDefault = View(viewState = object : ViewState {})
        val FeedSpacerBottomNavigationBar = View(viewState = object : ViewState {})

        private const val ItemsPerChunkBetweenAds = 24
    }

}