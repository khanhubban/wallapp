package wallapp.di.module

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.ads.inline.support.InlineAdConfigFactoryAdMob
import wallapp.appicon.AppIconManagerAndroid
import wallapp.application.ApplicationLifecycleObserver
import wallapp.apprestarter.AppRestarter
import wallapp.apprestarter.AppRestarterDefault
import wallapp.apprestarter.AppRestarterNoOp
import wallapp.appstore.AppStoreDescriptor
import wallapp.appstore.PlayStoreDescriptor
import wallapp.apptypeface.TypefaceRepositoryAndroid
import wallapp.asset.AssetManager
import wallapp.asset.AssetManagerSystem
import wallapp.buildconfig.BuildConfig
import wallapp.crashtracking.crashkios.CrashKiosInitializerAndroid
import wallapp.device.DeviceId
import wallapp.device.memory.DeviceMemory
import wallapp.device.refreshrate.DeviceRefreshRateSystem
import wallapp.device.state.DeviceStateSystem
import wallapp.di.FactoryAndroid
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.download.FirebaseStorageDownloaderAndroid
import wallapp.download.UrlDownloaderAndroid
import wallapp.image.cache.BitmapDiskCache
import wallapp.image.cache.BitmapDiskCacheDefault
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashAndroidBitmapMapper
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.hash.ImageHashDecoderAndroid
import wallapp.image.hash.blur.BlurHashDecoderAndroidBitmap
import wallapp.image.hash.blur.BlurHashDecoderAndroidBitmapDefault
import wallapp.inappbrowser.InAppBrowserManagerAndroid
import wallapp.inappreview.InAppReviewManagerAndroid
import wallapp.inappupdate.InAppUpdateChecker
import wallapp.inappupdate.InAppUpdateCheckerRemoteConfig
import wallapp.instantapp.InstantAppManagerSystem
import wallapp.language.LanguageRepository
import wallapp.language.LanguageRepositoryAndroid
import wallapp.navigation.RewardAdNavigator
import wallapp.navigation.RewardAdNavigatorAndroid
import wallapp.network.NetworkStateAndroid
import wallapp.permission.PermissionsProvider
import wallapp.permission.PermissionsProviderSystem
import wallapp.pixel.view.ViewRenderer
import wallapp.preferences.SharedPreferencesUpgrader
import wallapp.preferences.SharedPreferencesUpgraderNoOp
import wallapp.prefs.DevicePreferenceStorage
import wallapp.privacymessaging.PrivacyMessagingManagerGoogleAndroid
import wallapp.process.Process
import wallapp.process.bus.ProcessBusAidl
import wallapp.process.bus.ProcessBusIntraBroadcast
import wallapp.system.photo.picker.SystemPhotoPickerAndroid
import wallapp.system.toast.ToastDisplayController
import wallapp.system.toast.ToastDisplayControllerDefault
import wallapp.system.ui.mode.UiModeManager
import wallapp.system.ui.mode.UiModeManagerSystem
import wallapp.system.unit.SystemUnitManager
import wallapp.system.unit.SystemUnitManagerAndroid
import wallapp.system.wallpaper.AndroidWallpaperManager
import wallapp.system.wallpaper.AndroidWallpaperManagerDefault
import wallapp.system.wallpaper.SystemWallpaperManagerAndroid
import wallapp.system.window.WindowFrameManager
import wallapp.system.window.WindowFrameManagerAndroid
import wallapp.theme.SystemTheme
import wallapp.theme.SystemThemeAndroid
import wallapp.ui.ViewRendererApp
import wallapp.viewmodel.ViewModelProviderFactory
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.current.CurrentWallpaperManagerAndroid
import wallapp.wallpaper.current.CurrentWallpaperManagerDefault
import wallapp.wallpaper.saver.WallpaperSaverManager
import wallapp.wallpaper.saver.WallpaperSaverManagerAndroid
import wallapp.wallpaper.static.StaticWallpaperManager
import wallapp.wallpaper.static.StaticWallpaperManagerAndroid
import wallapp.worker.BackgroundWorkScheduler
import wallapp.worker.BackgroundWorkSchedulerAndroid
import coil.ImageLoader as CoilImageLoader

@Suppress("RemoveExplicitTypeArguments")
val AppPlatformModule: Module = module {
    single<AndroidWallpaperManager> { AndroidWallpaperManagerDefault(get(), get()) }
    single<AppIconManagerAndroid> { AppIconManagerAndroid(get(), get()) }
    single<AppRestarter> { AppRestarterDefault(get(), get()) }
    single<AppRestarterNoOp> { AppRestarterNoOp }
    single<AppStoreDescriptor> { get<PlayStoreDescriptor>() }
    single<ApplicationLifecycleObserver> { ApplicationLifecycleObserver(get(NamedScope.LazyProcess), get(), get(NamedScope.LazyAppState), get(NamedScope.LazyTimeRepository)) }
    single<AssetManager> { AssetManagerSystem(get()) }
    single<BackgroundWorkScheduler> { BackgroundWorkSchedulerAndroid(get()) }
    single<BitmapDiskCache> { BitmapDiskCacheDefault(get()) }
    single<BlurHashDecoderAndroidBitmap> { BlurHashDecoderAndroidBitmapDefault(get(), get()) }
    single<BuildConfig> { FactoryAndroid.createBuildConfig(get(), wallapp.di.base.BuildConfig.DEBUG) }
    single<CoilImageLoader> { FactoryAndroid.createCoilImageLoader(this) }
    single<CrashKiosInitializerAndroid> { CrashKiosInitializerAndroid() }
    single<CurrentWallpaperManager> { get<CurrentWallpaperManagerAndroid>() }
    single<CurrentWallpaperManagerAndroid> { CurrentWallpaperManagerAndroid(get(), get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<CurrentWallpaperManagerDefault> { CurrentWallpaperManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<DeviceId> { FactoryAndroid.deviceId(this) }
    single<DeviceMemory> { FactoryAndroid.deviceMemory(this) }
    single<DeviceRefreshRateSystem> { DeviceRefreshRateSystem(get()) }
    single<DeviceStateSystem> { DeviceStateSystem(get(), get(), get()) }
    single<FirebaseStorageDownloaderAndroid> { FirebaseStorageDownloaderAndroid() }
    single<ImageCacheKeyManager> { FactoryAndroid.createImageCacheKeyManager(this) }
    single<ImageHashAndroidBitmapMapper> { ImageHashAndroidBitmapMapper }
    single<ImageHashDecoder> { ImageHashDecoderAndroid(get()) }
    single<InAppBrowserManagerAndroid> { InAppBrowserManagerAndroid(get(), get(), get()) }
    single<InAppReviewManagerAndroid> { InAppReviewManagerAndroid(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<InAppUpdateChecker> { InAppUpdateCheckerRemoteConfig(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<InlineAdConfigFactoryAdMob> { InlineAdConfigFactoryAdMob(get(), get(), get(NamedScope.LazyInlineAdConfigFactory)) }
    single<InstantAppManagerSystem> { InstantAppManagerSystem(get()) }
    single<Intent>(NamedScope.MainAppIntent) { FactoryAndroid.appMainIntent(this) }
    single<LanguageRepository> { LanguageRepositoryAndroid(get()) }
    single<Lazy<DevicePreferenceStorage>>(NamedScope.LazyDevicePreferenceStorage) { getLazy() }
    single<Lazy<DeviceStateSystem>>(NamedScope.LazyDeviceStateSystem) { getLazy() }
    single<Lazy<InstantAppManagerSystem>>(NamedScope.LazyInstantAppManagerSystem) { getLazy() }
    single<NetworkStateAndroid> { NetworkStateAndroid(get(), get(), get()) }
    single<PermissionsProvider> { PermissionsProviderSystem(get(), get(), get(NamedScope.AccessibilityServiceName)) }
    single<PlayStoreDescriptor> { PlayStoreDescriptor }
    single<PrivacyMessagingManagerGoogleAndroid> { PrivacyMessagingManagerGoogleAndroid(get(), get()) }
    single<Process> { FactoryAndroid.process(this) }
    single<ProcessBusAidl> { ProcessBusAidl(get(), get(), get()) }
    single<ProcessBusIntraBroadcast> { ProcessBusIntraBroadcast(get(), get()) }
    single<RewardAdNavigator> { RewardAdNavigatorAndroid(get()) }
    single<SharedPreferences>(NamedScope.CacheFileMediaMap) { FactoryAndroid.cacheFileMediaMap(this) }
    single<SharedPreferences>(NamedScope.DeviceSharedPrefs) { FactoryAndroid.deviceSharedPrefs(this) }
    single<SharedPreferences>(NamedScope.LicenseBackupSettingsFilename) { FactoryAndroid.licenseBackupSharedPrefs(this) }
    single<SharedPreferences>(NamedScope.LicenseSharedPrefs) { FactoryAndroid.licenseSharedPrefs(this) }
    single<SharedPreferences>(NamedScope.UserSharedPrefs) { FactoryAndroid.userSharedPrefs(this) }
    single<SharedPreferencesUpgrader> { SharedPreferencesUpgraderNoOp() }
    single<StaticWallpaperManager> { StaticWallpaperManagerAndroid(get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<String>(NamedScope.AccessibilityServiceName) { FactoryAndroid.accessibilityServiceName() }
    single<String>(NamedScope.ApplicationId) { FactoryAndroid.applicationId(this) }
    single<SystemPhotoPickerAndroid> { SystemPhotoPickerAndroid(get(), get(NamedScope.CoroutineScopeMain)) }
    single<SystemTheme> { get(SystemThemeAndroid::class) }
    single<SystemThemeAndroid> { SystemThemeAndroid(get(), get()) }
    single<SystemUnitManager> { SystemUnitManagerAndroid(get()) }
    single<SystemWallpaperManagerAndroid> { SystemWallpaperManagerAndroid(get(), get(), get(), get(), get(), get()) }
    single<ToastDisplayController> { ToastDisplayControllerDefault(get()) }
    single<TypefaceRepositoryAndroid> { TypefaceRepositoryAndroid(get()) }
    single<UiModeManager> { UiModeManagerSystem(get()) }
    single<UrlDownloaderAndroid> { UrlDownloaderAndroid(get(), get(), get(), get()) }
    single<ViewModelProvider.Factory> { get(ViewModelProviderFactory::class) }
    single<ViewRenderer> { ViewRendererApp }
    single<WallpaperSaverManager> { WallpaperSaverManagerAndroid(get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<WindowFrameManager> { FactoryAndroid.windowFrameManager(this) }
    single<WindowFrameManagerAndroid> { WindowFrameManagerAndroid(get(), get()) }
}