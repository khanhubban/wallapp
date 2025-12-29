package wallapp.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import org.koin.core.scope.Scope
import platform.Foundation.NSUserDefaults
import wallapp.account.data.AccountDataManager
import wallapp.account.data.AccountDataManagerDefault
import wallapp.account.data.AccountDataRepository
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdConfigDefault
import wallapp.ad.reward.internal.RewardAdInternalNavigatorCompat
import wallapp.ad.reward.internal.RewardAdInternalPlaybackManagerDefault
import wallapp.ads.AdSourceInitializer
import wallapp.ads.AdUnitIds
import wallapp.ads.inline.support.InlineAdConfigFactories
import wallapp.ads.inline.support.InlineAdItemFactories
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.RewardAdPlaybackManagerIos
import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.appconfig.AppPlatformConfig
import wallapp.appicon.AppIconManager
import wallapp.appicon.AppIconManagerIos
import wallapp.appvisibility.AppVisibility
import wallapp.appvisibility.AppVisibilityIos
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.firebase.FirebaseAuthManagerFirebase
import wallapp.auth.firebase.FirebaseAuthManagerNoOp
import wallapp.auth.firebase.FirebaseAuthManagerPreset
import wallapp.auth.google.GoogleAuthCoordinatorForIos
import wallapp.auth.google.GoogleAuthManager
import wallapp.auth.google.GoogleAuthManagerIos
import wallapp.billing.BillingManager
import wallapp.billing.BillingManagerRevenueCatIos
import wallapp.billing.revenuecat.RevenueCatManager
import wallapp.billing.revenuecat.RevenueCatManagerIos
import wallapp.billing.revenuecat.RevenueCatManagerNoOp
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.billing.revenuecat.RevenueCatUserManagerNoOp
import wallapp.buildconfig.BuildConfig
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.CoroutineContextsDispatchers
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingCrashlyticsIos
import wallapp.data.DataRepository
import wallapp.data.DataRepositoryDefault
import wallapp.data.content.ContentCacheConfig
import wallapp.data.content.ContentCacheConfigDefault
import wallapp.data.favorite.AccountDataRepositoryFirebaseMobile
import wallapp.deeplink.DeepLinkManager
import wallapp.deeplink.DeepLinkManagerDefault
import wallapp.download.FirebaseStorageDownloaderBundled
import wallapp.device.DeviceId
import wallapp.device.DeviceIdMock
import wallapp.device.DeviceSpec
import wallapp.device.DeviceSpecIos
import wallapp.device.refreshrate.DeviceRefreshRate
import wallapp.device.refreshrate.DeviceRefreshRateMock
import wallapp.device.state.DeviceState
import wallapp.device.state.DeviceStateMock
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.FirebaseStorageDownloaderIos
import wallapp.download.UrlDownloader
import wallapp.download.UrlDownloaderIos
import wallapp.entitlement.EntitlementRepository
import wallapp.entitlement.EntitlementRepositoryDefault
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.entitlement.EntitlementSkuSpecsAppStore
import wallapp.image.host.ImageHostPlatformConfig
import wallapp.image.host.ImageHostPlatformConfigIos
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderSeiko
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.inappbrowser.InAppBrowserManagerDelegate
import wallapp.inappreview.InAppReviewManager
import wallapp.inappreview.InAppReviewManagerIos
import wallapp.initializer.module.ModuleInitializer
import wallapp.instantapp.InstantAppManager
import wallapp.instantapp.InstantAppManagerNoOp
import wallapp.interop.InteropFactory
import wallapp.license.state.LicenseStateSessionManager
import wallapp.license.state.LicenseStateSessionManagerActive
import wallapp.messaging.CloudMessagingManager
import wallapp.messaging.CloudMessagingManagerIos
import wallapp.navigation.AppNavigator
import wallapp.navigation.AppNavigatorDefault
import wallapp.navigation.CurrentScreenProvider
import wallapp.navigation.CurrentScreenProviderIos
import wallapp.network.NetworkState
import wallapp.network.NetworkStateIos
import wallapp.network.NetworkUserManager
import wallapp.network.NetworkUserManagerFirebase
import wallapp.network.NetworkUserManagerPreset
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertManagerIos
import wallapp.pixel.view.UIKitFactory
import wallapp.preferences.PreferenceDefinitions
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.privacymessaging.PrivacyMessagingManagerIos
import wallapp.process.bridge.ProcessBridge
import wallapp.process.bridge.ProcessBridgeMock
import wallapp.process.bus.ProcessBusAlt
import wallapp.process.bus.ProcessBusMain
import wallapp.process.bus.ProcessBusMock
import wallapp.profileimage.ProfileImageManager
import wallapp.profileimage.ProfileImageManagerIos
import wallapp.userprofile.UserProfileRepository
import wallapp.userprofile.UserProfileRepositoryFirebase
import wallapp.userprofile.UserProfileRepositoryPreset
import wallapp.remoteconfig.ConfigValueRepository
import wallapp.remoteconfig.ConfigValueRepositoryFirebase
import wallapp.remoteconfig.RemoteConfig
import wallapp.remoteconfig.RemoteConfigDataDefault
import wallapp.remoteconfig.RemoteConfigFirebase
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteendpoint.RemoteEndpointMode
import wallapp.remotepaywall.RemotePaywallEventManager
import wallapp.remotepaywall.RemotePaywallEventManagerDemo
import wallapp.remotepaywall.RemotePaywallEventRepository
import wallapp.remotepaywall.RemotePaywallEventRepositoryNoOp
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.remotepaywall.RemotePaywallManagerNoOp
import wallapp.runmode.isNotApp
import wallapp.security.EncryptionManager
import wallapp.security.EncryptionManagerIos
import wallapp.settings.Settings
import wallapp.settings.SettingsMultiplatform
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.photo.viewer.SystemPhotoViewer
import wallapp.system.photo.viewer.SystemPhotoViewerNoOp
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.system.wallpaper.SystemWallpaperManagerNoOp
import wallapp.system.window.WindowFrameManager
import wallapp.system.window.WindowFrameManagerIos
import wallapp.system.window.WindowFrameManagerNoOp
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelFactoryCached
import wallapp.viewmodel.ViewModelFactoryDefault
import wallapp.viewmodel.ViewModelFactoryPlatform
import wallapp.viewmodel.ViewModelFactoryPlatformIos
import wallapp.viewmodel.ViewModelProviderFactory
import wallapp.viewmodel.ViewModelProviderFactoryIos
import wallapp.wallpaper.cache.BaseCache
import wallapp.wallpaper.cache.BaseCacheSuspend
import wallapp.wallpaper.cache.BaseCacheSuspendDefault
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.cache.WallpaperImageCacheIos
import com.russhwolf.settings.Settings as SettingsKm

actual val Factory: FactoryCommon = FactoryIos

object FactoryIos : FactoryCommon() {

    override var remoteEndpointMode: RemoteEndpointMode = RemoteEndpointMode.Firebase

    override fun platformModuleInitializers(scope: Scope): List<ModuleInitializer> {
        return listOf()
    }

    override fun appIconManager(scope: Scope): AppIconManager {
        return AppIconManagerIos()
    }

    override fun adSourceInitializer(scope: Scope): AdSourceInitializer {
        return scope.get<Lazy<AdSourceInitializer>>(NamedScope.LazyAdSourceInitializerNoOp).get()
    }

    override fun adUnitIds(scope: Scope): AdUnitIds {
        return InteropFactory().createAdUnitIds()
    }

    fun applicationId(scope: Scope): String {
        return "com.wallapp.example"
    }

    override fun appNavigator(scope: Scope): AppNavigator {
        return scope.get<AppNavigatorDefault>()
    }

    override fun appPlatformConfig(scope: Scope): AppPlatformConfig {
        val buildConfig: BuildConfig = scope.get()
        return AppPlatformConfig(
            packageName = buildConfig.packageName,
            mainActivityClassName = "wallapp.app.WallAppActivity",
            wallpaperServiceClassName = null,
        )
    }

    override fun appVisibility(scope: Scope): AppVisibility {
        return AppVisibilityIos()
    }

    fun baseCache(): BaseCache {
        return InteropFactory().createBaseCache()
    }

    fun baseCacheSuspend(): BaseCacheSuspend {
        return BaseCacheSuspendDefault(baseCache())
    }

    override fun billingManager(scope: Scope): BillingManager {
        return if (canConfigureRevenueCat(scope)) {
            scope.get<BillingManagerRevenueCatIos>()
        } else {
            scope.get<BillingManager>(NamedScope.BillingManagerFallback)
        }
    }

    private val cacheFileMediaMapUserDefaults: NSUserDefaults by lazy {
        NSUserDefaults(suiteName = PreferenceDefinitions.CacheFileMediaMapFilename)
    }

    override fun cacheFileMediaMapSettings(scope: Scope): Settings {
        val userDefaults = cacheFileMediaMapUserDefaults
        val settings: SettingsKm = NSUserDefaultsSettings(userDefaults)
        return SettingsMultiplatform(settings)
    }

    fun buildConfig(scope: Scope): BuildConfig {
        return InteropFactory().createBuildConfig()
    }

    override fun cloudMessagingManager(scope: Scope): CloudMessagingManager {
        return scope.get<CloudMessagingManagerIos>()
    }

    override fun configValueRepository(scope: Scope): ConfigValueRepository {
        return ConfigValueRepositoryFirebase
    }

    override fun contentCacheConfig(scope: Scope): ContentCacheConfig {
        return scope.get<ContentCacheConfigDefault>()
//        return ContentCacheConfigMock(prefetchWallpaperImages = false)
    }

    override fun coroutineContexts(scope: Scope): CoroutineContexts {
        return CoroutineContextsDispatchers
    }

    override fun crashTrackingRemote(scope: Scope): CrashTracking {
        return CrashTrackingCrashlyticsIos(
            crashTrackingUserId = scope.get(),
            coroutineScopeMain = scope.get(NamedScope.CoroutineScopeMain),
        )
    }

    override fun currentScreenProvider(scope: Scope): CurrentScreenProvider {
        return CurrentScreenProviderIos(InteropFactory().createCurrentScreenCoordinator())
    }

    override fun dataRepository(scope: Scope): DataRepository {
        return scope.get<DataRepositoryDefault>()
    }

    override fun deepLinkManager(scope: Scope): DeepLinkManager {
        return scope.get<DeepLinkManagerDefault>()
    }

    fun deviceId(scope: Scope): DeviceId {
        return DeviceIdMock(deviceId = "com.example.device-id")
    }

    override fun deviceRefreshRate(scope: Scope): DeviceRefreshRate {
        return DeviceRefreshRateMock()
    }

    private val devicePreferencesUserDefaults: NSUserDefaults by lazy {
        NSUserDefaults(suiteName = PreferenceDefinitions.DeviceSettingsFilename)
    }

    override fun deviceSettings(scope: Scope): Settings {
        val userDefaults = devicePreferencesUserDefaults
        val settings: SettingsKm = NSUserDefaultsSettings(userDefaults)
        return SettingsMultiplatform(settings)
    }

    override fun deviceSpec(scope: Scope): DeviceSpec {
        return DeviceSpecIos
    }

    override fun deviceState(scope: Scope): DeviceState {
        return DeviceStateMock()
    }

    override fun dialogManager(scope: Scope): AlertManager {
        return AlertManagerIos(scope.get(), scope.get(NamedScope.CoroutineScopeMain))
    }

    override fun encryptionManager(scope: Scope): EncryptionManager {
        return EncryptionManagerIos()
    }

    override fun entitlementRepository(scope: Scope): EntitlementRepository {
//        val buildConfig = scope.get<BuildConfig>()
//        // Temp to unlock all content for Play Store builds (see #75 and #76)
//        return if (buildConfig.debug) {
//            scope.get<EntitlementRepositoryDefault>()
//        } else {
//            EntitlementRepositoryUnlockAll()
//        }
        return scope.get<EntitlementRepositoryDefault>()
    }

    override fun entitlementSkusSpecs(scope: Scope): EntitlementSkuSpecs {
        return scope.get<EntitlementSkuSpecsAppStore>()
    }

    override fun firebaseStorageDownloader(scope: Scope): FirebaseStorageDownloader {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Bundled -> FirebaseStorageDownloaderBundled
            RemoteEndpointMode.Firebase -> FirebaseStorageDownloaderIos(InteropFactory().createFirebaseStorageDownloadCoordinator())
            RemoteEndpointMode.FirebaseAdmin -> error("FirebaseAdmin not supported on iOS")
        }
    }

    fun googleAuthCoordinatorForIos(): GoogleAuthCoordinatorForIos {
        return InteropFactory().createGoogleAuthCoordinatorForIos()
    }

    override fun imageHostPlatformConfig(scope: Scope): ImageHostPlatformConfig {
        return ImageHostPlatformConfigIos
    }

    override fun imageLoader(scope: Scope): ImageLoader {
        return scope.get<ImageLoaderSeiko>()
//        return ImageLoaderNoOp
    }

    override fun imagePrefetcher(scope: Scope): ImagePrefetcher {
        return InteropFactory().createImagePrefetcherEx()
    }

    override fun inlineAdConfigFactories(scope: Scope): InlineAdConfigFactories {
        return InlineAdConfigFactories(emptyList())
    }

    override fun inlineAdItemFactories(scope: Scope): InlineAdItemFactories {
        return InlineAdItemFactories(emptyList())
    }

    override fun inAppBrowserManager(scope: Scope): InAppBrowserManager {
        val delegate = InteropFactory().createInAppBrowserDelegate()
        return InAppBrowserManagerDelegate(delegate)
    }

    override fun inAppReviewManager(scope: Scope): InAppReviewManager {
        return scope.get<InAppReviewManagerIos>()
    }

    override fun instantAppManager(scope: Scope): InstantAppManager {
        return scope.get<Lazy<InstantAppManagerNoOp>>(NamedScope.LazyInstantAppManagerNoOp).get()
    }

    private val licenseUserDefaults: NSUserDefaults by lazy {
        NSUserDefaults(suiteName = PreferenceDefinitions.LicenseSettingsFilename)
    }

    override fun licenseSettings(scope: Scope): Settings {
        val userDefaults = licenseUserDefaults
        val settings: SettingsKm = NSUserDefaultsSettings(userDefaults)
        return SettingsMultiplatform(settings)
    }

    override fun licenseStateSessionManager(scope: Scope): LicenseStateSessionManager {
        return LicenseStateSessionManagerActive(scope.get(), scope.get(NamedScope.CoroutineScopeIo))
    }

    override fun networkState(scope: Scope): NetworkState {
        return NetworkStateIos(InteropFactory().createNetworkStateNative())
    }

    override fun networkUserManager(scope: Scope): NetworkUserManager {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Bundled -> NetworkUserManagerPreset()
            RemoteEndpointMode.Firebase -> scope.get<NetworkUserManagerFirebase>()
            RemoteEndpointMode.FirebaseAdmin -> error("FirebaseAdmin not supported on iOS")
        }
    }

    override fun profileImageManager(scope: Scope): ProfileImageManager {
        return scope.get<ProfileImageManagerIos>()
    }

    override fun processBridge(scope: Scope): ProcessBridge {
        return ProcessBridgeMock()
    }

    override fun privacyMessagingManager(scope: Scope): PrivacyMessagingManager {
        return PrivacyMessagingManagerIos(InteropFactory().createPrivacyMessagingManagerDelegate())
    }

    override fun remoteConfig(scope: Scope): RemoteConfig {
        return RemoteConfigFirebase(
            buildConfig = scope.get(),
            coroutineScopeIo = scope.get(NamedScope.CoroutineScopeIo),
            deviceState = scope.get(),
        )
    }

    override fun remoteConfigData(scope: Scope): RemoteConfigData {
        return RemoteConfigDataDefault(
            remoteConfig = scope.get(),
            configValueRepository = scope.get(),
            coroutineScopeMain = scope.get(NamedScope.CoroutineScopeMain),
        )
    }

    @OptIn(ExperimentalSettingsImplementation::class)
    override fun remoteServerContentSettings(scope: Scope): Settings {
        val settingsKm = KeychainSettings(PreferenceDefinitions.RemoteServerContentFilename)
        return SettingsMultiplatform(settingsKm)
    }

    override fun remotePaywallManager(scope: Scope): RemotePaywallManager {
        return RemotePaywallManagerNoOp
    }

    override fun remotePaywallEventManager(scope: Scope): RemotePaywallEventManager {
        return RemotePaywallEventManagerDemo(scope.get(), scope.get(), scope.get(), scope.get(NamedScope.CoroutineScopeMain))
    }

    override fun remotePaywallEventRepository(scope: Scope): RemotePaywallEventRepository {
        return RemotePaywallEventRepositoryNoOp
    }

    override fun revenueCatManager(scope: Scope): RevenueCatManager {
        return if (canConfigureRevenueCat(scope)) {
            scope.get<RevenueCatManagerIos>()
        } else {
            scope.get<RevenueCatManagerNoOp>()
        }
    }

    override fun revenueCatUserManager(scope: Scope): RevenueCatUserManager {
        return if (canConfigureRevenueCat(scope)) {
            InteropFactory().createRevenueCatUserManager()
        } else {
            scope.get<RevenueCatUserManagerNoOp>()
        }
    }

    fun revenueCatManagerIos(): RevenueCatManagerIos {
        return InteropFactory().createRevenueCatManager() as RevenueCatManagerIos
    }

    override fun rewardAdConfig(scope: Scope): RewardAdConfig {
        return RewardAdConfigDefault(scope.get(), scope.get(), scope.get(), scope.get(NamedScope.CoroutineScopeMain))
    }

    override fun rewardAdInternalNavigator(scope: Scope): RewardAdInternalNavigator {
        return scope.get<RewardAdInternalNavigatorCompat>()
    }

    override fun rewardAdInternalPlaybackManager(scope: Scope): RewardAdInternalPlaybackManager {
        return scope.get<RewardAdInternalPlaybackManagerDefault>()
    }

    override fun rewardAdPlaybackManager(scope: Scope): RewardAdPlaybackManager {
        return rewardAdPlaybackManagerDefault(scope) ?: rewardAdPlaybackManagerAdMob(scope)
    }

    override fun rewardAdPlaybackManagerAdMob(scope: Scope): RewardAdPlaybackManager {
        return RewardAdPlaybackManagerIos(InteropFactory().createRewardAdCoordinator())
    }

    override fun systemWallpaperManager(scope: Scope): SystemWallpaperManager {
        return SystemWallpaperManagerNoOp
    }

    override fun toAltProcessBus(scope: Scope): ProcessBusAlt {
        return ProcessBusMock(emptyList())
    }

    override fun toMainProcessBus(scope: Scope): ProcessBusMain {
        return ProcessBusMock(emptyList())
    }

    override fun systemPhotoPicker(scope: Scope): SystemPhotoPicker {
        return InteropFactory().createSystemPhotoPicker()
    }

    override fun systemPhotoViewer(scope: Scope): SystemPhotoViewer {
        return SystemPhotoViewerNoOp
    }

    private val userSettingsUserDefaults: NSUserDefaults by lazy {
        NSUserDefaults(suiteName = PreferenceDefinitions.UserSettingsFilename)
    }

    override fun userSettings(scope: Scope): Settings {
        val userDefaults = userSettingsUserDefaults
        val settings: SettingsKm = NSUserDefaultsSettings(userDefaults)
        return SettingsMultiplatform(settings)
    }

    override fun viewModelFactory(scope: Scope): ViewModelFactory {
        return ViewModelFactoryCached(scope.get<ViewModelFactoryDefault>())
    }

    override fun viewModelFactoryPlatform(scope: Scope): ViewModelFactoryPlatform {
        return ViewModelFactoryPlatformIos()
    }

    override fun viewModelProviderFactory(scope: Scope): ViewModelProviderFactory {
        return ViewModelProviderFactoryIos(
            viewModelFactory = scope.get(),
        )
    }

    override fun accountDataManager(scope: Scope): AccountDataManager {
        return scope.get<AccountDataManagerDefault>()
    }

    override fun firebaseAuthManager(scope: Scope): FirebaseAuthManager {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Bundled -> FirebaseAuthManagerPreset()
            RemoteEndpointMode.Firebase -> FirebaseAuthManagerFirebase(scope.get(), scope.get(NamedScope.CoroutineScopeMain), scope.get())
            RemoteEndpointMode.FirebaseAdmin -> error("FirebaseAdmin not supported on iOS")
        }
    }

    override fun googleAuthManager(scope: Scope): GoogleAuthManager {
        return scope.get<GoogleAuthManagerIos>()
    }

    override fun accountDataRepositoryServer(scope: Scope): AccountDataRepository {
        return scope.get<AccountDataRepositoryFirebaseMobile>()
    }

    override fun urlDownloader(scope: Scope): UrlDownloader {
        return UrlDownloaderIos(InteropFactory().createUrlDownloadCoordinator())
    }

    override fun wallpaperImageCache(scope: Scope): WallpaperImageCache {
        return WallpaperImageCacheIos(baseCache(), scope.get())
    }

    override fun windowFrameManager(scope: Scope): WindowFrameManager {
        return if (runMode.isNotApp) {
            WindowFrameManagerNoOp
        } else {
            WindowFrameManagerIos(scope.get(), scope.get(NamedScope.CoroutineScopeMain))
        }
    }

    override fun userProfileRepository(scope: Scope): UserProfileRepository {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Bundled -> UserProfileRepositoryPreset()
            RemoteEndpointMode.Firebase -> scope.get<UserProfileRepositoryFirebase>()
            RemoteEndpointMode.FirebaseAdmin -> error("FirebaseAdmin not supported on iOS")
        }
    }

    fun uiControllerManager(): UiControllerManager {
        return InteropFactory().createUiControllerManager()
    }

    override fun uiKitFactory(scope: Scope): UIKitFactory {
        return InteropFactory().createUIKitFactory()
    }
}
