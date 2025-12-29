package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.ad.AdManager
import wallapp.ad.AdManagerConfig
import wallapp.ad.AdManagerConfigDefault
import wallapp.ad.AdManagerDefault
import wallapp.ad.InternalAdFactory
import wallapp.ad.InternalAdFactoryDefault
import wallapp.app.AppViewModel
import wallapp.app.AppViewModelFactory
import wallapp.apphistory.AppHistoryManager
import wallapp.apphistory.AppHistoryManagerCache
import wallapp.apphistory.AppHistoryManagerDefault
import wallapp.content.model.WallpaperDefaults
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.prefetch.ContentPrefetcherFactoryDefault
import wallapp.content.state.appicon.AppIconSpecFactory
import wallapp.content.state.artist.ArtistViewStateFactory
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.content.state.home.HomeFilterManager
import wallapp.content.state.home.HomeViewStateFactory
import wallapp.content.state.index.IndexTabSpecFactory
import wallapp.content.state.settings.SettingViewStateFactory
import wallapp.content.state.showcase.ads.ShowcaseAdsViewStateMapper
import wallapp.content.state.showcase.nativefeed.ShowcaseIosNativeFeedViewStateMapper
import wallapp.content.state.upgrade.plus.paywall.PaywallViewStateMapper
import wallapp.content.state.wallpaper.WallpaperShowcaseViewStateFactory
import wallapp.data.DataRepository
import wallapp.data.DataRepositoryDefault
import wallapp.data.content.ContentCacheConfig
import wallapp.data.content.ContentCacheConfigDefault
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentCacheManagerDefault
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.download.ActiveDownloadManager
import wallapp.download.ActiveDownloadManagerDefault
import wallapp.download.DownloadManager
import wallapp.download.DownloadManagerDefault
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.UrlDownloader
import wallapp.image.hash.blur.BlurHashDecoder
import wallapp.image.hash.blur.BlurHashDecoderDefault
import wallapp.lifecycle.AppLifecycleManager
import wallapp.lifecycle.AppLifecycleManagerDefault
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkErrorBroadcasterDefault
import wallapp.network.NetworkRefreshManager
import wallapp.network.NetworkRefreshManagerDefault
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.network.NetworkRefreshTriggerBroadcasterDefault
import wallapp.network.NetworkUserManager
import wallapp.network.NetworkUserManagerFirebase
import wallapp.onboarding.OnboardingManager
import wallapp.onboarding.OnboardingManagerDefault
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.globaloverlay.GlobalOverlayManagerDefault
import wallapp.pixel.render.RenderConfig
import wallapp.pixel.render.RenderViewIdFactory
import wallapp.pixel.resource.DefaultResources
import wallapp.pixel.resource.DefaultResourcesDefault
import wallapp.pixel.view.DefaultViewSpec
import wallapp.pixel.view.UIKitFactory
import wallapp.pixel.view.ViewAlignmentMapper
import wallapp.prefs.DevicePreferenceStorageDefault
import wallapp.remoteapi.RemoteApi
import wallapp.remoteapi.RemoteApiDefault
import wallapp.remoteapi.RemoteApiEncryptionConfig
import wallapp.remoteapi.RemoteApiEncryptionConfigDefault
import wallapp.remoteapi.RemoteApiSecretManager
import wallapp.remoteapi.RemoteApiSecretManagerDefault
import wallapp.render.RenderConfigDefault
import wallapp.screen.ScreenManager
import wallapp.screen.ScreenManagerDefault
import wallapp.theme.ThemeManager
import wallapp.theme.ThemeManagerDefault
import wallapp.time.EpochTicker
import wallapp.time.EpochTickerConfig
import wallapp.time.EpochTickerConfigDefault
import wallapp.time.EpochTickerDefault
import wallapp.ui.view.DefaultViewSpecDefault
import wallapp.ui.view.RenderViewIdFactoryDefault
import wallapp.ui.view.ViewAlignmentMapperDefault
import wallapp.view.ViewAlignmentFactory
import wallapp.view.ViewAlignmentFactoryDefault
import wallapp.view.ViewEventFactory
import wallapp.view.ViewEventFactoryDefault
import wallapp.view.ViewFactory
import wallapp.view.ViewFactoryDefault
import wallapp.view.ViewIdMapper
import wallapp.view.ViewIdMapperDefault
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecArbitratorConfig
import wallapp.view.ViewSpecArbitratorConfigDefault
import wallapp.view.ViewSpecArbitratorDefault
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateFactoryDefault
import wallapp.view.ViewStateMapper
import wallapp.view.ViewStateMapperDefault
import wallapp.view.ViewStateRefresher
import wallapp.view.ViewStateRefresherDefault
import wallapp.view.feed.FeedFormatter
import wallapp.view.feed.FeedFormatterDefault
import wallapp.view.menu.MenuItemFactory
import wallapp.view.shape.ShapeSpecFactory
import wallapp.view.shape.ShapeSpecFactoryDefault
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelFactoryCommon
import wallapp.viewmodel.ViewModelFactoryDefault
import wallapp.viewmodel.ViewModelFactoryPlatform
import wallapp.viewmodel.ViewModelProviderFactory
import wallapp.wallpaper.actionbutton.CollectionActionButtonManager
import wallapp.wallpaper.actionbutton.CollectionActionButtonManagerDefault
import wallapp.wallpaper.actionbutton.CollectionActionButtonMapper
import wallapp.wallpaper.actionbutton.WallpaperActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperActionButtonManagerDefault
import wallapp.wallpaper.actionbutton.WallpaperActionButtonMapper
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonManager
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonManagerDefault
import wallapp.wallpaper.actionbutton.WallpaperShowcaseActionButtonMapper
import wallapp.wallpaper.app.AppWallpaperManager
import wallapp.wallpaper.app.AppWallpaperManagerDefault
import wallapp.wallpaper.live.LiveWallpaperManager
import wallapp.wallpaper.live.LiveWallpaperManagerKmm
import wallapp.wallpaper.live.LiveWallpaperManagerNoOp
import wallapp.wallpaper.set.SetWallpaperManager
import wallapp.wallpaper.set.SetWallpaperManagerDefault

@Suppress("RemoveExplicitTypeArguments")
val WallAppModule: Module = module {
    single<ActiveDownloadManager> { ActiveDownloadManagerDefault(get(NamedScope.CoroutineScopeIo)) }
    single<AdManager> { AdManagerDefault(get(), get(), get(), get()) }
    single<AdManagerConfig> { AdManagerConfigDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<AlertManager> { Factory.dialogManager(this) }
    single<AppHistoryManager> { AppHistoryManagerDefault(get(), get()) }
    single<AppHistoryManagerCache> { get<DevicePreferenceStorageDefault>() }
    single<AppIconSpecFactory> { AppIconSpecFactory(get(), get(), get()) }
    single<AppLifecycleManager> { AppLifecycleManagerDefault() }
    single<AppViewModel> { AppViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<AppWallpaperManager> { AppWallpaperManagerDefault(get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<ArtistViewStateFactory> { ArtistViewStateFactory(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<BlurHashDecoder> { BlurHashDecoderDefault }
    single<CollectionActionButtonManager> { CollectionActionButtonManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<CollectionActionButtonMapper> { CollectionActionButtonMapper(get(), get(), get(), get()) }
    single<ContentCacheConfig> { Factory.contentCacheConfig(this) }
    single<ContentCacheConfigDefault> { ContentCacheConfigDefault(get(), get()) }
    single<ContentCacheManager> { ContentCacheManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<ContentPrefetcherFactory> { ContentPrefetcherFactoryDefault(get(), get(), get()) }
    single<DataRepository> { Factory.dataRepository(this) }
    single<DataRepositoryDefault> { DataRepositoryDefault() }
    single<DefaultResources> { DefaultResourcesDefault(get()) }
    single<DefaultViewSpec> { DefaultViewSpecDefault(get()) }
    single<DownloadManager> { DownloadManagerDefault(get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<EpochTicker> { get<EpochTickerDefault>() }
    single<EpochTickerConfig> { get<EpochTickerConfigDefault>() }
    single<EpochTickerConfigDefault> { EpochTickerConfigDefault(get()) }
    single<EpochTickerDefault> { EpochTickerDefault(get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<ErrorViewStateMapper> { ErrorViewStateMapper(get(), get(), get(), get(), get(), get()) }
    single<FirebaseStorageDownloader> { Factory.firebaseStorageDownloader(this) }
    single<FeedFormatter> { FeedFormatterDefault(get(), get(), get(), get(), get()) }
    single<NetworkRefreshTriggerBroadcaster> { NetworkRefreshTriggerBroadcasterDefault(get(), get(NamedScope.CoroutineScopeIo)) }
    single<GlobalOverlayManager> { GlobalOverlayManagerDefault() }
    single<HomeFilterManager> { HomeFilterManager(get()) }
    single<HomeViewStateFactory> { HomeViewStateFactory(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<IndexTabSpecFactory> { IndexTabSpecFactory(get(), get(), get()) }
    single<InternalAdFactory> { InternalAdFactoryDefault(get(), get(), get(), get(), get(), get(), get(), get()) }
    single<Lazy<AppViewModel>>(NamedScope.LazyAppViewModel) { getLazy() }
    single<Lazy<AppViewModelFactory>>(NamedScope.LazyAppViewModelFactory) { getLazy() }
    single<Lazy<ViewModelFactory>>(NamedScope.LazyViewModelFactory) { getLazy() }
    single<LiveWallpaperManager> { get<LiveWallpaperManagerNoOp>() /*get(LiveWallpaperManagerKmm::class)*/ }
    single<LiveWallpaperManagerKmm> { LiveWallpaperManagerKmm(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<LiveWallpaperManagerNoOp> { LiveWallpaperManagerNoOp() }
    single<MenuItemFactory> { MenuItemFactory(get(), get(), get(), get(), get(), get()) }
    single<NetworkErrorBroadcaster> { NetworkErrorBroadcasterDefault() }
    single<NetworkRefreshManager> { NetworkRefreshManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo), get(NamedScope.CoroutineScopeMain)) }
    single<NetworkUserManager> { Factory.networkUserManager(this) }
    single<NetworkUserManagerFirebase> { NetworkUserManagerFirebase(get(), get(NamedScope.CoroutineScopeIo)) }
    single<OnboardingManager> { OnboardingManagerDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<PaywallViewStateMapper> { PaywallViewStateMapper(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<RemoteApi> { RemoteApiDefault(get(), get(), get(), get()) }
    single<RemoteApiEncryptionConfig> { RemoteApiEncryptionConfigDefault(get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<RemoteApiSecretManager> { RemoteApiSecretManagerDefault() }
    single<RenderConfig> { RenderConfigDefault }
    single<RenderViewIdFactory> { RenderViewIdFactoryDefault() }
    single<ScreenManager> { ScreenManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<SetWallpaperManager> { SetWallpaperManagerDefault(get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<SettingViewStateFactory> { SettingViewStateFactory(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ShapeSpecFactory> { ShapeSpecFactoryDefault() }
    single<ShowcaseAdsViewStateMapper> { ShowcaseAdsViewStateMapper(get(), get(), get(), get(), get()) }
    single<ShowcaseIosNativeFeedViewStateMapper> { ShowcaseIosNativeFeedViewStateMapper(get(), get(), get(), get()) }
    single<ThemeManager> { ThemeManagerDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<UIKitFactory> { Factory.uiKitFactory(this) }
    single<UrlDownloader> { Factory.urlDownloader(this) }
    single<ViewAlignmentFactory> { ViewAlignmentFactoryDefault() }
    single<ViewAlignmentMapper> { ViewAlignmentMapperDefault() }
    single<ViewEventFactory> { ViewEventFactoryDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<ViewFactory> { ViewFactoryDefault(get(), get(), get(), get(), get()) }
    single<ViewIdMapper> { ViewIdMapperDefault() }
    single<ViewModelFactory> { Factory.viewModelFactory(this) }
    single<ViewModelFactoryCommon> { Factory.viewModelFactoryCommon(this) }
    single<ViewModelFactoryDefault> { ViewModelFactoryDefault(get(), get()) }
    single<ViewModelFactoryPlatform> { Factory.viewModelFactoryPlatform(this) }
    single<ViewModelProviderFactory> { Factory.viewModelProviderFactory(this) }
    single<ViewSpecArbitrator> { ViewSpecArbitratorDefault(get()) }
    single<ViewSpecArbitratorConfig> { get<ViewSpecArbitratorConfigDefault>() }
    single<ViewSpecArbitratorConfigDefault> { ViewSpecArbitratorConfigDefault(get(), get(), get(NamedScope.CoroutineScopeMainImmediate)) }
    single<ViewStateFactory> { ViewStateFactoryDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<ViewStateMapper> { ViewStateMapperDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<ViewStateRefresher> { ViewStateRefresherDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<WallpaperActionButtonManager> { WallpaperActionButtonManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<WallpaperActionButtonMapper> { WallpaperActionButtonMapper(get(), get(), get(), get()) }
    single<WallpaperDefaults> { Factory.wallpaperDefaults(this) }
    single<WallpaperShowcaseActionButtonManager> { WallpaperShowcaseActionButtonManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<WallpaperShowcaseActionButtonMapper> { WallpaperShowcaseActionButtonMapper(get(), get(), get(), get()) }
    single<WallpaperShowcaseViewStateFactory> { WallpaperShowcaseViewStateFactory(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
}
