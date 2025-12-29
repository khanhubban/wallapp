package wallapp.di.module

import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.ads.AdUnitIdsDefault
import wallapp.app.AppLaunchManager
import wallapp.app.AppLaunchManagerDefault
import wallapp.app.AppStateManager
import wallapp.app.AppStateManagerWrapper
import wallapp.app.AppViewModelFactory
import wallapp.app.AppViewModelFactoryDefault
import wallapp.appconfig.AppConfig
import wallapp.appconfig.AppConfigAltProcess
import wallapp.appconfig.AppConfigMainProcess
import wallapp.appconfig.AppPlatformConfig
import wallapp.appicon.AppIconManager
import wallapp.appshortcuts.AppShortcutsManager
import wallapp.appshortcuts.AppShortcutsManagerNoOp
import wallapp.appstate.AppState
import wallapp.appstate.AppStateAltProcess
import wallapp.appstate.AppStateMainProcess
import wallapp.appversion.AppVersionUpgrader
import wallapp.appversion.AppVersionUpgraderNoOp
import wallapp.appvisibility.AppVisibility
import wallapp.appvisibility.AppVisibilityDefaultProcess
import wallapp.appvisibility.AppVisibilityWallpaperProcess
import wallapp.bottomsheet.BottomSheetViewStateProviderManager
import wallapp.bottomsheet.BottomSheetViewStateProviderManagerDefault
import wallapp.content.state.settings.SettingKeyManager
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.CoroutineScopes
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingArbitrated
import wallapp.crashtracking.CrashTrackingLocal
import wallapp.crashtracking.CrashTrackingUserId
import wallapp.crashtracking.CrashTrackingUserIdDefault
import wallapp.data.osslicense.OssLicenseRepository
import wallapp.data.osslicense.OssLicenseRepositoryPreset
import wallapp.device.DeviceCountry
import wallapp.device.DeviceCountryIpTelephony
import wallapp.device.DeviceCountryTelephony
import wallapp.device.DeviceInfo
import wallapp.device.DeviceModel
import wallapp.device.DeviceSpec
import wallapp.device.refreshrate.DeviceRefreshRate
import wallapp.device.state.DeviceState
import wallapp.devicerecord.DeviceRecordManager
import wallapp.devicerecord.DeviceRecordManagerDefault
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.flavorconfig.FlavorConfig
import wallapp.graphics.color.ColorManager
import wallapp.image.ImageViewSpecFactory
import wallapp.image.ImageViewSpecFactoryDefault
import wallapp.image.bucket.ImageBucketManager
import wallapp.image.bucket.ImageBucketManagerDefault
import wallapp.image.host.ImageHostManager
import wallapp.image.host.ImageHostManagerConfig
import wallapp.image.host.ImageHostManagerConfigDefault
import wallapp.image.host.ImageHostManagerDefault
import wallapp.image.host.ImageHostPlatformConfig
import wallapp.image.host.ImageHostUrlMapper
import wallapp.image.host.ImageHostUrlMapperConfig
import wallapp.image.host.ImageHostUrlMapperConfigImgix
import wallapp.image.host.prefetch.ImagePrefetcherSeikoImageLoader
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderSeiko
import wallapp.image.prefetch.ImagePrefetchManager
import wallapp.image.prefetch.ImagePrefetchManagerDefault
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.image.scaler.ImageScaler
import wallapp.image.scaler.ImageScalerDefault
import wallapp.image.size.ImageSizeMapper
import wallapp.image.size.ImageSizeMapperDefault
import wallapp.image.sized.SizedImageMapper
import wallapp.image.sized.SizedImageMapperDefault
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.inappreview.InAppReviewManager
import wallapp.inappreview.InAppReviewRequestManager
import wallapp.inappreview.InAppReviewRequestManagerConfig
import wallapp.inappreview.InAppReviewRequestManagerConfigDefault
import wallapp.inappreview.InAppReviewRequestManagerDefault
import wallapp.initializer.module.ModuleInitializers
import wallapp.instantapp.InstantAppManager
import wallapp.instantapp.InstantAppManagerNoOp
import wallapp.interop.InteropBridge
import wallapp.license.LicenseCacheAltProcess
import wallapp.license.LicenseCacheMainProcess
import wallapp.license.cache.LicenseCache
import wallapp.mediamap.MediaMapRepository
import wallapp.mediamap.MediaMapRepositoryConfig
import wallapp.mediamap.MediaMapRepositoryConfigDefault
import wallapp.mediamap.MediaMapRepositoryDefault
import wallapp.monitoring.MonitoringManager
import wallapp.monitoring.MonitoringManagerDefault
import wallapp.navigation.AppNavigator
import wallapp.navigation.AppNavigatorConfig
import wallapp.navigation.AppNavigatorConfigDefault
import wallapp.navigation.AppNavigatorDefault
import wallapp.navigation.CurrentScreenProvider
import wallapp.navigation.CurrentScreenProviderDefault
import wallapp.network.NetworkState
import wallapp.pixel.image.host.ImageHostUrlMapperImgix
import wallapp.pixel.navigation.NavigationManager
import wallapp.pixel.navigation.NavigationManagerDefault
import wallapp.pixel.shape.ShapeClipperComposable
import wallapp.pixel.shape.ShapeMapperComposable
import wallapp.pixel.theme.shape.ShapeClipperComposableDefault
import wallapp.pixel.theme.shape.ShapeMapperComposableDefault
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.preferences.PreferenceDefaultsFlavor
import wallapp.preferences.PreferenceDefaultsProviderConfigDefault
import wallapp.preferences.PreferenceDefaultsProviderDefault
import wallapp.preferences.UserPreferences
import wallapp.preferences.UserPreferencesAltProcess
import wallapp.preferences.UserPreferencesMainProcess
import wallapp.prefs.DevicePreferenceStorage
import wallapp.prefs.DevicePreferenceStorageDefault
import wallapp.prefs.PreferenceDefaults
import wallapp.prefs.PreferenceDefaultsProvider
import wallapp.prefs.PreferenceDefaultsProviderConfig
import wallapp.prefs.PreferenceStorage
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.process.Process
import wallapp.process.bridge.ProcessBridge
import wallapp.process.bridge.ProcessBridgeAltProcess
import wallapp.process.bridge.ProcessBridgeMainProcess
import wallapp.process.bus.ProcessBusAlt
import wallapp.process.bus.ProcessBusMain
import wallapp.process.listener.ProcessListenerManager
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.random.RandomManager
import wallapp.random.RandomManagerDefault
import wallapp.remoteconfig.ConfigValueRepository
import wallapp.remoteconfig.RemoteConfig
import wallapp.remoteconfig.RemoteConfigPreset
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProvider
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault
import wallapp.remotecontent.RemoteServerContentCache
import wallapp.remotecontent.RemoteServerContentCacheDefault
import wallapp.remotepaywall.RemotePaywallEventManager
import wallapp.remotepaywall.RemotePaywallEventManagerDefault
import wallapp.remotepaywall.RemotePaywallEventRepository
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.resource.ResourceRepository
import wallapp.resource.ResourceRepositoryDefault
import wallapp.resources.image.ImageRepository
import wallapp.resources.image.ImageRepositoryDefault
import wallapp.resources.string.DateTimeFormatter
import wallapp.resources.string.DateTimeFormatterDefault
import wallapp.resources.string.StringArbitrator
import wallapp.resources.string.StringArbitratorDefault
import wallapp.resources.string.StringRepository
import wallapp.resources.string.StringRepositoryDefault
import wallapp.resources.string.Strings
import wallapp.resources.translation.TranslationRepository
import wallapp.resources.translation.TranslationRepositoryConfig
import wallapp.resources.translation.TranslationRepositoryConfigDefault
import wallapp.resources.translation.TranslationRepositoryEnglish
import wallapp.resources.translation.TranslationRepositorySingle
import wallapp.security.EncryptionManager
import wallapp.settings.AllSettings
import wallapp.settings.SettingManager
import wallapp.settings.SettingManagerDefault
import wallapp.settings.Settings
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.photo.viewer.SystemPhotoViewer
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.system.window.WindowManager
import wallapp.system.window.WindowManagerDefault
import wallapp.time.RenderTimeDelta
import wallapp.time.RenderTimeDeltaFrameInterval
import wallapp.util.MainThreadChecker
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewSpecFactoryDefault

@Suppress("RemoveExplicitTypeArguments")
val AppModule: Module = module {
    single<AdUnitIdsDefault> { AdUnitIdsDefault }
    single<AllSettings> { Factory.allSettings(this) }
    single<AppConfig> { Factory.appConfig(this) }
    single<AppConfigAltProcess> { AppConfigAltProcess(get(), get(NamedScope.UserSettings), get(NamedScope.DeviceSettings), get(), get(NamedScope.CoroutineScopeMain), get()) }
    single<AppConfigMainProcess> { AppConfigMainProcess(get(), get(), get()) }
    single<AppIconManager> { Factory.appIconManager(this) }
    single<AppLaunchManager> { AppLaunchManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<AppNavigator> { Factory.appNavigator(this) }
    single<AppNavigatorConfig> { AppNavigatorConfigDefault() }
    single<AppNavigatorDefault> { AppNavigatorDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<AppPlatformConfig> { Factory.appPlatformConfig(this) }
    single<AppShortcutsManager> { Factory.appShortcutsManager(this) }
    single<AppShortcutsManagerNoOp> { AppShortcutsManagerNoOp() }
    single<AppState> { Factory.appState(this) }
    single<AppStateAltProcess> { AppStateAltProcess(get(), get(NamedScope.DeviceSettings), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<AppStateMainProcess> { AppStateMainProcess(get(), get(), get()) }
    single<AppStateManager> { AppStateManagerWrapper(get(NamedScope.CoroutineScopeMain)) }
    single<AppVersionUpgrader> { Factory.appVersionUpgrader(this) }
    single<AppVersionUpgraderNoOp> { AppVersionUpgraderNoOp(get(), get()) }
    single<AppViewModelFactory> { AppViewModelFactoryDefault(get()) }
    single<AppVisibility> { Factory.appVisibility(this) }
    single<AppVisibilityDefaultProcess> { AppVisibilityDefaultProcess() }
    single<AppVisibilityWallpaperProcess> { AppVisibilityWallpaperProcess() }
    single<BottomSheetViewStateProviderManager> { BottomSheetViewStateProviderManagerDefault() }
    single<ColorManager> { Factory.colorManager() }
    single<ConfigValueRepository> { Factory.configValueRepository(this) }
    single<CoroutineContexts> { Factory.coroutineContexts(this) }
    single<CoroutineScope>(NamedScope.CoroutineScopeIo) { Factory.coroutineScopeIo(this) }
    single<CoroutineScope>(NamedScope.CoroutineScopeMain) { Factory.coroutineScopeMain(this) }
    single<CoroutineScope>(NamedScope.CoroutineScopeMainImmediate) { Factory.coroutineScopeMainImmediate(this) }
    single<CoroutineScopes> { Factory.coroutineScopes(this)}
    single<CrashTracking>(NamedScope.CrashTrackingArbitrated) { CrashTrackingArbitrated }
    single<CrashTracking>(NamedScope.CrashTrackingRemote) { Factory.crashTrackingRemote(this) }
    single<CrashTrackingLocal> { CrashTrackingLocal }
    single<CrashTrackingUserId> { CrashTrackingUserIdDefault(get(), get()) }
    single<CurrentScreenProvider> { Factory.currentScreenProvider(this) }
    single<CurrentScreenProviderDefault> { CurrentScreenProviderDefault(get(), get(NamedScope.CoroutineScopeMain)) }
    single<DateTimeFormatter> { DateTimeFormatterDefault(get()) }
    single<DeviceCountry> { get<DeviceCountryTelephony>() }
    single<DeviceCountryIpTelephony> { DeviceCountryIpTelephony(get()) }
    single<DeviceCountryTelephony> { DeviceCountryTelephony(get()) }
    single<DeviceInfo> { Factory.deviceInfo(this) }
    single<DeviceModel> { Factory.deviceModel(this) }
    single<DevicePreferenceStorage> { Factory.devicePreferenceStorage(this) }
    single<DevicePreferenceStorageDefault> { DevicePreferenceStorageDefault(get(), get(NamedScope.DeviceSettings), get(), get(NamedScope.CoroutineScopeMain)) }
    single<DeviceRecordManager> { DeviceRecordManagerDefault(get(), get(), get()) }
    single<DeviceRefreshRate> { Factory.deviceRefreshRate(this) }
    single<DeviceSpec> { Factory.deviceSpec(this) }
    single<DeviceState> { Factory.deviceState(this) }
    single<EncryptionManager> { Factory.encryptionManager(this) }
    single<FlavorConfig> { Factory.flavorConfig(this) }
    single<ImageBucketManager> { ImageBucketManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeMainImmediate)) }
    single<ImageHostManager> { ImageHostManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ImageHostManagerConfig> { ImageHostManagerConfigDefault(get()) }
    single<ImageHostPlatformConfig> { Factory.imageHostPlatformConfig(this) }
    single<ImageHostUrlMapper> { ImageHostUrlMapperImgix(get()) }
    single<ImageHostUrlMapperConfig> { ImageHostUrlMapperConfigImgix() }
    single<ImageLoader> { Factory.imageLoader(this) }
    single<ImageLoaderSeiko> { Factory.imageLoaderSeiko(this) }
    single<ImagePrefetchManager> { ImagePrefetchManagerDefault(get(), get()) }
    single<ImagePrefetcher> { Factory.imagePrefetcher(this) }
    single<ImagePrefetcherSeikoImageLoader> { ImagePrefetcherSeikoImageLoader(get(), get()) }
    single<ImageRepository> { ImageRepositoryDefault(get()) }
    single<ImageScaler> { ImageScalerDefault(get()) }
    single<ImageSizeMapper> { ImageSizeMapperDefault(get(), get()) }
    single<ImageViewSpecFactory> { ImageViewSpecFactoryDefault(get(), get(), get()) }
    single<InAppBrowserManager> { Factory.inAppBrowserManager(this) }
    single<InAppReviewManager> { Factory.inAppReviewManager(this) }
    single<InAppReviewRequestManager> { InAppReviewRequestManagerDefault(get(), get(), get()) }
    single<InAppReviewRequestManagerConfig> { InAppReviewRequestManagerConfigDefault(get()) }
    single<InstantAppManager> { Factory.instantAppManager(this) }
    single<InstantAppManagerNoOp> { InstantAppManagerNoOp() }
    single<InteropBridge> { Factory.interopBridge(this) }
    single<Lazy<AppState>>(NamedScope.LazyAppState) { getLazy() }
    single<Lazy<AppStateAltProcess>>(NamedScope.LazyAppStateAltProcess) { getLazy() }
    single<Lazy<AppStateMainProcess>>(NamedScope.LazyAppStateMainProcess) { getLazy() }
    single<Lazy<AppVisibilityDefaultProcess>>(NamedScope.LazyAppVisibilityDefaultProcess) { getLazy() }
    single<Lazy<AppVisibilityWallpaperProcess>>(NamedScope.LazyAppVisibilityWallpaperProcess) { getLazy() }
    single<Lazy<CrashTracking>>(NamedScope.LazyCrashTrackingRemote) { getLazy(NamedScope.CrashTrackingRemote) }
    single<Lazy<CrashTrackingLocal>>(NamedScope.LazyCrashTrackingLocal) { getLazy() }
    single<Lazy<InstantAppManagerNoOp>>(NamedScope.LazyInstantAppManagerNoOp) { getLazy() }
    single<Lazy<LicenseCacheAltProcess>>(NamedScope.LazyLicenseCacheAltProcess) { getLazy() }
    single<Lazy<LicenseCacheMainProcess>>(NamedScope.LazyLicenseCacheMainProcess) { getLazy() }
    single<Lazy<PreferenceStorage>>(NamedScope.LazyPreferenceStorage) { getLazy() }
    single<Lazy<Process>>(NamedScope.LazyProcess) { getLazy() }
    single<Lazy<ProcessBridgeAltProcess>>(NamedScope.LazyProcessBridgeAltProcess) { getLazy() }
    single<Lazy<ProcessBridgeMainProcess>>(NamedScope.LazyProcessBridgeMainProcess) { getLazy() }
    single<Lazy<UserPreferences>>(NamedScope.LazyUserPreferences) { getLazy() }
    single<Lazy<UserPreferencesAltProcess>>(NamedScope.LazyUserPreferencesAltProcess) { getLazy() }
    single<Lazy<UserPreferencesMainProcess>>(NamedScope.LazyUserPreferencesMainProcess) { getLazy() }
    single<LicenseCache> { Factory.licenseCache(this) }
    single<LicenseCacheAltProcess> { LicenseCacheAltProcess(get(), get(NamedScope.LicenseSettings), get(), get(NamedScope.CoroutineScopeMain)) }
    single<LicenseCacheMainProcess> { LicenseCacheMainProcess(get(), get(NamedScope.LicenseSettings), get(), get(NamedScope.CoroutineScopeMain)) }
    single<MainThreadChecker> { Factory.mainThreadChecker() }
    single<MediaMapRepository> { Factory.mediaMapRepository(this) }
    single<MediaMapRepositoryDefault> { MediaMapRepositoryDefault(get(), get(), get(), get(), get(), get(), get(), get()) }
    single<MediaMapRepositoryConfig> { Factory.mediaMapRepositoryConfig(this) }
    single<MediaMapRepositoryConfigDefault> { MediaMapRepositoryConfigDefault(get()) }
    single<ModuleInitializers> { Factory.moduleInitializers(this) }
    single<MonitoringManager> { MonitoringManagerDefault(get(), get(), get(NamedScope.LazyCrashTrackingLocal), get(NamedScope.LazyCrashTrackingRemote), get(NamedScope.LazyUserPreferences), get(NamedScope.CoroutineScopeMain)) }
    single<NavigationManager> { NavigationManagerDefault() }
    single<NetworkState> { Factory.networkState(this) }
    single<OssLicenseRepository> { OssLicenseRepositoryPreset }
    single<PreferenceDefaults> { PreferenceDefaultsFlavor(get(), get(), get()) }
    single<PreferenceDefaultsProvider> { get<PreferenceDefaultsProviderDefault>() }
    single<PreferenceDefaultsProviderConfig> { get<PreferenceDefaultsProviderConfigDefault>() }
    single<PreferenceDefaultsProviderConfigDefault> { PreferenceDefaultsProviderConfigDefault(get(), get(), get()) }
    single<PreferenceDefaultsProviderDefault> { PreferenceDefaultsProviderDefault(get(), get()) }
    single<PreferenceStorage> { Factory.preferenceStorage(this) }
    single<PrivacyMessagingManager> { Factory.privacyMessagingManager(this) }
    single<ProcessBridge> { Factory.processBridge(this) }
    single<ProcessBridgeAltProcess> { ProcessBridgeAltProcess(get(), get(NamedScope.ToMainProcessBus)) }
    single<ProcessBridgeMainProcess> { ProcessBridgeMainProcess(get(), get(NamedScope.ToAltProcessBus)) }
    single<ProcessBridgePreferenceManager> { Factory.processBridgePreferenceManager(this) }
    single<ProcessBusAlt>(NamedScope.ToAltProcessBus) { Factory.toAltProcessBus(this) }
    single<ProcessBusMain>(NamedScope.ToMainProcessBus) { Factory.toMainProcessBus(this) }
    single<ProcessListenerManager> { ProcessListenerManager() }
    single<RandomManager> { RandomManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<RemoteConfig> { Factory.remoteConfig(this) }
    single<RemoteConfigData> { Factory.remoteConfigData(this) }
    single<RemoteConfigDataDefaultsProvider> { RemoteConfigDataDefaultsProviderDefault }
    single<RemoteConfigPreset> { RemoteConfigPreset() }
    single<RemotePaywallEventManager> { Factory.remotePaywallEventManager(this) }
    single<RemotePaywallEventManagerDefault> { RemotePaywallEventManagerDefault(get(), get(), get()) }
    single<RemotePaywallEventRepository> { Factory.remotePaywallEventRepository(this) }
    single<RemotePaywallManager> { Factory.remotePaywallManager(this) }
    single<RenderTimeDelta> { RenderTimeDeltaFrameInterval(get()) }
    single<RemoteServerContentCache> { RemoteServerContentCacheDefault(get(NamedScope.RemoteServerContentSettings), get(NamedScope.CoroutineScopeIo)) }
    single<ResourceRepository> { ResourceRepositoryDefault() }
    single<SettingKeyManager> { SettingKeyManager() }
    single<SettingManager> { SettingManagerDefault(get(), get(NamedScope.CoroutineScopeMain)) }
    single<Settings>(NamedScope.CacheFileMediaMap) { Factory.cacheFileMediaMapSettings(this) }
    single<Settings>(NamedScope.DeviceSettings) { Factory.deviceSettings(this) }
    single<Settings>(NamedScope.LicenseSettings) { Factory.licenseSettings(this) }
    single<Settings>(NamedScope.RemoteServerContentSettings) { Factory.remoteServerContentSettings(this) }
    single<Settings>(NamedScope.UserSettings) { Factory.userSettings(this) }
    single<ShapeClipperComposable> { ShapeClipperComposableDefault(get()) }
    single<ShapeMapperComposable> { ShapeMapperComposableDefault }
    single<SizedImageMapper> { SizedImageMapperDefault(get()) }
    single<StringArbitrator> { StringArbitratorDefault(get(), get(), get()) }
    single<StringRepository> { StringRepositoryDefault(get(), get()) }
    single<Strings> { get<StringRepository>() }
    single<SystemPhotoPicker> { Factory.systemPhotoPicker(this) }
    single<SystemPhotoViewer> { Factory.systemPhotoViewer(this) }
    single<SystemWallpaperManager> { Factory.systemWallpaperManager(this) }
    single<TranslationRepository> { get<TranslationRepositorySingle>(NamedScope.TranslationRepositoryEnglish) }
    single<TranslationRepositoryConfig> { get<TranslationRepositoryConfigDefault>() }
    single<TranslationRepositoryConfigDefault> { TranslationRepositoryConfigDefault(get()) }
//    single<TranslationRepositoryDefault> { TranslationRepositoryDefault(get(), get(NamedScope.TranslationRepositoryEnglish), get(NamedScope.CoroutineScopeIo)) }
    single<TranslationRepositorySingle>(NamedScope.TranslationRepositoryEnglish) { TranslationRepositoryEnglish() }
    single<TypefaceRepository> { Factory.typefaceRepository(this) }
    single<UserPreferences> { Factory.userPreferences(this) }
    single<UserPreferencesAltProcess> { UserPreferencesAltProcess(get(), get(NamedScope.UserSettings), get(), get(NamedScope.CoroutineScopeMain)) }
    single<UserPreferencesMainProcess> { UserPreferencesMainProcess(get()) }
    single<ViewSpecFactory> { ViewSpecFactoryDefault(get(), get(), get(), get()) }
    single<WindowManager> { WindowManagerDefault(get()) }
}