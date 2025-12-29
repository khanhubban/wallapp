package wallapp.di

import com.russhwolf.settings.PreferencesSettings
import org.koin.core.scope.Scope
import wallapp.account.data.AccountDataManager
import wallapp.account.data.AccountDataManagerDefault
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdConfigNoOp
import wallapp.ads.AdSourceInitializer
import wallapp.ads.AdUnitIds
import wallapp.ads.AdUnitIdsDefault
import wallapp.ads.inline.support.InlineAdConfigFactories
import wallapp.ads.inline.support.InlineAdItemFactories
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.RewardAdPlaybackManagerNoOp
import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.ads.reward.internal.RewardAdInternalNavigatorNoOp
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManagerNoOp
import wallapp.appconfig.AppPlatformConfig
import wallapp.appvisibility.AppVisibility
import wallapp.appvisibility.AppVisibilityNoOp
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.firebase.FirebaseAuthManagerPreset
import wallapp.billing.BillingManager
import wallapp.billing.revenuecat.RevenueCatManager
import wallapp.billing.revenuecat.RevenueCatManagerNoOp
import wallapp.buildconfig.BuildConfig
import wallapp.buildconfig.BuildConfigMock
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.CoroutineContextsDispatchers
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingLocal
import wallapp.data.DataRepository
import wallapp.data.DataRepositoryDefault
import wallapp.deeplink.DeepLinkManager
import wallapp.deeplink.DeepLinkManagerNoOp
import wallapp.device.DeviceId
import wallapp.device.DeviceIdMock
import wallapp.device.DeviceSpec
import wallapp.device.DeviceSpecNoOp
import wallapp.device.refreshrate.DeviceRefreshRate
import wallapp.device.refreshrate.DeviceRefreshRateMock
import wallapp.device.state.DeviceState
import wallapp.device.state.DeviceStateMock
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.FirebaseStorageDownloaderAdmin
import wallapp.download.UrlDownloader
import wallapp.download.UrlDownloaderCompat
import wallapp.entitlement.EntitlementRepository
import wallapp.entitlement.EntitlementRepositoryDefault
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.entitlement.EntitlementSkuSpecsPlaceholder
import wallapp.googlecloud.GoogleStorageRepository
import wallapp.googlecloud.GoogleStorageRepositoryLocal
import wallapp.image.host.ImageHostPlatformConfig
import wallapp.image.host.ImageHostPlatformConfigDesktop
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderSeiko
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.image.prefetch.ImagePrefetcherNoOp
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.inappbrowser.InAppBrowserManagerNoOp
import wallapp.inappreview.InAppReviewManager
import wallapp.inappreview.InAppReviewManagerNoOp
import wallapp.initializer.module.ModuleInitializer
import wallapp.instantapp.InstantAppManager
import wallapp.instantapp.InstantAppManagerNoOp
import wallapp.license.state.LicenseStateSessionManager
import wallapp.license.state.LicenseStateSessionManagerNoOp
import wallapp.messaging.CloudMessagingManager
import wallapp.messaging.CloudMessagingManagerNoOp
import wallapp.navigation.AppNavigator
import wallapp.navigation.AppNavigatorDefault
import wallapp.network.NetworkState
import wallapp.network.NetworkStatePreset
import wallapp.network.NetworkUserManager
import wallapp.network.NetworkUserManagerAdmin
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertManagerComposable
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.privacymessaging.PrivacyMessagingManagerNoOp
import wallapp.process.bridge.ProcessBridge
import wallapp.process.bridge.ProcessBridgeMock
import wallapp.process.bus.ProcessBusAlt
import wallapp.process.bus.ProcessBusMain
import wallapp.process.bus.ProcessBusMock
import wallapp.profileimage.ProfileImageManager
import wallapp.profileimage.ProfileImageManagerDefault
import wallapp.remoteendpoint.RemoteEndpointMode
import wallapp.remotepaywall.RemotePaywallEventManager
import wallapp.remotepaywall.RemotePaywallEventManagerNoOp
import wallapp.remotepaywall.RemotePaywallEventRepository
import wallapp.remotepaywall.RemotePaywallEventRepositoryNoOp
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.remotepaywall.RemotePaywallManagerNoOp
import wallapp.security.EncryptionManager
import wallapp.security.EncryptionManagerDesktop
import wallapp.settings.Settings
import wallapp.settings.SettingsMultiplatform
import wallapp.settings.SettingsMultiplatformProperties
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.photo.picker.SystemPhotoPickerNoOp
import wallapp.system.photo.viewer.SystemPhotoViewer
import wallapp.system.photo.viewer.SystemPhotoViewerNoOp
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.system.wallpaper.SystemWallpaperManagerNoOp
import wallapp.userprofile.UserProfileRepository
import wallapp.userprofile.UserProfileRepositoryPreset
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelFactoryCached
import wallapp.viewmodel.ViewModelFactoryDefault
import wallapp.viewmodel.ViewModelFactoryPlatform
import wallapp.viewmodel.ViewModelFactoryPlatformDesktop
import wallapp.viewmodel.ViewModelProviderFactory
import wallapp.viewmodel.ViewModelProviderFactoryDesktop
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.cache.WallpaperImageCacheNoOp
import com.russhwolf.settings.Settings as SettingsKm

actual val Factory: FactoryCommon = FactoryDesktop

object FactoryDesktop : FactoryCommon() {

    override var remoteEndpointMode: RemoteEndpointMode = RemoteEndpointMode.FirebaseAdmin

    override fun platformModuleInitializers(scope: Scope): List<ModuleInitializer> {
        return emptyList()
    }

    override fun accountDataManager(scope: Scope): AccountDataManager {
        return scope.get<AccountDataManagerDefault>()
    }

    override fun adSourceInitializer(scope: Scope): AdSourceInitializer {
        return scope.get<Lazy<AdSourceInitializer>>(NamedScope.LazyAdSourceInitializerNoOp).get()
    }

    override fun adUnitIds(scope: Scope): AdUnitIds {
        return AdUnitIdsDefault
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
        return AppVisibilityNoOp()
    }

    override fun billingManager(scope: Scope): BillingManager {
        return scope.get<BillingManager>(NamedScope.BillingManagerFallback)
    }

    override fun cacheFileMediaMapSettings(scope: Scope): Settings {
        return SettingsMultiplatform(SettingsMultiplatformProperties())
    }

    fun buildConfig(scope: Scope): BuildConfig {
        return BuildConfigMock()
    }

    override fun cloudMessagingManager(scope: Scope): CloudMessagingManager {
        return CloudMessagingManagerNoOp
    }

    override fun coroutineContexts(scope: Scope): CoroutineContexts {
        return CoroutineContextsDispatchers
    }

    override fun crashTrackingRemote(scope: Scope): CrashTracking {
        return CrashTrackingLocal
    }

    override fun dataRepository(scope: Scope): DataRepository {
        return DataRepositoryDefault()
    }

    override fun deepLinkManager(scope: Scope): DeepLinkManager = DeepLinkManagerNoOp

    fun deviceId(scope: Scope): DeviceId {
        return DeviceIdMock(deviceId = "com.example.device-id")
    }

    override fun deviceRefreshRate(scope: Scope): DeviceRefreshRate {
        return DeviceRefreshRateMock()
    }

    override fun deviceSettings(scope: Scope): Settings {
        val settings: SettingsKm = PreferencesSettings(PreferencesDesktop.devicePreferences)
        return SettingsMultiplatform(settings)
    }

    override fun deviceSpec(scope: Scope): DeviceSpec {
        return DeviceSpecNoOp
    }

    override fun deviceState(scope: Scope): DeviceState {
        return DeviceStateMock()
    }

    override fun dialogManager(scope: Scope): AlertManager {
        return AlertManagerComposable()
    }

    override fun encryptionManager(scope: Scope): EncryptionManager {
        return EncryptionManagerDesktop()
    }

    override fun entitlementRepository(scope: Scope): EntitlementRepository {
        return scope.get<EntitlementRepositoryDefault>()
    }

    override fun entitlementSkusSpecs(scope: Scope): EntitlementSkuSpecs {
        return EntitlementSkuSpecsPlaceholder
    }

    override fun firebaseAuthManager(scope: Scope): FirebaseAuthManager {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Firebase -> TODO("Add support for RemoteEndpointMode.Firebase")
            RemoteEndpointMode.FirebaseAdmin -> FirebaseAuthManagerPreset()
        }
    }

    override fun firebaseStorageDownloader(scope: Scope): FirebaseStorageDownloader {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.FirebaseAdmin -> {
                FirebaseStorageDownloaderAdmin(googleStorageRepositoryAdmin(scope), scope.get())
            }

            RemoteEndpointMode.Firebase -> {
                TODO("Add support for RemoteEndpointMode.Firebase")
            }
        }
    }

    private fun googleStorageRepositoryAdmin(scope: Scope): GoogleStorageRepository {
        require(remoteEndpointMode == RemoteEndpointMode.FirebaseAdmin) {
            "googleStorageRepositoryAdmin is only available in FirebaseAdmin mode"
        }
        return GoogleStorageRepositoryLocal()
    }

    override fun imageHostPlatformConfig(scope: Scope): ImageHostPlatformConfig {
        return ImageHostPlatformConfigDesktop
    }

    override fun imageLoader(scope: Scope): ImageLoader {
        return scope.get<ImageLoaderSeiko>()
    }

    override fun imagePrefetcher(scope: Scope): ImagePrefetcher {
        return ImagePrefetcherNoOp
    }

    override fun inlineAdConfigFactories(scope: Scope): InlineAdConfigFactories {
        return InlineAdConfigFactories(emptyList())
    }

    override fun inlineAdItemFactories(scope: Scope): InlineAdItemFactories {
        return InlineAdItemFactories(emptyList())
    }

    override fun inAppBrowserManager(scope: Scope): InAppBrowserManager {
        return InAppBrowserManagerNoOp
    }

    override fun inAppReviewManager(scope: Scope): InAppReviewManager {
        return InAppReviewManagerNoOp
    }

    override fun instantAppManager(scope: Scope): InstantAppManager {
        return scope.get<Lazy<InstantAppManagerNoOp>>(NamedScope.LazyInstantAppManagerNoOp).get()
    }

    override fun licenseSettings(scope: Scope): Settings {
        val settings: SettingsKm = PreferencesSettings(PreferencesDesktop.licensePreferences)
        return SettingsMultiplatform(settings)
    }

    override fun licenseStateSessionManager(scope: Scope): LicenseStateSessionManager {
        return LicenseStateSessionManagerNoOp
    }

    override fun networkState(scope: Scope): NetworkState {
        return NetworkStatePreset()
    }

    override fun networkUserManager(scope: Scope): NetworkUserManager {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Firebase -> TODO("Add support for RemoteEndpointMode.Firebase")
            RemoteEndpointMode.FirebaseAdmin -> NetworkUserManagerAdmin()
        }
    }

    override fun processBridge(scope: Scope): ProcessBridge {
        return ProcessBridgeMock()
    }

    override fun privacyMessagingManager(scope: Scope): PrivacyMessagingManager {
        return PrivacyMessagingManagerNoOp
    }

    override fun profileImageManager(scope: Scope): ProfileImageManager {
        return scope.get<ProfileImageManagerDefault>()
    }

    override fun remoteServerContentSettings(scope: Scope): Settings {
        return SettingsMultiplatform(SettingsMultiplatformProperties())
    }

    override fun remotePaywallManager(scope: Scope): RemotePaywallManager {
        return RemotePaywallManagerNoOp
    }

    override fun remotePaywallEventManager(scope: Scope): RemotePaywallEventManager {
        return RemotePaywallEventManagerNoOp
    }

    override fun remotePaywallEventRepository(scope: Scope): RemotePaywallEventRepository {
        return RemotePaywallEventRepositoryNoOp
    }

    override fun revenueCatManager(scope: Scope): RevenueCatManager {
        return scope.get<RevenueCatManagerNoOp>()
    }

    override fun rewardAdConfig(scope: Scope): RewardAdConfig = RewardAdConfigNoOp

    override fun rewardAdInternalNavigator(scope: Scope): RewardAdInternalNavigator = RewardAdInternalNavigatorNoOp

    override fun rewardAdInternalPlaybackManager(scope: Scope) = RewardAdInternalPlaybackManagerNoOp()

    override fun rewardAdPlaybackManager(scope: Scope): RewardAdPlaybackManager {
        return RewardAdPlaybackManagerNoOp
    }

    override fun rewardAdPlaybackManagerAdMob(scope: Scope): RewardAdPlaybackManager? = null

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
        return SystemPhotoPickerNoOp
    }

    override fun systemPhotoViewer(scope: Scope): SystemPhotoViewer {
        return SystemPhotoViewerNoOp
    }

    override fun userProfileRepository(scope: Scope): UserProfileRepository {
        return when (remoteEndpointMode) {
            RemoteEndpointMode.Firebase -> TODO("Add support for RemoteEndpointMode.Firebase")
            RemoteEndpointMode.FirebaseAdmin -> UserProfileRepositoryPreset()
        }
    }

    override fun userSettings(scope: Scope): Settings {
        val settings: SettingsKm = PreferencesSettings(PreferencesDesktop.userPreferences)
        return SettingsMultiplatform(settings)
    }

    override fun viewModelFactory(scope: Scope): ViewModelFactory {
        return ViewModelFactoryCached(scope.get<ViewModelFactoryDefault>())
    }

    override fun viewModelFactoryPlatform(scope: Scope): ViewModelFactoryPlatform {
        return ViewModelFactoryPlatformDesktop()
    }

    override fun viewModelProviderFactory(scope: Scope): ViewModelProviderFactory {
        return ViewModelProviderFactoryDesktop(
            viewModelFactory = scope.get(),
        )
    }

    override fun urlDownloader(scope: Scope): UrlDownloader {
        return UrlDownloaderCompat(scope.get())
    }

    override fun wallpaperImageCache(scope: Scope): WallpaperImageCache {
        return WallpaperImageCacheNoOp
    }
}