
package wallapp.di.module

import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.account.data.AccountDataDefaults
import wallapp.account.data.AccountDataDefaultsDefault
import wallapp.account.data.AccountDataRepository
import wallapp.account.data.AccountDataRepositoryCombined
import wallapp.account.data.AccountDataRepositoryController
import wallapp.account.data.AccountDataRepositoryPreferences
import wallapp.billing.BillingSubscriptionExpiredHandler
import wallapp.billing.BillingSubscriptionExpiredHandlerDefault
import wallapp.content.network.map.NetworkContentMapper
import wallapp.content.network.repository.NetworkContentRepository
import wallapp.content.network.repository.NetworkContentRepositoryKtor
import wallapp.content.network.repository.NetworkContentRepositoryUrlDownloader
import wallapp.content.state.ContentStateMapper
import wallapp.content.state.ContentStateMapperDefault
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.colors.ContentColorManagerDefault
import wallapp.content.state.explore.ExploreRepository
import wallapp.content.state.explore.ExploreRepositoryDefault
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.messagebar.MessageBarManagerDefault
import wallapp.data.artist.ArtistRepository
import wallapp.data.artist.ArtistRepositoryNetwork
import wallapp.data.artist.ArtistStateRepository
import wallapp.data.artist.ArtistStateRepositoryDefault
import wallapp.data.collection.CollectionRepository
import wallapp.data.collection.CollectionRepositoryDefault
import wallapp.data.content.ContentCategoryFactory
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentRepositoryDefault
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.content.media.ContentMediaRepositoryDefault
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.data.favorite.FavoriteItemsRepositoryDefault
import wallapp.data.folder.FolderLinkRepository
import wallapp.data.folder.FolderLinkRepositoryDefault
import wallapp.data.folder.FolderRepository
import wallapp.data.folder.FolderRepositoryNetwork
import wallapp.data.folder.FolderStateRepository
import wallapp.data.folder.FolderStateRepositoryDefault
import wallapp.data.following.FollowingRepository
import wallapp.data.following.FollowingRepositoryDefault
import wallapp.data.model.ModelRepository
import wallapp.data.model.ModelRepositoryRaw
import wallapp.data.purchase.PurchaseRecordRepository
import wallapp.data.purchase.PurchaseRecordRepositoryDefault
import wallapp.data.showcase.ShowcaseRepository
import wallapp.data.showcase.ShowcaseRepositoryDefault
import wallapp.data.showcase.ShowcaseRepositoryHighlightsConfig
import wallapp.data.showcase.ShowcaseRepositoryHighlightsConfigDefault
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.data.wallpaper.WallpaperRepositoryDefault
import wallapp.data.wallpaper.WallpaperStateRepository
import wallapp.data.wallpaper.WallpaperStateRepositoryDefault
import wallapp.deeplink.DeepLinkManager
import wallapp.deeplink.DeepLinkManagerDefault
import wallapp.deeplink.DeepLinkMapper
import wallapp.deeplink.DeepLinkMapperDefault
import wallapp.deeplink.DeepLinkSlugResolver
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.entitlement.EntitlementManager
import wallapp.entitlement.EntitlementManagerDefault
import wallapp.entitlement.EntitlementRepository
import wallapp.entitlement.EntitlementRepositoryDefault
import wallapp.media.network.repository.NetworkMediaMapRepository
import wallapp.media.network.repository.NetworkMediaMapRepositoryConfig
import wallapp.media.network.repository.NetworkMediaMapRepositoryConfigDefault
import wallapp.media.network.repository.NetworkMediaMapRepositoryNetwork
import wallapp.mediamap.NetworkMediaCacheManager
import wallapp.mediamap.NetworkMediaCacheManagerDefault
import wallapp.network.NetworkStateManager
import wallapp.network.NetworkStateManagerDefault
import wallapp.purchase.PurchaseUiManager
import wallapp.purchase.PurchaseUiManagerDefault
import wallapp.remoteapi.RemoteEndpointsSpecRepository
import wallapp.remoteapi.RemoteEndpointsSpecRepositoryDefault
import wallapp.remoteendpoint.RemoteApiEndpointRepository
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryConfig
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryConfigDefault
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryDefault
import wallapp.remoteendpoint.RemoteEndpointsRepositoryNetwork
import wallapp.remoteendpoint.RemoteEndpointsRepositoryNetworkDefault
import wallapp.remoteendpoint.RemoteEndpointsRepositoryPreset
import wallapp.remoteendpoint.RemoteEndpointsRepositoryPresetDefault
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.wallpaper.download.ActiveWallpaperDownloadManagerDefault
import wallapp.wallpaper.download.WallpaperDownloadEventManager
import wallapp.wallpaper.download.WallpaperDownloadEventManagerDefault
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadManagerDefault
import wallapp.wallpaper.saver.WallpaperSaverManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusCache
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusCacheDefault
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusCacheDefaultData
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusCacheDefaultDataDefault
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusCacheMemory
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManagerDefault
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusRepository
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusRepositoryDefault

@Suppress("RemoveExplicitTypeArguments")
val ContentModule: Module = module {
    single<AccountDataDefaults> { AccountDataDefaultsDefault(get()) }
    single<AccountDataRepository> { Factory.accountDataRepository(this) }
    single<AccountDataRepositoryCombined> { Factory.accountDataRepositoryCombined(this) }
    single<AccountDataRepositoryController> { get<AccountDataRepositoryCombined>() }
    single<AccountDataRepositoryPreferences> { AccountDataRepositoryPreferences(get(), get(), get(NamedScope.CoroutineScopeIo)) }
//    single<ActiveWallpaperDownloadManager> { ActiveWallpaperDownloadManagerPreset(get(), get(NamedScope.CoroutineScopeMain)) }
    single<ActiveWallpaperDownloadManager> { ActiveWallpaperDownloadManagerDefault(get(), get()) }
    single<ArtistRepository> { ArtistRepositoryNetwork(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ArtistStateRepository> { ArtistStateRepositoryDefault(get(), get(), get(), get()) }
    single<BillingSubscriptionExpiredHandler> { BillingSubscriptionExpiredHandlerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<CollectionRepository> { CollectionRepositoryDefault(get()) }
    single<ContentCategoryFactory> { ContentCategoryFactory(get()) }
    single<ContentColorManager> { ContentColorManagerDefault(get(), get(NamedScope.CoroutineScopeMain)) }
    single<ContentMediaRepository> { ContentMediaRepositoryDefault(get(), get(), get()) }
    single<ContentRepository> { ContentRepositoryDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.LazyPurchasableRepository), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<ContentStateMapper> { ContentStateMapperDefault(get(), get()) }
    single<DeepLinkManager> { Factory.deepLinkManager(this) }
    single<DeepLinkManagerDefault> { DeepLinkManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<DeepLinkMapper> { DeepLinkMapperDefault(get(), get()) }
    single<DeepLinkSlugResolver> { DeepLinkSlugResolver }
    single<EntitlementManager> { EntitlementManagerDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<EntitlementRepository> { Factory.entitlementRepository(this) }
    single<EntitlementRepositoryDefault> { EntitlementRepositoryDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ExploreRepository> { ExploreRepositoryDefault(get(), get(), get(), get(), get(), get()) }
    single<FavoriteItemsRepository> { FavoriteItemsRepositoryDefault(get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<FolderLinkRepository> { FolderLinkRepositoryDefault(get()) }
    single<FolderRepository> { Factory.folderRepository(this) }
    single<FolderRepositoryNetwork> { FolderRepositoryNetwork(get(), get(NamedScope.CoroutineScopeMain)) }
    single<FolderStateRepository> { FolderStateRepositoryDefault(get(), get(NamedScope.LazyContentRepository), get(), get()) }
    single<FollowingRepository> { FollowingRepositoryDefault(get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<HttpClient> { Factory.networkContentHttpClient(this) }
    single<Lazy<ContentRepository>>(NamedScope.LazyContentRepository) { getLazy() }
    single<Lazy<WallpaperSaverManager>>(NamedScope.LazyWallpaperSaverManager) { getLazy() }
    single<MessageBarManager> { get<MessageBarManagerDefault>() }
    single<MessageBarManagerDefault> { MessageBarManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ModelRepository> { Factory.modelRepository(this) }
    single<ModelRepositoryRaw> { ModelRepositoryRaw(get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<NetworkContentMapper> { NetworkContentMapper() }
    single<NetworkContentRepository> { Factory.networkContentRepository(this) }
    single<NetworkContentRepositoryKtor> { NetworkContentRepositoryKtor(get(), get()) }
    single<NetworkContentRepositoryUrlDownloader> { NetworkContentRepositoryUrlDownloader(get(), get(), get()) }
    single<NetworkMediaCacheManager> { NetworkMediaCacheManagerDefault(get(NamedScope.CacheFileMediaMap)) }
    single<NetworkMediaMapRepository> { Factory.networkMediaMapRepository(this) }
    single<NetworkMediaMapRepositoryConfig> { NetworkMediaMapRepositoryConfigDefault(get()) }
    single<NetworkMediaMapRepositoryNetwork> { NetworkMediaMapRepositoryNetwork(get(), get(), get()) }
    single<NetworkStateManager> { NetworkStateManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<PurchaseRecordRepository> { PurchaseRecordRepositoryDefault(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<PurchaseUiManager> { PurchaseUiManagerDefault(get(), get(NamedScope.CoroutineScopeMain)) }
    single<RemoteApiEndpointRepository> { Factory.remoteApiEndpointRepository(this) }
    single<RemoteApiEndpointRepositoryDefault> { RemoteApiEndpointRepositoryDefault(get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<RemoteApiEndpointRepositoryConfig> { RemoteApiEndpointRepositoryConfigDefault(get()) }
    single<RemoteEndpointsRepositoryNetwork> { RemoteEndpointsRepositoryNetworkDefault(get(), get(), get()) }
    single<RemoteEndpointsRepositoryPreset> { RemoteEndpointsRepositoryPresetDefault() }
    single<RemoteEndpointsSpecRepository> { Factory.remoteEndpointsSpecRepository(this) }
    single<RemoteEndpointsSpecRepositoryDefault> { RemoteEndpointsSpecRepositoryDefault() }
    single<ShowcaseRepository> { ShowcaseRepositoryDefault(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<ShowcaseRepositoryHighlightsConfig> { ShowcaseRepositoryHighlightsConfigDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<WallpaperDownloadEventManager> { WallpaperDownloadEventManagerDefault(get(), get()) }
    single<WallpaperDownloadManager> { WallpaperDownloadManagerDefault(get(), get(), get(), get(), get(), get(NamedScope.LazyWallpaperSaverManager), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<WallpaperImageCache> { Factory.wallpaperImageCache(this) }
    single<WallpaperRepository> { WallpaperRepositoryDefault(get(), get(NamedScope.CoroutineScopeIo)) }
    single<WallpaperStateRepository> { WallpaperStateRepositoryDefault(get(), get()) }
    single<WallpaperSystemPhotoStatusCache> { get<WallpaperSystemPhotoStatusCacheDefault>() }
    single<WallpaperSystemPhotoStatusCacheDefault> { WallpaperSystemPhotoStatusCacheDefault(get(), get(NamedScope.CoroutineScopeIo)) }
    single<WallpaperSystemPhotoStatusCacheDefaultData> { WallpaperSystemPhotoStatusCacheDefaultDataDefault(get()) }
    single<WallpaperSystemPhotoStatusCacheMemory> { WallpaperSystemPhotoStatusCacheMemory() }
    single<WallpaperSystemPhotoStatusManager> { WallpaperSystemPhotoStatusManagerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<WallpaperSystemPhotoStatusRepository> { WallpaperSystemPhotoStatusRepositoryDefault(get(), get(), get(NamedScope.CoroutineScopeIo)) }
}
