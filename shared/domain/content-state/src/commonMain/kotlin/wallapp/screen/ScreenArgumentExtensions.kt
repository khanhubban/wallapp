package wallapp.screen

import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.account.AccountViewModel
import wallapp.content.state.artist.ArtistViewModel
import wallapp.content.state.artist.ArtistsViewModel
import wallapp.content.state.collection.CollectionActionViewModel
import wallapp.content.state.collection.CollectionViewModel
import wallapp.content.state.connections.ConnectionsViewModel
import wallapp.content.state.dataconsent.DataConsentViewModel
import wallapp.content.state.debug.DebugViewModel
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.appupdaterequired.ErrorAppUpdateRequiredViewModel
import wallapp.content.state.error.downloadfailed.ErrorDownloadFailedViewModel
import wallapp.content.state.error.expiredsubscription.ErrorSubscriptionExpiredViewModel
import wallapp.content.state.error.network.ErrorNetworkViewModel
import wallapp.content.state.error.permissionphotosandroid.ErrorPermissionSystemMediaDeniedAndroidViewModel
import wallapp.content.state.error.permissionphotosios.ErrorPermissionSystemMediaDeniedIosViewModel
import wallapp.content.state.error.purchase.ErrorPurchaseViewModel
import wallapp.content.state.error.remotedatafetch.ErrorRemoteDataFetchViewModel
import wallapp.content.state.error.rewardad.ErrorRewardAdViewModel
import wallapp.content.state.error.signin.ErrorSignInViewModel
import wallapp.content.state.error.template.ErrorTemplateViewModel
import wallapp.content.state.error.userprofile.ErrorUserProfileViewModel
import wallapp.content.state.explore.ExploreViewModel
import wallapp.content.state.firstrun.FirstRunViewModel
import wallapp.content.state.folder.FolderViewModel
import wallapp.content.state.home.HomeOnboardingViewModel
import wallapp.content.state.home.HomeViewModel
import wallapp.content.state.index.IndexViewModel
import wallapp.content.state.osslicenses.OssLicensesViewModel
import wallapp.content.state.profile.ProfileViewModel
import wallapp.content.state.rewardadinternal.RewardAdInternalViewModel
import wallapp.content.state.search.SearchInputViewModel
import wallapp.content.state.search.SearchResultsViewModel
import wallapp.content.state.settings.SettingsViewModel
import wallapp.content.state.showcase.ads.ShowcaseAdsViewModel
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewModel
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewModel
import wallapp.content.state.signup.SignUpViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.content.state.wallpaper.WallpaperSingleActionViewModel
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.viewmodel.ViewModelFactory

fun WallpaperItem.createWallpaperScreenArgument(
    firstWallpaperId: WallpaperId? = null,
): WallpaperShowcaseScreenArgument =
    when (this) {
        is WallpaperRemix -> {
            WallpaperShowcaseScreenArgument(
                remixId = this.id,
                firstWallpaperId = firstWallpaperId,
            )
        }

        else -> {
            throw IllegalStateException("Unknown WallpaperItem type: $this")
        }
    }

val ScreenArgument.screen: Screen
    get() = when (this) {
        is ScreenArgument.AccountScreenArgument -> Screen.Account
        is ScreenArgument.ArtistsScreenArgument -> Screen.Artists
        is ScreenArgument.ArtistIdScreenArgument -> Screen.Artist
        is ScreenArgument.CollectionIdScreenArgument -> Screen.Collection
        is ScreenArgument.CollectionActionScreenArgument -> Screen.CollectionAction
        is ScreenArgument.ConnectionsScreenArgument -> Screen.Connections
        is ScreenArgument.DataConsentScreenArgument -> Screen.DataConsent
        is ScreenArgument.DebugSettingsScreenArgument -> Screen.DebugSettings
        is ScreenArgument.ErrorScreenArgument -> {
            when (errorScreen) {
                ErrorScreen.AppUpdateRequired -> Screen.ErrorAppUpdateRequired
                is ErrorScreen.DownloadFailed -> Screen.ErrorDownloadFailed
                is ErrorScreen.Network -> Screen.ErrorNetwork
                ErrorScreen.PermissionSystemMediaDeniedAndroid -> Screen.ErrorPermissionSystemMediaDeniedAndroid
                ErrorScreen.PermissionSystemMediaDeniedIos -> Screen.ErrorPermissionSystemMediaDeniedIos
                is ErrorScreen.Purchase -> Screen.ErrorPurchase
                ErrorScreen.RemoteDataFetch -> Screen.ErrorRemoteDataFetch
                is ErrorScreen.RewardAd -> Screen.ErrorRewardAd
                is ErrorScreen.SignIn -> Screen.ErrorSignIn
                ErrorScreen.SubscriptionExpired -> Screen.ErrorSubscriptionExpired
                ErrorScreen.Template -> Screen.ErrorTemplate
                is ErrorScreen.UserProfile -> Screen.ErrorUserProfile
            }
        }
        is ScreenArgument.ExploreScreenArgument -> Screen.Explore
        is ScreenArgument.FirstRunScreenArgument -> Screen.FirstRun
        is ScreenArgument.FolderScreenArgument -> Screen.Folder
        is ScreenArgument.HomeOnboardingScreenArgument -> Screen.HomeOnboarding
        is ScreenArgument.HomeScreenArgument -> Screen.Home
        is ScreenArgument.IndexScreenArgument -> Screen.Index
        is ScreenArgument.ManageSubscriptionScreenArgument -> Screen.Paywall
        is ScreenArgument.OssLicensesScreenArgument -> Screen.OssLicenses
        is ScreenArgument.PaywallScreenArgument -> Screen.Paywall
        is ScreenArgument.ProfileScreenArgument -> Screen.Profile
        is ScreenArgument.RewardAdInternalScreenArgument -> Screen.RewardAdInternal
        is ScreenArgument.SearchScreenArgument -> Screen.Search
        is ScreenArgument.SearchResultsScreenArgument -> Screen.SearchResults
        is ScreenArgument.ShowcaseAdsScreenArgument -> Screen.ShowcaseAds
        is ScreenArgument.ShowcaseIosNativeFeedScreenArgument -> Screen.ShowcaseIosNativeFeed
        is ScreenArgument.ShowcaseTypefaceScreenArgument -> Screen.ShowcaseTypeface
        is ScreenArgument.ShowcaseTypefaceIosScreenArgument -> Screen.ShowcaseTypefaceIos
        is ScreenArgument.SignUpScreenArgument -> Screen.SignUp
        is ScreenArgument.WallpaperShowcaseScreenArgument -> Screen.WallpaperShowcase
        is ScreenArgument.WallpaperSingleActionScreenArgument -> Screen.WallpaperSingleAction
        else -> throw IllegalStateException("Unknown ScreenArgument type: $this")
    }

fun ScreenArgument.createViewModel(viewModelFactory: ViewModelFactory): ScreenViewStateProvider? {
    if (!screen.nativeNavigationSupported) return null
    val clazz = when (this) {
        is ScreenArgument.ArtistIdScreenArgument -> ArtistViewModel::class
        ScreenArgument.AccountScreenArgument -> AccountViewModel::class
        ScreenArgument.ArtistsScreenArgument -> ArtistsViewModel::class
        is ScreenArgument.CollectionIdScreenArgument -> CollectionViewModel::class
        is ScreenArgument.CollectionActionScreenArgument -> CollectionActionViewModel::class
        is ScreenArgument.ConnectionsScreenArgument -> ConnectionsViewModel::class
        ScreenArgument.DataConsentScreenArgument -> DataConsentViewModel::class
        ScreenArgument.DebugSettingsScreenArgument -> DebugViewModel::class
        is ScreenArgument.DesignIdScreenArgument -> null
        is ScreenArgument.ErrorScreenArgument -> {
            when (errorScreen) {
                ErrorScreen.AppUpdateRequired -> ErrorAppUpdateRequiredViewModel::class
                is ErrorScreen.DownloadFailed -> ErrorDownloadFailedViewModel::class
                is ErrorScreen.Network -> ErrorNetworkViewModel::class
                ErrorScreen.PermissionSystemMediaDeniedAndroid -> ErrorPermissionSystemMediaDeniedAndroidViewModel::class
                ErrorScreen.PermissionSystemMediaDeniedIos -> ErrorPermissionSystemMediaDeniedIosViewModel::class
                is ErrorScreen.Purchase -> ErrorPurchaseViewModel::class
                ErrorScreen.RemoteDataFetch -> ErrorRemoteDataFetchViewModel::class
                is ErrorScreen.RewardAd -> ErrorRewardAdViewModel::class
                is ErrorScreen.SignIn -> ErrorSignInViewModel::class
                ErrorScreen.SubscriptionExpired -> ErrorSubscriptionExpiredViewModel::class
                ErrorScreen.Template -> ErrorTemplateViewModel::class
                is ErrorScreen.UserProfile -> ErrorUserProfileViewModel::class
            }
        }
        ScreenArgument.ExploreScreenArgument -> ExploreViewModel::class
        ScreenArgument.FirstRunScreenArgument -> FirstRunViewModel::class
        is ScreenArgument.FolderScreenArgument -> FolderViewModel::class
        ScreenArgument.HomeOnboardingScreenArgument -> HomeOnboardingViewModel::class
        ScreenArgument.HomeScreenArgument -> HomeViewModel::class
        ScreenArgument.IndexScreenArgument -> IndexViewModel::class
        ScreenArgument.ManageSubscriptionScreenArgument -> null
        ScreenArgument.OssLicensesScreenArgument -> OssLicensesViewModel::class
        is ScreenArgument.PaywallScreenArgument -> PaywallViewModel::class
        ScreenArgument.ProfileScreenArgument -> ProfileViewModel::class
        is ScreenArgument.RemixIdScreenArgument -> null
        is ScreenArgument.RewardAdInternalScreenArgument -> RewardAdInternalViewModel::class
        ScreenArgument.SearchScreenArgument -> SearchInputViewModel::class
        ScreenArgument.SearchResultsScreenArgument -> SearchResultsViewModel::class
        is ScreenArgument.SettingsArgument -> SettingsViewModel::class
        ScreenArgument.SignUpScreenArgument -> SignUpViewModel::class
        is ScreenArgument.ShowcaseAdsScreenArgument -> ShowcaseAdsViewModel::class
        is ScreenArgument.ShowcaseIosNativeFeedScreenArgument -> ShowcaseIosNativeFeedViewModel::class
        is ScreenArgument.ShowcaseTypefaceScreenArgument -> ShowcaseTypefaceViewModel::class
        is ScreenArgument.ShowcaseTypefaceIosScreenArgument -> ShowcaseTypefaceViewModel::class
        is ScreenArgument.WallpaperSingleActionScreenArgument -> WallpaperSingleActionViewModel::class
        is ScreenArgument.WallpaperShowcaseScreenArgument -> WallpaperShowcaseViewModel::class
    }
    return clazz?.let {
        viewModelFactory.create(it, this)
    }
}
