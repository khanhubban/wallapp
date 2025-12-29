package wallapp.viewmodel

import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.account.state.signin.SignInViewModel
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdWatchManager
import wallapp.ads.reward.RewardAdManager
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.app.AppStateManager
import wallapp.app.AppViewModel
import wallapp.app.AppViewModelFactory
import wallapp.appconfig.AppConfig
import wallapp.apphistory.AppHistoryManager
import wallapp.appvisibility.AppVisibility
import wallapp.bottomsheet.BottomSheetViewStateProviderManager
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.account.AccountViewModel
import wallapp.content.state.artist.ArtistViewModel
import wallapp.content.state.artist.ArtistViewStateFactory
import wallapp.content.state.artist.ArtistsViewModel
import wallapp.content.state.collection.CollectionActionViewModel
import wallapp.content.state.collection.CollectionViewModel
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.connections.ConnectionsViewModel
import wallapp.content.state.dataconsent.DataConsentViewModel
import wallapp.content.state.debug.DebugViewModel
import wallapp.content.state.error.ErrorViewStateMapper
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
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.osslicenses.OssLicensesViewModel
import wallapp.content.state.profile.ProfileViewModel
import wallapp.content.state.rewardadinternal.RewardAdInternalViewModel
import wallapp.content.state.search.SearchInputViewModel
import wallapp.content.state.search.SearchResultsViewModel
import wallapp.content.state.settings.SettingViewStateFactory
import wallapp.content.state.settings.SettingsViewModel
import wallapp.content.state.showcase.ads.ShowcaseAdsViewModel
import wallapp.content.state.showcase.ads.ShowcaseAdsViewStateMapper
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewModel
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewStateMapper
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewModel
import wallapp.content.state.signup.SignUpViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewModel
import wallapp.content.state.upgrade.plus.paywall.PaywallViewStateMapper
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewStateFactory
import wallapp.content.state.wallpaper.WallpaperSingleActionViewModel
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentRepository
import wallapp.data.folder.FolderStateRepository
import wallapp.data.following.FollowingRepository
import wallapp.data.osslicense.OssLicenseRepository
import wallapp.di.Lazy
import wallapp.entitlement.EntitlementRepository
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.license.state.LicenseStateRepository
import wallapp.network.NetworkRefreshManager
import wallapp.network.NetworkState
import wallapp.permission.SystemPermissionManager
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchaseManager
import wallapp.reflect.isSubclassOf
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.ArtistIdScreenArgument
import wallapp.screen.ScreenArgument.CollectionIdScreenArgument
import wallapp.screen.ScreenArgument.ConnectionsScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.search.SearchSessionManager
import wallapp.search.content.SearchContentRepository
import wallapp.settings.SettingManager
import wallapp.string.quote
import wallapp.system.navigation.SystemNavigator
import wallapp.system.share.SystemShareManager
import wallapp.theme.ThemeManager
import wallapp.time.EpochTicker
import wallapp.time.TimeRepository
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateMapper
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.wallpaper.actionbutton.CollectionActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonManager
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager
import wallapp.worker.BackgroundWorkScheduler
import kotlin.reflect.KClass

class ViewModelFactoryCommon(
    private val contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val viewStateFactoryWallpaper: WallpaperShowcaseViewStateFactory,
    private val viewStateFactoryArtist: ArtistViewStateFactory,
    private val viewStateMapper: ViewStateMapper,
    private val viewStateMapperError: ErrorViewStateMapper,
    private val viewStateMapperPaywall: PaywallViewStateMapper,
    private val viewStateMapperShowcaseAds: ShowcaseAdsViewStateMapper,
    private val viewStateRefresher: ViewStateRefresher,
    private val viewEventFactory: ViewEventFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val messageBarManager: MessageBarManager,
    private val settingManager: SettingManager,
    private val settingViewStateFactory: SettingViewStateFactory,
    private val appConfig: AppConfig,
    private val appHistoryManager: AppHistoryManager,
    private val collectionActionButtonManager: CollectionActionButtonManager,
    private val contentColorManager: ContentColorManager,
    private val contentCacheManager: ContentCacheManager,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val followingRepository: FollowingRepository,
    private val folderStateRepository: FolderStateRepository,
    private val rewardAdManager: RewardAdManager,
    private val rewardAdConfig: RewardAdConfig,
    private val rewardAdWatchManager: RewardAdWatchManager,
    private val rewardAdInternalPlaybackManager: RewardAdInternalPlaybackManager,
    private val inAppBrowserManager: InAppBrowserManager,
    private val systemNavigator: SystemNavigator,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemShareManager: SystemShareManager,
    private val wallpaperActionButtonManager: WallpaperActionButtonManager,
    private val wallpaperShowcaseActionButtonManager: WallpaperShowcaseActionButtonManager,
    private val activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val backgroundWorkScheduler: BackgroundWorkScheduler,
    private val accountManager: AccountManager,
    private val accountDataRepository: AccountDataRepository,
    private val profileImageManager: ProfileImageManager,
    private val privacyMessagingManager: PrivacyMessagingManager,
    private val licenseStateRepository: LicenseStateRepository,
    private val searchSessionManager: SearchSessionManager,
    private val searchContentRepository: SearchContentRepository,
    private val entitlementRepository: EntitlementRepository,
    private val purchaseManager: PurchaseManager,
    private val ossLicenseRepository: OssLicenseRepository,
    private val showcaseIosNativeFeedViewStateMapper: ShowcaseIosNativeFeedViewStateMapper,
    private val globalOverlayManager: GlobalOverlayManager,
    private val networkState: NetworkState,
    private val timeRepository: TimeRepository,
    private val epochTicker: EpochTicker,
    private val appVisibility: AppVisibility,
    private val bottomSheetViewStateProviderManager: BottomSheetViewStateProviderManager,
    private val _appViewModelFactory: Lazy<AppViewModelFactory>,
    private val _appViewModel: Lazy<AppViewModel>,
    private val themeManager: ThemeManager,
    private val alertManager: AlertManager,
    private val networkRefreshManager: NetworkRefreshManager,
) : ViewModelFactory {

    private val appViewModelFactory by lazy { _appViewModelFactory.get() }
    private val appViewModel by lazy { _appViewModel.get() }
    private val appStateManager: AppStateManager
        get() = appViewModel

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extra: Any?, onClearedCallback: (() -> Unit)?): T {
        val viewModel = when {
            modelClass.isSubclassOf(AppViewModel::class) -> {
                appViewModel as T
            }

            modelClass.isSubclassOf(HomeOnboardingViewModel::class) -> {
                appViewModel.indexViewModel.homeOnboardingViewModel as T
            }

            modelClass.isSubclassOf(HomeViewModel::class) -> {
                appViewModel.indexViewModel.homeViewModel as T
            }

            modelClass.isSubclassOf(IndexViewModel::class) -> {
                appViewModel.indexViewModel as T
            }

            modelClass.isSubclassOf(ExploreViewModel::class) -> {
                appViewModel.indexViewModel.exploreViewModel as T
            }

            modelClass.isSubclassOf(ProfileViewModel::class) -> {
                appViewModel.indexViewModel.profileViewModel as T
            }

            modelClass.isSubclassOf(WallpaperSingleActionViewModel::class) -> {
                WallpaperSingleActionViewModel(
                    argument = extra.let {
                        val argument = it as? ScreenArgument.WallpaperSingleActionScreenArgument
                        require(argument is ScreenArgument.WallpaperSingleActionScreenArgument)
                        argument
                    },
                    rewardAdConfig,
                    contentRepository,
                    entitlementRepository,
                    appStateManager,
                    rewardAdManager,
                    rewardAdWatchManager,
                    wallpaperActionButtonManager,
                    wallpaperSystemPhotoStatusManager,
                    networkState,
                    menuItemFactory,
                    viewStateFactory,
                    viewEventFactory,
                    strings,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(WallpaperShowcaseViewModel::class) -> {
                WallpaperShowcaseViewModel(
                    argument = extra.let {
                        val argument = it as? WallpaperShowcaseScreenArgument
                        require(argument is WallpaperShowcaseScreenArgument)
                        argument
                    },
                    activeWallpaperDownloadManager,
                    contentRepository,
                    viewStateRefresher,
                    entitlementRepository,
                    appStateManager,
                    appHistoryManager,
                    contentColorManager,
                    rewardAdConfig,
                    rewardAdWatchManager,
                    followingRepository,
                    wallpaperShowcaseActionButtonManager,
                    wallpaperDownloadManager,
                    wallpaperSystemPhotoStatusManager,
                    systemShareManager,
                    viewStateFactoryWallpaper,
                    viewSpecArbitrator,
                    appConfig,
                    strings,
                    searchContentRepository,
                    bottomSheetViewStateProviderManager,
                    appViewModelFactory,
                ) as T
            }

            modelClass.isSubclassOf(SignUpViewModel::class) -> {
                SignUpViewModel(
                    contentRepository,
                    viewStateFactory,
                    signInViewModel = create(SignInViewModel::class, extra = null),
                ) as T
            }

            modelClass.isSubclassOf(ArtistViewModel::class) -> {
                ArtistViewModel(
                    artistId = (extra as ArtistIdScreenArgument).artistId,
                    contentRepository,
                    appHistoryManager,
                    messageBarManager,
                    viewStateFactoryArtist,
                    appStateManager,
                    contentPrefetcherFactory,
                    contentColorManager,
                    systemNavigator,
                    viewStateRefresher,
                    followingRepository,
                ) as T
            }

            modelClass.isSubclassOf(ArtistsViewModel::class) -> {
                ArtistsViewModel(
                    contentRepository,
                    viewStateFactory,
                    contentColorManager,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(CollectionViewModel::class) -> {
                CollectionViewModel(
                    collectionId = (extra as CollectionIdScreenArgument).collectionId,
                    firstWallpaperId = extra.firstWallpaperId,
                    contentRepository,
                    appHistoryManager,
                    activeWallpaperDownloadManager,
                    backgroundWorkScheduler,
                    messageBarManager,
                    viewStateFactory,
                    contentColorManager,
                    viewEventFactory,
                    contentPrefetcherFactory,
                    systemPermissionManager,
                    appStateManager,
                    bottomSheetViewStateProviderManager,
                    appViewModelFactory,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(FolderViewModel::class) -> {
                FolderViewModel(
                    folderId = (extra as FolderScreenArgument).folderId,
                    folderStateRepository,
                    appStateManager,
                    messageBarManager,
                    viewStateFactory,
                    contentColorManager,
                    contentPrefetcherFactory,
                    themeManager,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(CollectionActionViewModel::class) -> {
                CollectionActionViewModel(
                    argument = extra.let {
                        val argument = it as? ScreenArgument.CollectionActionScreenArgument
                        require(argument is ScreenArgument.CollectionActionScreenArgument)
                        argument
                    },
                    activeWallpaperDownloadManager,
                    backgroundWorkScheduler,
                    contentRepository,
                    menuItemFactory,
                    purchaseManager,
                    collectionActionButtonManager,
                    systemPermissionManager,
                    viewStateFactory,
                    viewEventFactory,
                    strings,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ConnectionsViewModel::class) -> {
                ConnectionsViewModel(
                    selectedConnectionType = (extra as ConnectionsScreenArgument).initialConnectionType,
                    contentRepository,
                    viewStateRefresher,
                    viewStateFactory,
                ) as T
            }

            modelClass.isSubclassOf(ErrorAppUpdateRequiredViewModel::class) -> {
                ErrorAppUpdateRequiredViewModel(
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorDownloadFailedViewModel::class) -> {
                ErrorDownloadFailedViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorNetworkViewModel::class) -> {
                ErrorNetworkViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    appStateManager,
                    networkState,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorPermissionSystemMediaDeniedAndroidViewModel::class) -> {
                ErrorPermissionSystemMediaDeniedAndroidViewModel(
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorPermissionSystemMediaDeniedIosViewModel::class) -> {
                ErrorPermissionSystemMediaDeniedIosViewModel(
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorPurchaseViewModel::class) -> {
                ErrorPurchaseViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorRemoteDataFetchViewModel::class) -> {
                ErrorRemoteDataFetchViewModel(
                    appStateManager,
                    networkRefreshManager,
                    viewStateMapperError,
                    viewStateRefresher,
                    viewEventFactory,
                ) as T
            }

            modelClass.isSubclassOf(ErrorRewardAdViewModel::class) -> {
                ErrorRewardAdViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    contentRepository,
                    viewStateFactory,
                    appStateManager,
                    networkState,
                    privacyMessagingManager,
                    systemNavigator,
                    strings,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorSignInViewModel::class) -> {
                ErrorSignInViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorSubscriptionExpiredViewModel::class) -> {
                ErrorSubscriptionExpiredViewModel(
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                    entitlementRepository,
                ) as T
            }

            modelClass.isSubclassOf(ErrorTemplateViewModel::class) -> {
                ErrorTemplateViewModel(
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ErrorUserProfileViewModel::class) -> {
                ErrorUserProfileViewModel(
                    argument = (extra as ScreenArgument.ErrorScreenArgument),
                    appStateManager,
                    viewStateMapperError,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(FirstRunViewModel::class) -> {
                FirstRunViewModel(
                    appStateManager,
                    contentRepository,
                    viewStateFactory,
                    viewStateMapper,
                    signInViewModel = create(SignInViewModel::class, extra = null),
                    accountManager,
                    accountDataRepository,
                    entitlementRepository,
                    contentCacheManager,
                    viewEventFactory,
                    viewStateRefresher,
                    systemPermissionManager,
                    systemNavigator,
                ) as T
            }

            modelClass.isSubclassOf(SettingsViewModel::class) -> {
                SettingsViewModel(
                    argument = extra as ScreenArgument.SettingsArgument,
                    settingViewStateFactory,
                    settingManager,
                    menuItemFactory,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(DebugViewModel::class) -> {
                DebugViewModel(
                    accountManager,
                    settingViewStateFactory,
                    contentRepository,
                    menuItemFactory,
                    strings,
                    viewStateFactory,
                    viewStateRefresher,
                    networkState,
                ) as T
            }

            modelClass.isSubclassOf(SearchInputViewModel::class) -> {
                SearchInputViewModel(
                    searchSessionManager,
                    viewStateFactory,
                    viewEventFactory,
                    appStateManager,
                    viewStateRefresher,
                    themeManager,
                ) as T
            }

            modelClass.isSubclassOf(ShowcaseAdsViewModel::class) -> {
                ShowcaseAdsViewModel(
                    contentRepository,
                    viewStateMapperShowcaseAds,
                ) as T
            }

            modelClass.isSubclassOf(ShowcaseTypefaceViewModel::class) -> {
                ShowcaseTypefaceViewModel(
                    viewStateFactory,
                ) as T
            }

            modelClass.isSubclassOf(DataConsentViewModel::class) -> {
                DataConsentViewModel(
                    contentRepository,
                    viewStateFactory,
                    accountDataRepository,
                ) as T
            }

            modelClass.isSubclassOf(SignInViewModel::class) -> {
                SignInViewModel(
                    appStateManager,
                    viewStateFactory,
                    accountManager,
                    globalOverlayManager,
                    strings,
                    networkState,
                ) as T
            }

            modelClass.isSubclassOf(AccountViewModel::class) -> {
                AccountViewModel(
                    appStateManager,
                    alertManager,
                    viewStateFactory,
                    viewStateRefresher,
                    accountManager,
                    accountDataRepository,
                    profileImageManager,
                    licenseStateRepository,
                    globalOverlayManager,
                    contentColorManager,
                    strings,
                    privacyMessagingManager,
                    systemNavigator,
                    systemPermissionManager,
                ) as T
            }

            modelClass.isSubclassOf(OssLicensesViewModel::class) -> {
                OssLicensesViewModel(
                    ossLicenseRepository,
                    settingViewStateFactory,
                    menuItemFactory,
                    strings,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(PaywallViewModel::class) -> {
                PaywallViewModel(
                    argument = extra as PaywallScreenArgument,
                    entitlementRepository,
                    viewStateMapperError,
                    viewStateMapperPaywall,
                    purchaseManager,
                    appStateManager,
                    viewEventFactory,
                    alertManager,
                    strings,
                ) as T
            }

            modelClass.isSubclassOf(RewardAdInternalViewModel::class) -> {
                RewardAdInternalViewModel(
                    rewardAdInternalPlaybackManager,
                    viewStateFactory,
                    inAppBrowserManager,
                    timeRepository,
                    epochTicker,
                    appVisibility,
                    alertManager,
                ) as T
            }

            modelClass.isSubclassOf(SearchResultsViewModel::class) -> {
                SearchResultsViewModel(
                    searchSessionManager,
                    viewStateFactory,
                    contentPrefetcherFactory,
                    contentRepository,
                    appStateManager,
                    networkRefreshManager,
                    viewStateRefresher,
                ) as T
            }

            modelClass.isSubclassOf(ShowcaseIosNativeFeedViewModel::class) -> {
                ShowcaseIosNativeFeedViewModel(
                    contentRepository,
                    showcaseIosNativeFeedViewStateMapper,
                    viewStateRefresher,
                ) as T
            }

            else -> throw IllegalArgumentException("Support for ${modelClass.simpleName.quote()} must be added")
        }
        if (onClearedCallback != null) {
            viewModel.updateOnClearedCallback(onClearedCallback)
        }
        return viewModel
    }
}