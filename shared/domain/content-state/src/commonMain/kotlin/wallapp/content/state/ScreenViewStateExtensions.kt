package wallapp.content.state

import wallapp.content.state.artist.ArtistViewState
import wallapp.content.state.artist.ArtistsViewState
import wallapp.content.state.collection.CollectionViewState
import wallapp.content.state.collections.CollectionsViewState
import wallapp.content.state.connections.ConnectionsViewState
import wallapp.content.state.error.rewardad.ErrorRewardAdViewState
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.favorites.FavoritesViewState
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.home.HomeViewState
import wallapp.content.state.rewardadinternal.RewardAdInternalViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.content.state.search.SearchResultsViewState
import wallapp.content.state.upgrade.plus.paywall.PaywallViewState
import wallapp.content.state.wallpaper.WallpaperShowcaseViewState
import wallapp.pixel.screen.ScreenViewState

fun ScreenViewState.isLoading(): Boolean {
    return when (this) {
        is ArtistViewState -> {
            this is ArtistViewState.Loading
        }
        is ArtistsViewState -> {
            this is ArtistsViewState.Loading
        }
        is CollectionViewState -> {
            this is CollectionViewState.Loading
        }
        is CollectionsViewState -> {
            this is CollectionsViewState.Loading
        }
        is ConnectionsViewState -> {
            this is ConnectionsViewState.Loading
        }
        is ExploreViewState -> {
            this is ExploreViewState.Loading
        }
        is ErrorRewardAdViewState -> {
            this is ErrorRewardAdViewState.Loading
        }
        is FavoritesViewState -> {
            this is FavoritesViewState.Loading
        }
        is HomeOnboardingViewState -> {
            this is HomeOnboardingViewState.Loading
        }
        is HomeViewState -> {
            this is HomeViewState.Loading
        }
        is SearchResultsViewState -> {
            this is SearchResultsViewState.Loading
        }
        is SearchInputViewState -> {
            this is SearchInputViewState.Loading
        }
        is PaywallViewState -> {
            this is PaywallViewState.Loading
        }
        is RewardAdInternalViewState -> {
            this is RewardAdInternalViewState.Loading
        }
        is WallpaperShowcaseViewState -> {
            this is WallpaperShowcaseViewState.Loading
        }
        else -> false
    }
}