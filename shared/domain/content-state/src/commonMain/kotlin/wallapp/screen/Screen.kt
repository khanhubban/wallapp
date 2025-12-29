package wallapp.screen

import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class Screen(
    val routeFormat: String,
    val routeBuilder: (String?) -> String = { routeFormat },
    val stringArgumentName: String? = null,
    val screenTransitionType: ScreenTransitionType = ScreenTransitionType.SlideVertically,
    internal var screenTransitionItem: Any? = null,
    val nativeNavigationSupported: Boolean = true,
    val showAsModalSheet: Boolean = false,
) {
    data object Account : Screen(routeFormat = "account")

    data object Artist : Screen(
        routeFormat = "artist/{id}",
        routeBuilder = { argument -> "artist/$argument" },
        stringArgumentName = "id",
    )

    data object Artists: Screen(routeFormat = "artists")

    data object Collection : Screen(
        routeFormat = "Collection/{id}",
        routeBuilder = { argument -> "Collection/$argument" },
        stringArgumentName = "id",
    )

    data object CollectionAction : Screen(
        routeFormat = "collection-action/{id}",
        routeBuilder = { argument -> "collection-action/$argument" },
        stringArgumentName = "id",
        showAsModalSheet = true,
    )

    data object Connections : Screen(
        routeFormat = "connections/{id}",
        routeBuilder = { argument -> "connections/$argument" },
        stringArgumentName = "id",
    )

    data object DataConsent : Screen(
        routeFormat = "consent",
        screenTransitionType = ScreenTransitionType.None,
        nativeNavigationSupported = false,
    )

    data object DebugSettings : Screen(
        routeFormat = "DebugSettings",
    )

    data object ErrorAppUpdateRequired : Screen(
        routeFormat = "error/app-update-required",
    )

    data object ErrorDownloadFailed : Screen(
        routeFormat = "error/download-failed/{id}",
        routeBuilder = { argument -> "error/download-failed/$argument" },
        stringArgumentName = "id",
    )

    data object ErrorNetwork : Screen(
        routeFormat = "error/network/{id}",
        routeBuilder = { argument -> "error/network/$argument" },
        stringArgumentName = "id",
    )

    data object ErrorPermissionSystemMediaDeniedAndroid : Screen(
        routeFormat = "error/permission-system-media-denied-android",
    )

    data object ErrorPermissionSystemMediaDeniedIos : Screen(
        routeFormat = "error/permission-system-media-denied-ios",
    )

    data object ErrorPurchase : Screen(
        routeFormat = "error/purchase/{id}",
        routeBuilder = { argument -> "error/purchase/$argument" },
        stringArgumentName = "id",
    )

    data object ErrorRemoteDataFetch : Screen(
        routeFormat = "error/remote-data-fetch",
    )

    data object ErrorRewardAd : Screen(
        routeFormat = "error/reward-ad/{id}",
        routeBuilder = { argument -> "error/reward-ad/$argument" },
        stringArgumentName = "id",
    )

    data object ErrorSignIn : Screen(
        routeFormat = "error/sign-in/{id}",
        routeBuilder = { argument -> "error/sign-in/$argument" },
        stringArgumentName = "id",
    )

    data object ErrorSubscriptionExpired : Screen(
        routeFormat = "error/subscription-expired",
    )

    data object ErrorTemplate : Screen(
        routeFormat = "error/template",
    )

    data object ErrorUserProfile : Screen(
        routeFormat = "error/user-profile/{id}",
        routeBuilder = { argument -> "error/user-profile/$argument" },
        stringArgumentName = "id",
    )

    data object Explore : Screen(
        routeFormat = "explore"
    )

    data object FirstRun : Screen(
        routeFormat = "first-run"
    )

    data object Folder : Screen(
        routeFormat = "folder/{id}",
        routeBuilder = { argument -> "folder/$argument" },
        stringArgumentName = "id",
    )

    data object Home : Screen(
        routeFormat = "home"
    )

    data object HomeOnboarding : Screen(
        routeFormat = "home/onboarding"
    )

    data object Index : Screen(
        routeFormat = "index",
        screenTransitionType = ScreenTransitionType.None,
    )

    data object OssLicenses : Screen(
        routeFormat = "oss-licenses",
    )

    data object Paywall : Screen(
        routeFormat = "paywall/{argument}",
        routeBuilder = { argument -> "paywall/$argument" },
        stringArgumentName = "argument",
    )

    data object Profile : Screen(
        routeFormat = "profile/{id}",
    )

    data object RewardAdInternal : Screen(
        routeFormat = "reward-ad-internal",
    )

    // Only for iOS, needed to workaround the out of bounds issue
    data object RewardAdLoading : Screen(
        routeFormat = "reward-ad-loading-screen",
    )

    data object Search : Screen(
        routeFormat = "search",
        screenTransitionType = ScreenTransitionType.Fade,
        nativeNavigationSupported = false,
    )

    data object SearchResults: Screen(
        routeFormat = "search-results",
    )

    data object Settings : Screen(
        routeFormat = "settings/{data}",
        routeBuilder = { argument -> "settings/$argument" },
        stringArgumentName = "data",
        nativeNavigationSupported = false,
    )

    data object SignUp : Screen(
        routeFormat = "sign-up",
        screenTransitionType = ScreenTransitionType.None,
        nativeNavigationSupported = false
    )

    data object ShowcaseAds : Screen(
        routeFormat = "showcase/ads",
    )

    data object ShowcaseIosNativeFeed : Screen(
        routeFormat = "showcase/native-feed",
    )

    data object ShowcaseTypeface : Screen(
        routeFormat = "showcase/typeface",
    )

    data object ShowcaseTypefaceIos : Screen(
        routeFormat = "showcase/typeface-ios",
    )

    data object WallpaperShowcase : Screen(
        routeFormat = "w/{id}",
        routeBuilder = { argument -> "w/$argument" },
        stringArgumentName = "id",
        showAsModalSheet = true,
    )

    data object WallpaperSingleAction : Screen(
        routeFormat = "wallpaper-single-action/{id}",
        routeBuilder = { argument -> "wallpaper-single-action/$argument" },
        stringArgumentName = "id",
        showAsModalSheet = true,
    )

    companion object {
        fun fromRoute(route: String): Screen? {
            return when (route) {
                Account.routeFormat -> Account
                Artist.routeFormat -> Artist
                Artists.routeFormat -> Artists
                Collection.routeFormat -> Collection
                CollectionAction.routeFormat -> CollectionAction
                Connections.routeFormat -> Connections
                DataConsent.routeFormat -> DataConsent
                DebugSettings.routeFormat -> DebugSettings
                ErrorAppUpdateRequired.routeFormat -> ErrorAppUpdateRequired
                ErrorDownloadFailed.routeFormat -> ErrorDownloadFailed
                ErrorNetwork.routeFormat -> ErrorNetwork
                ErrorPermissionSystemMediaDeniedAndroid.routeFormat -> ErrorPermissionSystemMediaDeniedAndroid
                ErrorPermissionSystemMediaDeniedIos.routeFormat -> ErrorPermissionSystemMediaDeniedIos
                ErrorPurchase.routeFormat -> ErrorPurchase
                ErrorRemoteDataFetch.routeFormat -> ErrorRemoteDataFetch
                ErrorRewardAd.routeFormat -> ErrorRewardAd
                ErrorSignIn.routeFormat -> ErrorSignIn
                ErrorSubscriptionExpired.routeFormat -> ErrorSubscriptionExpired
                ErrorTemplate.routeFormat -> ErrorTemplate
                ErrorUserProfile.routeFormat -> ErrorUserProfile
                Folder.routeFormat -> Folder
                FirstRun.routeFormat -> FirstRun
                Index.routeFormat -> Index
                OssLicenses.routeFormat -> OssLicenses
                Paywall.routeFormat -> Paywall
                RewardAdInternal.routeFormat -> RewardAdInternal
                Search.routeFormat -> Search
                Settings.routeFormat -> Settings
                SignUp.routeFormat -> SignUp
                ShowcaseAds.routeFormat -> ShowcaseAds
                ShowcaseIosNativeFeed.routeFormat -> ShowcaseIosNativeFeed
                ShowcaseTypeface.routeFormat -> ShowcaseTypeface
                WallpaperShowcase.routeFormat -> WallpaperShowcase
                WallpaperSingleAction.routeFormat -> WallpaperSingleAction
                else -> throw IllegalArgumentException("Unknown route: $route")
            }
        }
    }
}

fun Screen.route(argument: String? = null): String = routeBuilder.invoke(argument)

val networkErrorScreens = listOf(Screen.ErrorNetwork, Screen.ErrorRemoteDataFetch)