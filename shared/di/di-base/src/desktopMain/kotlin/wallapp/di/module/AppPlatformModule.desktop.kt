package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.apprestarter.AppRestarter
import wallapp.apprestarter.AppRestarterNoOp
import wallapp.buildconfig.BuildConfig
import wallapp.device.DeviceId
import wallapp.device.memory.DeviceMemory
import wallapp.device.memory.DeviceMemoryMock
import wallapp.di.FactoryDesktop
import wallapp.di.NamedScope
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.cache.ImageCacheKeyManagerNoOp
import wallapp.image.hash.ImageHashBitmapMapperDesktop
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.hash.ImageHashDecoderDesktop
import wallapp.image.hash.blur.BlurHashDecoderBitmapDesktop
import wallapp.image.hash.blur.BlurHashDecoderBitmapDesktopDefault
import wallapp.inappupdate.InAppUpdateChecker
import wallapp.inappupdate.InAppUpdateCheckerMock
import wallapp.language.LanguageRepository
import wallapp.language.LanguageRepositoryNoOp
import wallapp.navigation.RewardAdNavigator
import wallapp.navigation.RewardAdNavigatorNoOp
import wallapp.pixel.view.ViewRenderer
import wallapp.process.Process
import wallapp.process.ProcessMockDefault
import wallapp.system.toast.ToastDisplayController
import wallapp.system.toast.ToastDisplayControllerNoOp
import wallapp.system.unit.SystemUnitManager
import wallapp.system.unit.SystemUnitManagerDesktop
import wallapp.system.window.WindowFrameManager
import wallapp.theme.SystemTheme
import wallapp.theme.SystemThemeNoOp
import wallapp.ui.ViewRendererApp
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.current.CurrentWallpaperManagerNoOp
import wallapp.wallpaper.saver.WallpaperSaverManager
import wallapp.wallpaper.saver.WallpaperSaverManagerNoOp
import wallapp.wallpaper.static.StaticWallpaperManager
import wallapp.wallpaper.static.StaticWallpaperManagerNoOp
import wallapp.worker.BackgroundWorkScheduler
import wallapp.worker.BackgroundWorkSchedulerNoOp

@Suppress("RemoveExplicitTypeArguments")
val AppPlatformModule: Module = module {
    single<AppRestarter> { get<AppRestarterNoOp>() }
    single<AppRestarterNoOp> { AppRestarterNoOp }
    single<BackgroundWorkScheduler> { BackgroundWorkSchedulerNoOp }
    single<BlurHashDecoderBitmapDesktop> { BlurHashDecoderBitmapDesktopDefault(get(), get()) }
    single<BuildConfig> { FactoryDesktop.buildConfig(this) }
    single<CurrentWallpaperManager> { CurrentWallpaperManagerNoOp }
    single<DeviceId> { FactoryDesktop.deviceId(this) }
    single<DeviceMemory> { DeviceMemoryMock() }
    single<ImageCacheKeyManager> { ImageCacheKeyManagerNoOp }
    single<ImageHashBitmapMapperDesktop> { ImageHashBitmapMapperDesktop }
    single<ImageHashDecoder> { ImageHashDecoderDesktop(get()) }
    single<InAppUpdateChecker> { InAppUpdateCheckerMock() }
    single<LanguageRepository> { LanguageRepositoryNoOp }
    single<Process> { ProcessMockDefault() }
    single<RewardAdNavigator> { RewardAdNavigatorNoOp }
    single<StaticWallpaperManager> { StaticWallpaperManagerNoOp }
    single<String>(NamedScope.ApplicationId) { FactoryDesktop.applicationId(this) }
    single<SystemTheme> { SystemThemeNoOp() }
    single<SystemUnitManager> { SystemUnitManagerDesktop() }
    single<ToastDisplayController> { ToastDisplayControllerNoOp }
    single<ViewRenderer> { ViewRendererApp }
    single<WallpaperSaverManager> { WallpaperSaverManagerNoOp }
    single<WindowFrameManager> { FactoryDesktop.windowFrameManager(this) }
}