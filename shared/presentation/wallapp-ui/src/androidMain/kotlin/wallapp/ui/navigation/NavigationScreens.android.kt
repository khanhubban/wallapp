package wallapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.app.AppViewModelExtras
import wallapp.content.state.account.AccountViewModel
import wallapp.content.state.artist.ArtistViewModel
import wallapp.content.state.artist.ArtistsViewModel
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
import wallapp.content.state.index.IndexViewModel
import wallapp.content.state.osslicenses.OssLicensesViewModel
import wallapp.content.state.rewardadinternal.RewardAdInternalViewModel
import wallapp.content.state.search.SearchInputViewModel
import wallapp.content.state.settings.SettingsViewModel
import wallapp.content.state.showcase.ads.ShowcaseAdsViewModel
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewModel
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewModel
import wallapp.content.state.signup.SignUpViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.content.state.wallpaper.WallpaperSingleActionViewModel
import wallapp.pixel.render.Render
import wallapp.screen.Screen
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.ArtistIdScreenArgument
import wallapp.screen.ScreenArgument.CollectionIdScreenArgument
import wallapp.screen.ScreenArgument.ConnectionsScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.screen.ScreenManager
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.screen.route
import wallapp.ui.AppScreen
import wallapp.ui.ScreenControllers
import wallapp.ui.ScreenControllersAndroid
import wallapp.viewmodel.ViewModel

@Composable
actual fun NavigationScreens(
    render: Render,
    screenControllers: ScreenControllers,
    modifier: Modifier,
) {
    require(screenControllers is ScreenControllersAndroid)
    val viewModelProviderFactory = screenControllers.viewModelProviderFactory
    val screenManager = screenControllers.screenManager
    val navController = screenControllers.screenNavHostController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val initialScreen = screenControllers.initialScreen

    navBackStackEntry?.destination?.route
        ?.let { Screen.fromRoute(it) }
        ?.also { screenManager.onScreenChanged(it) }

    // Need to initialise the IndexViewModel here to avoid its onCleared() method being called
    // in cases where app is recreated (e.g. after changing app icon) since IndexViewModel's reference
    // is stored in AppViewModel.
    val indexViewModel = remember {
        screenControllers.viewModelFactory.create(IndexViewModel::class)
    }

    NavHost(
        navController = navController,
        startDestination = initialScreen.route(),
        modifier = modifier,
    ) {
        composable(render, Screen.Account) {
            Screen<AccountViewModel>(
                Screen.Account,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Collection) { argument ->
            Screen<CollectionViewModel>(
                Screen.Collection,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyCollectionId] =
                            ScreenArgument.fromJsonString(argument!!) as CollectionIdScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Artist) { argument ->
            Screen<ArtistViewModel>(
                Screen.Artist,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyArtistId] =
                            ScreenArgument.fromJsonString(argument!!) as ArtistIdScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Artists) {
            Screen<ArtistsViewModel>(
                Screen.Artists,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Connections) { argument ->
            Screen<ConnectionsViewModel>(
                Screen.Connections,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyConnectionsArguments] =
                            ScreenArgument.fromJsonString(argument!!) as ConnectionsScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.DataConsent) {
            Screen<DataConsentViewModel>(
                Screen.DataConsent,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.DebugSettings) {
            Screen<DebugViewModel>(
                Screen.DebugSettings,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorAppUpdateRequired) {
            Screen<ErrorAppUpdateRequiredViewModel>(
                Screen.ErrorAppUpdateRequired,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorDownloadFailed) { argument ->
            Screen<ErrorDownloadFailedViewModel>(
                Screen.ErrorDownloadFailed,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorNetwork) { argument ->
            Screen<ErrorNetworkViewModel>(
                Screen.ErrorNetwork,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorPermissionSystemMediaDeniedAndroid) {
            Screen<ErrorPermissionSystemMediaDeniedAndroidViewModel>(
                Screen.ErrorPermissionSystemMediaDeniedAndroid,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorPermissionSystemMediaDeniedIos) {
            Screen<ErrorPermissionSystemMediaDeniedIosViewModel>(
                Screen.ErrorPermissionSystemMediaDeniedIos,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorPurchase) { argument ->
            Screen<ErrorPurchaseViewModel>(
                Screen.ErrorPurchase,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorRemoteDataFetch) {
            Screen<ErrorRemoteDataFetchViewModel>(
                Screen.ErrorRemoteDataFetch,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorRewardAd) { argument ->
            Screen<ErrorRewardAdViewModel>(
                Screen.ErrorRewardAd,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorSignIn) { argument ->
            Screen<ErrorSignInViewModel>(
                Screen.ErrorSignIn,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorSubscriptionExpired) {
            Screen<ErrorSubscriptionExpiredViewModel>(
                Screen.ErrorSubscriptionExpired,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorTemplate) {
            Screen<ErrorTemplateViewModel>(
                Screen.ErrorTemplate,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ErrorUserProfile) { argument ->
            Screen<ErrorUserProfileViewModel>(
                Screen.ErrorUserProfile,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        argument?.also {
                            this[AppViewModelExtras.ExtraKeyErrorScreen] =
                                ScreenArgument.fromJsonString(argument) as ScreenArgument.ErrorScreenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.FirstRun) {
            Screen<FirstRunViewModel>(
                Screen.FirstRun,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Folder) { argument ->
            Screen<FolderViewModel>(
                Screen.Folder,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyFolder] =
                            ScreenArgument.fromJsonString(argument!!) as FolderScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Index) {
            Screen<IndexViewModel>(
                Screen.Index,
                viewModel = indexViewModel,
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.OssLicenses) { argument ->
            Screen<OssLicensesViewModel>(
                Screen.OssLicenses,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyOssLicensesArguments] =
                            ScreenArgument.OssLicensesScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Paywall) { argument ->
            Screen<PaywallViewModel>(
                Screen.Paywall,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyPaywallScreenArgument] =
                            ScreenArgument.fromJsonString(argument!!) as PaywallScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.RewardAdInternal) { argument ->
            Screen<RewardAdInternalViewModel>(
                Screen.RewardAdInternal,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        this[AppViewModelExtras.ExtraKeyRewardAdInternalArguments] =
                            ScreenArgument.RewardAdInternalScreenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Search) { argument ->
            Screen<SearchInputViewModel>(
                Screen.Search,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.Settings) { argument ->
            Screen<SettingsViewModel>(
                Screen.Settings,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        val screenArgument = ScreenArgument.fromJsonString(argument!!)
                        if (screenArgument is ScreenArgument.SettingsArgument) {
                            this[AppViewModelExtras.ExtraKeySettingsArguments] = screenArgument
                        }
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.SignUp) {
            Screen<SignUpViewModel>(
                Screen.SignUp,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ShowcaseAds) {
            Screen<ShowcaseAdsViewModel>(
                Screen.ShowcaseAds,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ShowcaseIosNativeFeed) {
            Screen<ShowcaseIosNativeFeedViewModel>(
                Screen.ShowcaseIosNativeFeed,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.ShowcaseTypeface) {
            Screen<ShowcaseTypefaceViewModel>(
                Screen.ShowcaseTypeface,
                viewModel = viewModel(factory = viewModelProviderFactory),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.WallpaperShowcase) { argument ->
            Screen<WallpaperShowcaseViewModel>(
                Screen.WallpaperShowcase,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        val screenArgument = ScreenArgument.fromJsonString(argument!!)
                        require(screenArgument is WallpaperShowcaseScreenArgument)
                        this[AppViewModelExtras.ExtraKeyWallpaperShowcaseArguments] = screenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }

        composable(render, Screen.WallpaperSingleAction) { argument ->
            Screen<WallpaperSingleActionViewModel>(
                Screen.WallpaperSingleAction,
                viewModel = viewModel(
                    factory = viewModelProviderFactory,
                    extras = MutableCreationExtras().apply {
                        val screenArgument = ScreenArgument.fromJsonString(argument!!)
                        require(screenArgument is ScreenArgument.WallpaperSingleActionScreenArgument)
                        this[AppViewModelExtras.ExtraKeyWallpaperSingleActionArguments] =
                            screenArgument
                    },
                ),
                screenManager,
            ) {
                AppScreen(render, it.viewState, modifier)
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> Screen(
    screen: Screen,
    viewModel: T,
    screenManager: ScreenManager,
    content: @Composable (T) -> Unit,
) {
    screenManager.registerController(
        screen,
        controller = if (viewModel is ScreenSystemBarControllerHolder) {
            viewModel.screenSystemBarController
        } else {
            MutableStateFlow(ScreenSystemBarController.Default)
        },
    )
    content.invoke(viewModel)
}