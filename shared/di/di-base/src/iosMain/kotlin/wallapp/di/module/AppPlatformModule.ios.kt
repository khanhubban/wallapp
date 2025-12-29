package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.apprestarter.AppRestarter
import wallapp.apprestarter.AppRestarterNoOp
import wallapp.buildconfig.BuildConfig
import wallapp.device.DeviceId
import wallapp.device.memory.DeviceMemory
import wallapp.device.memory.DeviceMemoryMock
import wallapp.di.FactoryIos
import wallapp.di.NamedScope
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.cache.ImageCacheKeyManagerNoOp
import wallapp.image.hash.ImageHashBitmapMapperIos
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.hash.ImageHashDecoderIos
import wallapp.image.hash.blur.BlurHashDecoderBitmapIos
import wallapp.image.hash.blur.BlurHashDecoderBitmapIosSynchronized
import wallapp.inappreview.InAppReviewManagerIos
import wallapp.inappupdate.InAppUpdateChecker
import wallapp.inappupdate.InAppUpdateCheckerRemoteConfig
import wallapp.language.LanguageRepository
import wallapp.language.LanguageRepositoryIos
import wallapp.navigation.RewardAdNavigator
import wallapp.navigation.RewardAdNavigatorIos
import wallapp.pixel.view.ViewRenderer
import wallapp.process.Process
import wallapp.process.ProcessMockDefault
import wallapp.system.toast.ToastDisplayController
import wallapp.system.toast.ToastDisplayControllerNoOp
import wallapp.system.unit.SystemUnitManager
import wallapp.system.unit.SystemUnitManagerIos
import wallapp.system.window.WindowFrameManager
import wallapp.theme.SystemTheme
import wallapp.theme.SystemThemeIos
import wallapp.ui.ViewRendererApp
import wallapp.wallpaper.cache.BaseCacheSuspend
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.current.CurrentWallpaperManagerNoOp
import wallapp.wallpaper.saver.WallpaperSaverManager
import wallapp.wallpaper.saver.WallpaperSaverManagerIos
import wallapp.wallpaper.static.StaticWallpaperManager
import wallapp.wallpaper.static.StaticWallpaperManagerIos
import wallapp.worker.BackgroundWorkScheduler
import wallapp.worker.BackgroundWorkSchedulerIos

@Suppress("RemoveExplicitTypeArguments")
val AppPlatformModule: Module = module {
    single<AppRestarter> { get<AppRestarterNoOp>() }
    single<AppRestarterNoOp> { AppRestarterNoOp }
    single<BackgroundWorkScheduler> { BackgroundWorkSchedulerIos(get()) }
    single<BaseCacheSuspend> { FactoryIos.baseCacheSuspend() }
    single<BlurHashDecoderBitmapIos> { BlurHashDecoderBitmapIosSynchronized(get(), get()) }
    single<BuildConfig> { FactoryIos.buildConfig(this) }
    single<CurrentWallpaperManager> { CurrentWallpaperManagerNoOp }
    single<DeviceId> { FactoryIos.deviceId(this) }
    single<DeviceMemory> { DeviceMemoryMock() }
    single<ImageCacheKeyManager> { ImageCacheKeyManagerNoOp }
    single<ImageHashBitmapMapperIos> { ImageHashBitmapMapperIos }
    single<ImageHashDecoder> { ImageHashDecoderIos(get()) }
    single<InAppReviewManagerIos> { InAppReviewManagerIos() }
    single<InAppUpdateChecker> { InAppUpdateCheckerRemoteConfig(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<LanguageRepository> { LanguageRepositoryIos }
    single<Process> { ProcessMockDefault() }
    single<RewardAdNavigator> { RewardAdNavigatorIos(get()) }
    single<StaticWallpaperManager> { StaticWallpaperManagerIos(get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<String>(NamedScope.ApplicationId) { FactoryIos.applicationId(this) }
    single<SystemTheme> { get<SystemThemeIos>() }
    single<SystemThemeIos> { SystemThemeIos() }
    single<SystemUnitManager> { SystemUnitManagerIos() }
    single<ToastDisplayController> { ToastDisplayControllerNoOp }
    single<ViewRenderer> { ViewRendererApp }
    single<WallpaperSaverManager> { get<WallpaperSaverManagerIos>() }
    single<WallpaperSaverManagerIos> { WallpaperSaverManagerIos(get(), get(), get(), get(),get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<WindowFrameManager> { FactoryIos.windowFrameManager(this) }
}