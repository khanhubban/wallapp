package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.DataHandle
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentRepository
import wallapp.data.entitlement.isUnlockedCollection
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.entitlement.EntitlementRepository
import wallapp.log.Log
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkState
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.permission.SystemPermissionType
import wallapp.system.photo.SystemPhotoId
import wallapp.system.photo.status.SystemPhotoStatus
import wallapp.system.photo.viewer.SystemPhotoViewer
import wallapp.system.platform.PlatformFeature
import wallapp.util.combine
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.model.RemixIdSizeKey
import wallapp.wallpaper.static.StaticWallpaperManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager

class CollectionActionButtonManagerDefault(
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val appStateManager: AppStateManager,
    private val contentRepository: ContentRepository,
    private val entitlementRepository: EntitlementRepository,
    private val wallpaperImageCache: WallpaperImageCache,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val currentWallpaperManager: CurrentWallpaperManager,
    private val staticWallpaperManager: StaticWallpaperManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemPhotoViewer: SystemPhotoViewer,
    private val collectionActionButtonMapper: CollectionActionButtonMapper,
    private val networkState: NetworkState,
    private val appConfig: AppConfig,
) : CollectionActionButtonManager {

    private val canOpenToSystemPhotosApp: Boolean
        get() = PlatformFeature.CanOpenToSystemPhotosApp

    override fun getCollectionActionButtonState(
        wallpaperId: WallpaperId?,
        collectionState: StateFlow<CollectionState?>,
        staticWallpaperSize: StaticWallpaperSize
    ): Flow<CollectionActionButtonState> {

        wallpaperId ?: return flowOf(collectionActionButtonMapper.mapNoneButtonState())

        return combine(
            collectionState,
            entitlementRepository.getEntitlementState(wallpaperId),
            contentRepository.getWallpaperContent(wallpaperId),
            currentWallpaperManager.currentHomeScreenWallpaperInfo,
            currentWallpaperManager.currentLockScreenWallpaperInfo,
            systemPermissionManager.systemMediaPermissionStatus,
            wallpaperSystemPhotoStatusManager.getSystemPhotoStatusFlow(wallpaperId),
            staticWallpaperManager.currentSettingWallpaper,
            wallpaperDownloadManager.getWallpaperDownloadState(wallpaperId, staticWallpaperSize),
        ) {
                collection,
                entitlementState,
                wallpaperItem,
                currentHomeScreenSystemWallpaperInfo,
                currentLockScreenSystemWallpaperInfo,
                mediaPermissionStatus,
                systemPhotoStatus,
                currentSettingWallpaper,
                wallpaperDownloadState,
            ->

            if (currentSettingWallpaper.contains(RemixIdSizeKey(wallpaperId, staticWallpaperSize))) {
                return@combine collectionActionButtonMapper.mapApplyingButtonState(wallpaperId)
            }

            val isWallpaperCached =
                if (entitlementState.isUnlockedCollection) {
                    wallpaperImageCache.isCached(wallpaperId, staticWallpaperSize)
                } else {
                    false
                }

            val showDownloadedToPhotos =
                appConfig.alwaysShowDownloadedToPhotos || staticWallpaperSize == StaticWallpaperSize.FullResolution

            val isUnlocked = collection?.connectionState?.isUnlocked

            val mapDownloadSelectedState = {
                collectionActionButtonMapper.mapDownloadSelectedState(
                    wallpaperId,
                    wallpaperItem.wallpaper,
                    staticWallpaperSize,
                    ::downloadWallpaper,
                )
            }

            val mapSetWallpaper: (DataHandle?) -> CollectionActionButtonState = { dataHandle ->
                if (showDownloadedToPhotos) {
                    if (systemPhotoStatus is SystemPhotoStatus.ExistsInPhotoLibrary) {
                        if (canOpenToSystemPhotosApp) {
                            collectionActionButtonMapper.mapOpenInPhotosButtonState(
                                wallpaperId,
                                staticWallpaperSize,
                                systemPhotoStatus.systemPhotoId,
                                onOpenInPhotosClick = ::openInPhotos,
                            )
                        } else {
                            collectionActionButtonMapper
                                .mapDownloadedToPhotosState(wallpaperId)
                        }
                    } else {
                        mapDownloadSelectedState()
                    }
                } else if (dataHandle == null) {
                    collectionActionButtonMapper.mapSetWallpaperButtonState(
                        wallpaperId,
                        wallpaperItem.wallpaper,
                        staticWallpaperSize,
                        onSetClick = ::setWallpaper
                    )
                } else {
                    collectionActionButtonMapper.mapSetWallpaperButtonStateWithWallpaperDownloadState(
                        wallpaperId,
                        wallpaperItem.wallpaper,
                        WallpaperDownloadState.Success(
                            wallpaperId,
                            dataHandle,
                            staticWallpaperSize,
                        ),
                        staticWallpaperSize,
                        onSetClick = ::setWallpaperWithDownloadState,
                    )
                }
            }

            when {
                isUnlocked == false -> {
                    collectionActionButtonMapper.mapNoneButtonState()
                }

                currentHomeScreenSystemWallpaperInfo != null
                        && currentHomeScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentHomeScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    collectionActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                currentLockScreenSystemWallpaperInfo != null
                        && currentLockScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentLockScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    collectionActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                isWallpaperCached -> {
                    if (hasMediaPermission(mediaPermissionStatus)) {
                        mapSetWallpaper(null)
                    } else {
                        Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                        collectionActionButtonMapper.mapPermissionDeniedState(wallpaperId)
                    }
                }

                isUnlocked == true -> {
                    when (wallpaperDownloadState) {
                        is WallpaperDownloadState.None, is WallpaperDownloadState.Cancelled -> {
                            mapDownloadSelectedState()
                        }

                        is WallpaperDownloadState.Queued,
                        is WallpaperDownloadState.DownloadStarting,
                        is WallpaperDownloadState.Downloading -> {
                            collectionActionButtonMapper.mapDownloadingButtonState(
                                wallpaperId,
                                wallpaperDownloadState,
                            )
                        }

                        is WallpaperDownloadState.Success -> {
                            mapSetWallpaper(wallpaperDownloadState.dataHandle)
                        }

                        is WallpaperDownloadState.Error -> {
                            collectionActionButtonMapper.mapErrorButtonState(
                                wallpaperId,
                                wallpaperItem.wallpaper
                            ) {
                                wallpaperItem.wallpaper?.let {
                                    downloadWallpaper(it, staticWallpaperSize)
                                }
                            }
                        }
                    }
                }

                else -> {
                    mapDownloadSelectedState()
                }
            }
        }
    }

    private fun hasMediaPermission(mediaPermissionStatus: SystemPermissionStatus): Boolean =
        mediaPermissionStatus is SystemPermissionStatus.Authorized
                || mediaPermissionStatus is SystemPermissionStatus.Limited

    private fun setWallpaper(
        wallpaperRemix: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
    ) = staticWallpaperManager.setWallpaperWithRemixAndSize(wallpaperRemix, staticWallpaperSize)

    private fun setWallpaperWithDownloadState(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
    ) = staticWallpaperManager.setWallpaper(wallpaperDownloadState, wallpaperRemix)

    private fun downloadWallpaper(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
    ) {
        Log.i("downloadWallpaper()")
        systemPermissionManager.permissionGuardedAction(
            SystemPermissionType.SystemMedia,
            actionOnSuccess = {
                if (networkState.networkConnectionState.value == NetworkConnectionState.Disconnected || networkState.networkConnectionState.value == NetworkConnectionState.ConnectionNoInternet) {
                    appStateManager.navigateToError(ErrorScreen.Network())
                    return@permissionGuardedAction
                }

                wallpaperDownloadManager.downloadStaticWallpaper(
                    wallpaper = wallpaper,
                    staticWallpaperSize = staticWallpaperSize,
                ).collect {
                    if (it is WallpaperDownloadState.Error) {
                        appStateManager.navigateToError(ErrorScreen.DownloadFailed(it.message))
                    }
                }
            },
            actionOnDenied = {
                appStateManager.navigateToError(permissionSystemMediaDenied())
            }
        )
    }

    private fun openInPhotos(systemPhotoId: SystemPhotoId) =
        systemPhotoViewer.openPhotoViewer(systemPhotoId)
}