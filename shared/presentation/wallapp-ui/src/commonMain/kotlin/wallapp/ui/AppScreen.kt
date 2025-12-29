package wallapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.state.account.AccountViewState
import wallapp.content.state.artist.ArtistViewState
import wallapp.content.state.artist.ArtistsViewState
import wallapp.content.state.collection.CollectionActionViewState
import wallapp.content.state.collection.CollectionViewState
import wallapp.content.state.collections.CollectionsViewState
import wallapp.content.state.connections.ConnectionsViewState
import wallapp.content.state.dataconsent.DataConsentViewState
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.error.rewardad.ErrorRewardAdViewState
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.favorites.FavoritesViewState
import wallapp.content.state.firstrun.FirstRunViewState
import wallapp.content.state.folder.FolderViewState
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.home.HomeViewState
import wallapp.content.state.index.IndexViewState
import wallapp.content.state.profile.ProfileViewState
import wallapp.content.state.rewardadinternal.RewardAdInternalViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.content.state.search.SearchResultsViewState
import wallapp.content.state.settings.SettingsViewState
import wallapp.content.state.showcase.ads.ShowcaseAdsViewState
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewState
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewState
import wallapp.content.state.signup.SignUpViewState
import wallapp.content.state.upgrade.plus.paywall.PaywallViewState
import wallapp.content.state.wallpaper.WallpaperShowcaseViewState
import wallapp.content.state.wallpaper.WallpaperSingleActionViewState
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.render.Render
import wallapp.pixel.screen.ScreenViewState
import wallapp.ui.content.account.Account
import wallapp.ui.content.artist.ArtistScreen
import wallapp.ui.content.artist.ArtistsScreen
import wallapp.ui.content.collection.CollectionAction
import wallapp.ui.content.collection.CollectionScreen
import wallapp.ui.content.collections.CollectionsScreen
import wallapp.ui.content.connections.ConnectionsScreen
import wallapp.ui.content.dataconsent.DataConsent
import wallapp.ui.content.error.ErrorScreen
import wallapp.ui.content.error.rewardad.ErrorRewardAd
import wallapp.ui.content.explore.ExploreScreen
import wallapp.ui.content.favorites.FavoritesScreen
import wallapp.ui.content.firstrun.FirstRunScreen
import wallapp.ui.content.folder.FolderScreen
import wallapp.ui.content.home.HomeOnboardingScreen
import wallapp.ui.content.home.HomeScreen
import wallapp.ui.content.index.IndexScreen
import wallapp.ui.content.paywall.Paywall
import wallapp.ui.content.profile.ProfileScreen
import wallapp.ui.content.rewardadinternal.RewardAdInternalScreen
import wallapp.ui.content.search.SearchInputScreen
import wallapp.ui.content.search.SearchResultsScreen
import wallapp.ui.content.settings.SettingsScreen
import wallapp.ui.content.showcase.ads.ShowcaseAdsScreen
import wallapp.ui.content.showcase.nativefeed.ShowcaseIosNativeFeedScreen
import wallapp.ui.content.showcase.typeface.ShowcaseTypefaceScreen
import wallapp.ui.content.signup.SignUp
import wallapp.ui.content.wallpaper.WallpaperShowcase
import wallapp.ui.content.wallpaper.WallpaperSingleAction

@Composable
fun AppScreen(
    render: Render,
    screenViewState: StateFlow<ScreenViewState>,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        render,
        screenViewState.collectAsState().value,
        modifier,
    )
}

@Composable
fun AppScreen(
    render: Render,
    screenViewState: ScreenViewState,
    modifier: Modifier = Modifier,
) {
    when (screenViewState) {
        is AccountViewState -> { Account(render, screenViewState, modifier) }
        is ArtistViewState -> { ArtistScreen(render, screenViewState, modifier) }
        is ArtistsViewState -> { ArtistsScreen(render, screenViewState, modifier) }
        is CollectionActionViewState -> { CollectionAction(render, screenViewState, modifier) }
        is CollectionViewState -> { CollectionScreen(render, screenViewState, modifier) }
        is CollectionsViewState -> { CollectionsScreen(render, screenViewState, modifier) }
        is ConnectionsViewState -> { ConnectionsScreen(render, screenViewState, modifier) }
        is DataConsentViewState -> { DataConsent(render, screenViewState, modifier) }
        is ErrorRewardAdViewState -> { ErrorRewardAd(render, screenViewState, modifier) }
        is ErrorViewState -> { ErrorScreen(render, screenViewState, modifier) }
        is ExploreViewState -> { ExploreScreen(render, screenViewState, modifier) }
        is FavoritesViewState -> { FavoritesScreen(render, screenViewState, modifier) }
        is FirstRunViewState -> { FirstRunScreen(render, screenViewState, modifier) }
        is FolderViewState -> { FolderScreen(render, screenViewState, modifier) }
        is HomeOnboardingViewState -> { HomeOnboardingScreen(render, screenViewState, modifier) }
        is HomeViewState -> { HomeScreen(render, screenViewState, modifier) }
        is IndexViewState -> { IndexScreen(render, screenViewState, modifier) }
        is PaywallViewState -> { Paywall(render, screenViewState, modifier) }
        is ProfileViewState -> { ProfileScreen(render, screenViewState, modifier) }
        is RewardAdInternalViewState -> { RewardAdInternalScreen(render, screenViewState, modifier) }
        is SearchInputViewState -> { SearchInputScreen(render, screenViewState, modifier) }
        is SearchResultsViewState -> { SearchResultsScreen(render, screenViewState, modifier) }
        is SettingsViewState -> { SettingsScreen(render, screenViewState, modifier) }
        is ShowcaseAdsViewState -> { ShowcaseAdsScreen(render, screenViewState, modifier) }
        is ShowcaseIosNativeFeedViewState -> { ShowcaseIosNativeFeedScreen(render, screenViewState, modifier) }
        is ShowcaseTypefaceViewState -> { ShowcaseTypefaceScreen(render, screenViewState, modifier) }
        is SignUpViewState -> { SignUp(render, screenViewState, modifier) }
        is WallpaperShowcaseViewState -> { WallpaperShowcase(render, screenViewState, modifier) }
        is WallpaperSingleActionViewState -> { WallpaperSingleAction(render, screenViewState, modifier) }
        else -> {
            UnhandledAppScreen(screenViewState, modifier)
        }
    }
}

@Composable
private fun UnhandledAppScreen(
    screenViewState: ScreenViewState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text("Unhandled ScreenViewState: ${screenViewState::class}")
    }
}
