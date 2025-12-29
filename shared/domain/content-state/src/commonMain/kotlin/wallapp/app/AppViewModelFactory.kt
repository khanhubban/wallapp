package wallapp.app;

import wallapp.content.state.collection.CollectionActionViewModel
import wallapp.content.state.explore.ExploreViewModel
import wallapp.content.state.home.HomeViewModel
import wallapp.content.state.profile.ProfileViewModel
import wallapp.content.state.rewardadinternal.RewardAdInternalViewModel
import wallapp.content.state.search.SearchResultsViewModel
import wallapp.content.state.showcase.ads.ShowcaseAdsViewModel
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.content.state.wallpaper.WallpaperSingleActionViewModel
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.CollectionActionScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.ShowcaseAdsScreenArgument
import wallapp.screen.ScreenArgument.ShowcaseIosNativeFeedScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.screen.ScreenArgument.WallpaperSingleActionScreenArgument

interface AppViewModelFactory {

	fun createAppViewModel(): AppViewModel
	fun createCollectionActionViewModel(argument: CollectionActionScreenArgument): CollectionActionViewModel
	fun createExploreViewModel(): ExploreViewModel
	fun createHomeViewModel(): HomeViewModel
	fun createPaywallViewModel(argument: PaywallScreenArgument): PaywallViewModel
	fun createProfileViewModel(): ProfileViewModel
	fun createRewardAdInternalViewModel(): RewardAdInternalViewModel
	fun createSearchResultsViewModel(): SearchResultsViewModel
	fun createShowcaseAdsViewModel(argument: ShowcaseAdsScreenArgument): ShowcaseAdsViewModel
	fun createShowcaseIosNativeFeedViewModel(argument: ShowcaseIosNativeFeedScreenArgument): ShowcaseIosNativeFeedViewModel
	fun createWallpaperShowcaseViewModel(argument: WallpaperShowcaseScreenArgument): WallpaperShowcaseViewModel
	fun createWallpaperSingleActionViewModel(argument: WallpaperSingleActionScreenArgument): WallpaperSingleActionViewModel
	fun createViewModelFor(argument: ScreenArgument): ScreenViewStateProvider?
}

