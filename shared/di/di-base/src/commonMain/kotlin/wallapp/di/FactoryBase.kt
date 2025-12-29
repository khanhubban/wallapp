package wallapp.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope
import wallapp.account.data.AccountDataManager
import wallapp.ad.reward.RewardAdConfig
import wallapp.ads.AdSourceInitializer
import wallapp.ads.AdUnitIds
import wallapp.ads.inline.support.InlineAdConfigFactories
import wallapp.ads.inline.support.InlineAdItemFactories
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.appconfig.AppPlatformConfig
import wallapp.appshortcuts.AppShortcutsManager
import wallapp.appvisibility.AppVisibility
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.google.GoogleAuthManager
import wallapp.billing.BillingManager
import wallapp.billing.revenuecat.RevenueCatManager
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.content.network.repository.NetworkContentRepository
import wallapp.coroutine.CoroutineContexts
import wallapp.crashtracking.CrashTracking
import wallapp.data.DataRepository
import wallapp.data.content.ContentCacheConfig
import wallapp.data.folder.FolderRepository
import wallapp.data.model.ModelRepository
import wallapp.deeplink.DeepLinkManager
import wallapp.device.DeviceSpec
import wallapp.device.refreshrate.DeviceRefreshRate
import wallapp.device.state.DeviceState
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.UrlDownloader
import wallapp.entitlement.EntitlementRepository
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.image.host.ImageHostPlatformConfig
import wallapp.image.loader.ImageLoader
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.inappreview.InAppReviewManager
import wallapp.instantapp.InstantAppManager
import wallapp.license.state.LicenseStateSessionManager
import wallapp.media.network.repository.NetworkMediaMapRepository
import wallapp.mediamap.MediaMapRepository
import wallapp.mediamap.MediaMapRepositoryConfig
import wallapp.messaging.CloudMessagingManager
import wallapp.navigation.AppNavigator
import wallapp.navigation.CurrentScreenProvider
import wallapp.network.NetworkState
import wallapp.network.NetworkUserManager
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.prefs.DevicePreferenceStorage
import wallapp.prefs.PreferenceStorage
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.process.bridge.ProcessBridge
import wallapp.process.bus.ProcessBusAlt
import wallapp.process.bus.ProcessBusMain
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchasableRepository
import wallapp.remoteapi.RemoteEndpointsSpecRepository
import wallapp.remoteconfig.ConfigValueRepository
import wallapp.remoteconfig.RemoteConfig
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteendpoint.RemoteApiEndpointRepository
import wallapp.remoteendpoint.RemoteEndpointMode
import wallapp.remotepaywall.RemotePaywallEventManager
import wallapp.remotepaywall.RemotePaywallEventRepository
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.runmode.RunMode
import wallapp.security.EncryptionManager
import wallapp.settings.Settings
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.photo.viewer.SystemPhotoViewer
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.system.window.WindowFrameManager
import wallapp.userprofile.UserProfileRepository
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelFactoryPlatform
import wallapp.viewmodel.ViewModelProviderFactory
import wallapp.wallpaper.cache.WallpaperImageCache

interface FactoryBase {

    var runMode: RunMode
    var remoteEndpointMode: RemoteEndpointMode
    var multiProcessAllowed: Boolean?
    var isDebug: Boolean?

    fun accountDataManager(scope: Scope): AccountDataManager
    fun adSourceInitializer(scope: Scope): AdSourceInitializer
    fun adUnitIds(scope: Scope): AdUnitIds
    fun appNavigator(scope: Scope): AppNavigator
    fun appPlatformConfig(scope: Scope): AppPlatformConfig
    fun appShortcutsManager(scope: Scope): AppShortcutsManager
    fun appVisibility(scope: Scope): AppVisibility
    fun billingManager(scope: Scope): BillingManager
    fun cacheFileMediaMapSettings(scope: Scope): Settings
    fun cloudMessagingManager(scope: Scope): CloudMessagingManager
    fun configValueRepository(scope: Scope): ConfigValueRepository
    fun contentCacheConfig(scope: Scope): ContentCacheConfig
    fun coroutineContexts(scope: Scope): CoroutineContexts
    fun coroutineScopeIo(scope: Scope): CoroutineScope
    fun coroutineScopeMain(scope: Scope): CoroutineScope
    fun coroutineScopeMainImmediate(scope: Scope): CoroutineScope
    fun crashTrackingRemote(scope: Scope): CrashTracking
    fun currentScreenProvider(scope: Scope): CurrentScreenProvider
    fun dataRepository(scope: Scope): DataRepository
    fun deepLinkManager(scope: Scope): DeepLinkManager
    fun deviceSpec(scope: Scope): DeviceSpec
    fun devicePreferenceStorage(scope: Scope): DevicePreferenceStorage
    fun deviceRefreshRate(scope: Scope) : DeviceRefreshRate
    fun deviceSettings(scope: Scope): Settings
    fun deviceState(scope: Scope): DeviceState
    fun dialogManager(scope: Scope): AlertManager
    fun encryptionManager(scope: Scope): EncryptionManager
    fun entitlementRepository(scope: Scope): EntitlementRepository
    fun entitlementSkusSpecs(scope: Scope): EntitlementSkuSpecs
    fun firebaseAuthManager(scope: Scope): FirebaseAuthManager
    fun firebaseStorageDownloader(scope: Scope): FirebaseStorageDownloader
    fun folderRepository(scope: Scope): FolderRepository
    fun googleAuthManager(scope: Scope): GoogleAuthManager
    fun imageHostPlatformConfig(scope: Scope): ImageHostPlatformConfig
    fun imageLoader(scope: Scope): ImageLoader
    fun imagePrefetcher(scope: Scope): ImagePrefetcher
    fun inlineAdConfigFactories(scope: Scope): InlineAdConfigFactories
    fun inlineAdItemFactories(scope: Scope): InlineAdItemFactories
    fun inAppBrowserManager(scope: Scope): InAppBrowserManager
    fun inAppReviewManager(scope: Scope): InAppReviewManager
    fun instantAppManager(scope: Scope): InstantAppManager
    fun licenseSettings(scope: Scope): Settings
    fun licenseStateSessionManager(scope: Scope): LicenseStateSessionManager
    fun mediaMapRepository(scope: Scope): MediaMapRepository
    fun mediaMapRepositoryConfig(scope: Scope): MediaMapRepositoryConfig
    fun modelRepository(scope: Scope): ModelRepository
    fun networkContentRepository(scope: Scope): NetworkContentRepository
    fun networkMediaMapRepository(scope: Scope): NetworkMediaMapRepository
    fun networkState(scope: Scope): NetworkState
    fun networkUserManager(scope: Scope): NetworkUserManager
    fun preferenceStorage(scope: Scope): PreferenceStorage
    fun processBridge(scope: Scope): ProcessBridge
    fun privacyMessagingManager(scope: Scope): PrivacyMessagingManager
    fun profileImageManager(scope: Scope): ProfileImageManager
    fun purchasableRepository(scope: Scope): PurchasableRepository
    fun remoteConfig(scope: Scope): RemoteConfig
    fun remoteConfigData(scope: Scope): RemoteConfigData
    fun remoteServerContentSettings(scope: Scope): Settings
    fun remoteEndpointsSpecRepository(scope: Scope): RemoteEndpointsSpecRepository
    fun remotePaywallEventManager(scope: Scope): RemotePaywallEventManager
    fun remotePaywallEventRepository(scope: Scope): RemotePaywallEventRepository
    fun remotePaywallManager(scope: Scope): RemotePaywallManager
    fun revenueCatManager(scope: Scope): RevenueCatManager
    fun revenueCatUserManager(scope: Scope): RevenueCatUserManager
    fun rewardAdConfig(scope: Scope): RewardAdConfig
    fun rewardAdInternalNavigator(scope: Scope): RewardAdInternalNavigator
    fun rewardAdInternalPlaybackManager(scope: Scope): RewardAdInternalPlaybackManager
    fun rewardAdPlaybackManager(scope: Scope): RewardAdPlaybackManager
    fun rewardAdPlaybackManagerAdMob(scope: Scope): RewardAdPlaybackManager?
    fun systemPhotoPicker(scope: Scope): SystemPhotoPicker
    fun systemPhotoViewer(scope: Scope): SystemPhotoViewer
    fun systemWallpaperManager(scope: Scope): SystemWallpaperManager
    fun toAltProcessBus(scope: Scope): ProcessBusAlt
    fun toMainProcessBus(scope: Scope): ProcessBusMain
    fun typefaceRepository(scope: Scope): TypefaceRepository
    fun urlDownloader(scope: Scope): UrlDownloader
    fun userProfileRepository(scope: Scope): UserProfileRepository
    fun userSettings(scope: Scope): Settings
    fun viewModelFactory(scope: Scope): ViewModelFactory
    fun viewModelFactoryPlatform(scope: Scope): ViewModelFactoryPlatform
    fun viewModelProviderFactory(scope: Scope): ViewModelProviderFactory
    fun wallpaperImageCache(scope: Scope): WallpaperImageCache
    fun windowFrameManager(scope: Scope): WindowFrameManager
    fun remoteApiEndpointRepository(scope: Scope): RemoteApiEndpointRepository
}