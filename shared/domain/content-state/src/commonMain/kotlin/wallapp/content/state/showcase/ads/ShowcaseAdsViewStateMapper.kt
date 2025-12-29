package wallapp.content.state.showcase.ads

import wallapp.ad.AdManager
import wallapp.ad.AdSource
import wallapp.content.model.Wallpaper
import wallapp.content.state.ad.FeedAdViewState
import wallapp.pixel.feed.FeedViewState
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.feed.FeedFormatter

class ShowcaseAdsViewStateMapper(
    private val viewStateFactory: ViewStateFactory,
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val feedFormatter: FeedFormatter,
    private val adManager: AdManager,
) {
    fun mapShowcaseAdsViewState(
        wallpaperItems: List<Wallpaper>?,
        insertAdAtIndex: Int = 1,
    ): ShowcaseAdsViewState {
        val wallpapers = wallpaperItems ?: return ShowcaseAdsViewState.Loading
        return ShowcaseAdsViewState.Data(
            feedViewState = createFeedViewState(wallpapers, insertAdAtIndex),
        )
    }

    private fun createFeedViewState(
        wallpapers: List<Wallpaper>,
        insertAdAtIndex: Int,
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

        val insertAd = views.size > insertAdAtIndex && views[insertAdAtIndex].viewState !is FeedAdViewState
        val singleColumnAd = (adManager.createFeedAd(insertAdAtIndex, source = AdSource.AdNetwork)
            ?: adManager.createFeedAd(insertAdAtIndex, source = AdSource.Preset))!!
        val finalizedViews = if (insertAd) {
            views.take(insertAdAtIndex) + singleColumnAd + views.drop(insertAdAtIndex)
        } else {
            views
        }

        return FeedViewState(
            feedViewSpec = feedViewSpec,
            views = finalizedViews,
            viewsVisibleListener = null,
            toolbar = viewStateFactory.createToolbar("Ads Showcase"),
        )
    }

}