package wallapp.content.state.showcase.nativefeed

import wallapp.content.model.Wallpaper
import wallapp.pixel.feed.FeedViewState
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.feed.FeedFormatter

class ShowcaseIosNativeFeedViewStateMapper(
    private val viewStateFactory: ViewStateFactory,
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val feedFormatter: FeedFormatter,
) {
    fun mapShowcaseAdsViewState(
        wallpaperItems: List<Wallpaper>?,
    ): ShowcaseIosNativeFeedViewState {
        val wallpapers = wallpaperItems ?: return ShowcaseIosNativeFeedViewState.Loading
        return ShowcaseIosNativeFeedViewState.Data(
            feedViewState = createFeedViewState(wallpapers),
        )
    }

    private fun createFeedViewState(
        wallpapers: List<Wallpaper>,
    ): FeedViewState {
        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec

        val views = feedFormatter.format(
            views = wallpapers.map {
                viewFactory.createWallpaperFeedPreview(
                    wallpaper = it,
                    showPlusButton = false,
                )
            },
            spacerStart = FeedFormatter.FeedSpacerTopToolbar,
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
        )

        return FeedViewState(
            feedViewSpec = feedViewSpec,
            views = views,
            viewsVisibleListener = null,
            toolbar = viewStateFactory.createToolbar("iOS Compose Feed"),
        )
    }

}