package wallapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.ViewModel
import wallapp.content.state.account.AccountViewModel
import wallapp.content.state.artist.ArtistViewModel
import wallapp.content.state.artist.ArtistsViewModel
import wallapp.content.state.collection.CollectionActionViewModel
import wallapp.content.state.collection.CollectionViewModel
import wallapp.content.state.connections.ConnectionsViewModel
import wallapp.content.state.dataconsent.DataConsentViewModel
import wallapp.content.state.debug.DebugViewModel
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
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewModel
import wallapp.content.state.signup.SignUpViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.content.state.wallpaper.WallpaperSingleActionViewModel
import wallapp.pixel.render.Render
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.ui.AppScreen
import wallapp.ui.ScreenControllers


@Composable
fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
    initialScreen: Screen,
    navigator: Navigator,
    modifier: Modifier,
) {
    val viewModelFactory = screenControllers.viewModelFactory
    val screenManager = screenControllers.screenManager

    val indexViewModel = remember {
        viewModelFactory.create(IndexViewModel::class)
    }

    fun register(screen: Screen, viewModel: Any) {
        if (viewModel !is ViewModel) return
        screenManager.registerController(
            screen,
            controller = if (viewModel is ScreenSystemBarControllerHolder) {
                viewModel.screenSystemBarController
            } else {
                MutableStateFlow(ScreenSystemBarController.Default)
            },
        )
//        screenManager.onScreenChanged(screen, via = "register")
    }

    navigator.currentEntry.collectAsState(initial = null).value
        ?.let { Screen.fromRoute(it.route.route) }
        ?.also { screenManager.onScreenChanged(it) }

    NavHost(
        navigator = navigator,
        initialRoute = initialScreen.routeFormat,
    ) {
        scene(Screen.Account.routeFormat) {
            val viewModel = viewModelFactory.create(
                AccountViewModel::class,
                null,
            )
            register(Screen.Account, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Artist.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                ArtistViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.Artist, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Artists.routeFormat) {
            val viewModel = viewModelFactory.create(
                ArtistsViewModel::class,
                null,
            )
            register(Screen.Artists, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Collection.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                CollectionViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.Collection, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.CollectionAction.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                CollectionActionViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.CollectionAction, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Connections.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                ConnectionsViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.Connections, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.DataConsent.routeFormat) {
            val viewModel = viewModelFactory.create(
                DataConsentViewModel::class,
                null,
            )
            register(Screen.DataConsent, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.DebugSettings.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                DebugViewModel::class,
                null,
            )
            register(Screen.DebugSettings, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorAppUpdateRequired.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorAppUpdateRequiredViewModel::class,
            )
            register(Screen.ErrorAppUpdateRequired, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorDownloadFailed.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                ErrorDownloadFailedViewModel::class,
            )
            register(Screen.ErrorDownloadFailed, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorNetwork.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorNetworkViewModel::class,
            )
            register(Screen.ErrorNetwork, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorPermissionSystemMediaDeniedAndroid.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorPermissionSystemMediaDeniedAndroidViewModel::class,
            )
            register(Screen.ErrorPermissionSystemMediaDeniedAndroid, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorPermissionSystemMediaDeniedIos.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorPermissionSystemMediaDeniedIosViewModel::class,
            )
            register(Screen.ErrorPermissionSystemMediaDeniedIos, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorPurchase.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorPurchaseViewModel::class,
            )
            register(Screen.ErrorPurchase, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorRemoteDataFetch.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorRemoteDataFetchViewModel::class,
            )
            register(Screen.ErrorRemoteDataFetch, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorRewardAd.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorRewardAdViewModel::class,
            )
            register(Screen.ErrorRewardAd, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorSignIn.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorSignInViewModel::class,
            )
            register(Screen.ErrorSignIn, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorSubscriptionExpired.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorSubscriptionExpiredViewModel::class,
            )
            register(Screen.ErrorSubscriptionExpired, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorTemplate.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorTemplateViewModel::class,
            )
            register(Screen.ErrorTemplate, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ErrorUserProfile.routeFormat) {
            val viewModel = viewModelFactory.create(
                ErrorUserProfileViewModel::class,
            )
            register(Screen.ErrorUserProfile, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Folder.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                FolderViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.Folder, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.FirstRun.routeFormat) {
            val viewModel = viewModelFactory.create(
                FirstRunViewModel::class,
                null,
            )
            register(Screen.FirstRun, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Home.routeFormat) {
            val viewModel = viewModelFactory.create(
                HomeViewModel::class,
                null,
            )
            register(Screen.Home, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.HomeOnboarding.routeFormat) {
            val viewModel = viewModelFactory.create(
                HomeOnboardingViewModel::class,
                null,
            )
            register(Screen.HomeOnboarding, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Index.routeFormat) {
            register(Screen.Index, indexViewModel)

            AppScreen(render, indexViewModel.viewState, modifier)
        }

        scene(Screen.OssLicenses.routeFormat) {
            val viewModel = viewModelFactory.create(
                OssLicensesViewModel::class,
                ScreenArgument.OssLicensesScreenArgument,
            )
            register(Screen.OssLicenses, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.RewardAdInternal.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                RewardAdInternalViewModel::class,
                ScreenArgument.RewardAdInternalScreenArgument,
            )
            register(Screen.RewardAdInternal, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Paywall.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                PaywallViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["argument"] as String),
            )
            register(Screen.Paywall, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Profile.routeFormat) {
            val viewModel = viewModelFactory.create(
                ProfileViewModel::class,
                null,
            )
            register(Screen.Profile, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Search.routeFormat) {
            val viewModel = viewModelFactory.create(
                SearchInputViewModel::class,
                null,
            )
            register(Screen.Search, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.SearchResults.routeFormat) {
            val viewModel = viewModelFactory.create(
                SearchResultsViewModel::class,
                null,
            )
            register(Screen.SearchResults, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.Settings.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                SettingsViewModel::class,
                null,
            )
            register(Screen.Settings, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.SignUp.routeFormat) {
            val viewModel = viewModelFactory.create(
                SignUpViewModel::class,
                null,
            )
            register(Screen.SignUp, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ShowcaseAds.routeFormat) {
            val viewModel = viewModelFactory.create(
                ShowcaseAdsViewModel::class,
                null,
            )
            register(Screen.ShowcaseAds, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.ShowcaseTypeface.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                ShowcaseTypefaceViewModel::class,
                null,
            )
            register(Screen.ShowcaseTypeface, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.WallpaperShowcase.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                WallpaperShowcaseViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.WallpaperShowcase, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }

        scene(Screen.WallpaperSingleAction.routeFormat) { backStackEntry ->
            val viewModel = viewModelFactory.create(
                WallpaperSingleActionViewModel::class,
                ScreenArgument.fromJsonString(backStackEntry.pathMap["id"] as String),
            )
            register(Screen.WallpaperSingleAction, viewModel)

            AppScreen(render, viewModel.viewState, modifier)
        }
    }
}
