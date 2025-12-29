package wallapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.ad.FeedAdViewState
import wallapp.content.state.artist.ArtistPreviewViewState
import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.carousel.CarouselViewState
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.content.state.explore.ExploreHeaderViewState
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.favorites.FavoritesViewState
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.content.state.home.HomeHeaderViewState
import wallapp.content.state.home.HomeOnboardingHeaderViewState
import wallapp.content.state.profile.ProfileConnectionsViewState
import wallapp.content.state.profile.ProfileCuratorViewState
import wallapp.content.state.profile.ProfileHeaderViewState
import wallapp.content.state.promo.PromoViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.content.state.search.SearchResultsHeaderViewState
import wallapp.content.state.settings.SettingGroupViewState
import wallapp.content.state.settings.SettingViewState
import wallapp.content.state.social.SocialLinksViewState
import wallapp.content.state.upgrade.plus.paywall.PaywallPlanViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.content.state.widget.HorizontalScrollRowViewState
import wallapp.content.state.widget.NoDataViewState
import wallapp.content.state.widget.WidgetViewState
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.render.Render
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewRendererCompose
import wallapp.ui.content.account.AccountOverview
import wallapp.ui.content.ad.Ad
import wallapp.ui.content.ad.InlineAd
import wallapp.ui.content.artist.ArtistPreview
import wallapp.ui.content.carousel.Carousel
import wallapp.ui.content.collection.CollectionPreview
import wallapp.ui.content.downloadstatus.DownloadStatus
import wallapp.ui.content.exhibit.Exhibit
import wallapp.ui.content.explore.ExploreHeader
import wallapp.ui.content.favorite.AnimatedFavoriteButton
import wallapp.ui.content.favorites.FavoritesScreen
import wallapp.ui.content.folder.FolderPreview
import wallapp.ui.content.home.HomeHeader
import wallapp.ui.content.home.HomeOnboardingHeader
import wallapp.ui.content.paywall.PaywallPlan
import wallapp.ui.content.profile.ProfileConnections
import wallapp.ui.content.profile.ProfileCurator
import wallapp.ui.content.profile.ProfileHeader
import wallapp.ui.content.promo.Promo
import wallapp.ui.content.search.SearchInputContent
import wallapp.ui.content.search.SearchResultsHeader
import wallapp.ui.content.settings.Setting
import wallapp.ui.content.settings.SettingGroup
import wallapp.ui.content.social.SocialLinks
import wallapp.ui.content.wallpaper.WallpaperFeedPreview
import wallapp.ui.content.widget.NoData
import wallapp.ui.widget.HorizontalScrollRow
import wallapp.ui.widget.Widget

object ViewRendererApp : ViewRendererCompose {

    @Composable
    override fun render(
        render: Render,
        view: View,
        modifier: Modifier,
        alignment: Alignment,
    ): Boolean {
        when (val viewState = view.viewState) {
            is WallpaperPreviewViewState -> {
                WallpaperFeedPreview(
                    render,
                    viewState,
                    modifier,
                    alignment,
                )
            }

            is ArtistPreviewViewState -> {
                ArtistPreview(render, viewState, view.viewSpec as FeedViewViewSpec, modifier, alignment)
            }

            is CollectionPreviewViewState -> {
                CollectionPreview(render, viewState, modifier, alignment)
            }

            is AccountOverviewViewState -> {
                AccountOverview(render, viewState, modifier)
            }

            is AdViewState -> {
                Ad(render, viewState, modifier)
            }

            is FeedAdViewState -> {
                InlineAd(render, viewState, modifier)
            }

            is FavoritesViewState -> {
                FavoritesScreen(render, viewState, modifier)
                return false
            }

            is SocialLinksViewState -> {
                SocialLinks(render, viewState, modifier)
            }

            is WidgetViewState -> {
                Widget(render, viewState, modifier)
            }

            is HorizontalScrollRowViewState -> {
                HorizontalScrollRow(render, viewState, modifier)
            }

            is SearchInputViewState -> {
                SearchInputContent(render, viewState, modifier)
            }

            is SearchResultsHeaderViewState -> {
                SearchResultsHeader(render, viewState, modifier)
            }

            is SettingViewState -> {
                Setting(render, viewState, modifier)
            }

            is SettingGroupViewState -> {
                SettingGroup(render, viewState, modifier)
            }

            is ProfileConnectionsViewState -> {
                ProfileConnections(render, viewState, modifier)
            }

            is ProfileCuratorViewState -> { ProfileCurator(render, viewState, modifier) }

            is ProfileHeaderViewState -> {
                ProfileHeader(render, viewState, modifier)
            }

            is PromoViewState -> {
                Promo(render, viewState, modifier)
            }

            is CarouselViewState -> {
                Carousel(render, viewState, viewSpec = view.viewSpec as CarouselViewSpec, modifier)
            }

            is DownloadStatusViewState -> {
                DownloadStatus(render, viewState, modifier)
            }

            is ExhibitViewState -> {
                Exhibit(render, viewState, modifier)
            }

            is ExploreHeaderViewState -> { ExploreHeader(render, viewState, modifier) }

            is HomeHeaderViewState -> { HomeHeader(render, viewState, modifier) }

            is HomeOnboardingHeaderViewState -> { HomeOnboardingHeader(render, viewState, modifier) }

            is NoDataViewState -> { NoData(render, viewState, modifier) }

            is FavoriteViewState -> { AnimatedFavoriteButton(render, viewState, modifier = modifier) }

            is PaywallPlanViewState -> { PaywallPlan(render, viewState, modifier) }

            is FolderPreviewViewState -> { FolderPreview(render, viewState, modifier) }

            else -> {
                return false
            }
        }

        return true
    }
}