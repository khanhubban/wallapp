package wallapp.app

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
import wallapp.screen.createViewModel
import wallapp.viewmodel.ViewModelFactory

class AppViewModelFactoryDefault(
    private val viewModelFactory: ViewModelFactory,
) : AppViewModelFactory {

	override fun createAppViewModel(): AppViewModel {
		return viewModelFactory.create(AppViewModel::class)
	}

	override fun createCollectionActionViewModel(argument: CollectionActionScreenArgument): CollectionActionViewModel {
		return viewModelFactory.create(CollectionActionViewModel::class, argument)
	}

	override fun createExploreViewModel(): ExploreViewModel {
		return viewModelFactory.create(ExploreViewModel::class)
	}

	override fun createHomeViewModel(): HomeViewModel {
		return viewModelFactory.create(HomeViewModel::class)
	}

	override fun createPaywallViewModel(argument: PaywallScreenArgument): PaywallViewModel {
		return viewModelFactory.create(PaywallViewModel::class, argument)
	}

	override fun createProfileViewModel(): ProfileViewModel {
		return viewModelFactory.create(ProfileViewModel::class)
	}

	override fun createRewardAdInternalViewModel(): RewardAdInternalViewModel {
		return viewModelFactory.create(RewardAdInternalViewModel::class)
	}

	override fun createSearchResultsViewModel(): SearchResultsViewModel {
		return viewModelFactory.create(SearchResultsViewModel::class)
	}

	override fun createShowcaseIosNativeFeedViewModel(argument: ShowcaseIosNativeFeedScreenArgument): ShowcaseIosNativeFeedViewModel {
		return viewModelFactory.create(ShowcaseIosNativeFeedViewModel::class, argument)
	}

	override fun createShowcaseAdsViewModel(argument: ShowcaseAdsScreenArgument): ShowcaseAdsViewModel {
		return viewModelFactory.create(ShowcaseAdsViewModel::class, argument)
	}

	override fun createWallpaperSingleActionViewModel(argument: WallpaperSingleActionScreenArgument): WallpaperSingleActionViewModel {
		return viewModelFactory.create(WallpaperSingleActionViewModel::class, argument)
	}

	override fun createWallpaperShowcaseViewModel(argument: WallpaperShowcaseScreenArgument): WallpaperShowcaseViewModel {
		return viewModelFactory.create(WallpaperShowcaseViewModel::class, argument)
	}

	override fun createViewModelFor(argument: ScreenArgument): ScreenViewStateProvider? {
		return argument.createViewModel(viewModelFactory)
	}
}