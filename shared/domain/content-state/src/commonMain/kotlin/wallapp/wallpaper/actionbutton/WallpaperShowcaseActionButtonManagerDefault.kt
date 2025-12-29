package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import wallapp.appconfig.AppConfig
import wallapp.content.model.WallpaperId
import wallapp.data.content.ContentRepository
import wallapp.data.entitlement.isUnlockedAny
import wallapp.data.entitlement.isUnlockedHd
import wallapp.data.entitlement.isUnlockedSd
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.entitlement.EntitlementRepository
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.system.photo.status.existsInPhotoLibrary
import wallapp.util.combine
import wallapp.wallpaper.cache.WallpaperCacheStatus
import wallapp.wallpaper.cache.WallpaperImageCache
import wallapp.wallpaper.current.CurrentWallpaperManager
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadManager
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.static.StaticWallpaperManager
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager

class WallpaperShowcaseActionButtonManagerDefault(
    private val activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val contentRepository: ContentRepository,
    private val entitlementRepository: EntitlementRepository,
    private val wallpaperImageCache: WallpaperImageCache,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val currentWallpaperManager: CurrentWallpaperManager,
    private val staticWallpaperManager: StaticWallpaperManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val wallpaperActionButtonMapper: WallpaperShowcaseActionButtonMapper,
    private val appConfig: AppConfig,
) : WallpaperShowcaseActionButtonManager {

    private val canShowDownloadedToPhotos: Boolean
        get() = appConfig.alwaysShowDownloadedToPhotos

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWallpaperShowcaseActionButtonState(
        wallpaperId: WallpaperId,
        staticWallpaperSize: StaticWallpaperSize,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState> {
        val entitlementStateFlow = entitlementRepository.getEntitlementState(wallpaperId)
        val staticWallpaperCacheStatusFlow = entitlementStateFlow.flatMapConcat { state ->
            if (state.isUnlockedAny) {
                wallpaperImageCache.getStaticWallpaperCacheStatus(wallpaperId, staticWallpaperSize)
            } else {
                flow { emit(WallpaperCacheStatus.NotCached) }
            }
        }
        return combine(
            entitlementRepository.getEntitlementState(wallpaperId),
            contentRepository.getWallpaperContent(wallpaperId),
            wallpaperDownloadManager.getWallpaperDownloadState(wallpaperId, staticWallpaperSize),
            activeWallpaperDownloadManager.activeDownloadStatus,
            currentWallpaperManager.currentHomeScreenWallpaperInfo,
            currentWallpaperManager.currentLockScreenWallpaperInfo,
            systemPermissionManager.systemMediaPermissionStatus,
            staticWallpaperCacheStatusFlow,
            wallpaperSystemPhotoStatusManager.getSystemPhotoStatusFlow(wallpaperId),
        ) {
                entitlementState,
                wallpaperItem,
                wallpaperDownloadState,
                _,
                currentHomeScreenSystemWallpaperInfo,
                currentLockScreenSystemWallpaperInfo,
                mediaPermissionStatus,
                staticWallpaperCacheStatus,
                systemPhotoStatus,
            ->

            val isWallpaperCached =
                (entitlementState.isUnlockedAny) &&
                        wallpaperImageCache.isCached(wallpaperId, staticWallpaperSize)

            val mapGetButtonState = {
                wallpaperActionButtonMapper.mapGetButtonState(
                    wallpaperId,
                    wallpaperItem.wallpaper,
                    appearAsPhotosButton = canShowDownloadedToPhotos && systemPhotoStatus.existsInPhotoLibrary,
                    navigateToBottomSheet,
                )
            }

            when {
                currentHomeScreenSystemWallpaperInfo?.wallpaperId == wallpaperId -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(
                        wallpaperId,
                        navigateToBottomSheet,
                    )
                }

                currentLockScreenSystemWallpaperInfo?.wallpaperId == wallpaperId -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(
                        wallpaperId,
                        navigateToBottomSheet,
                    )
                }

                isWallpaperCached && wallpaperDownloadState !is WallpaperDownloadState.Success -> {
                    if (hasMediaPermission(mediaPermissionStatus)) {
                        when (staticWallpaperCacheStatus) {
                            is WallpaperCacheStatus.Loading -> {
                                mapGetButtonState()
                            }

                            is WallpaperCacheStatus.NotCached, is WallpaperCacheStatus.Cached -> {
                                mapGetButtonState()
                            }
                        }
                    } else {
                        Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                        wallpaperActionButtonMapper.mapPermissionDeniedState(wallpaperId, staticWallpaperSize)
                    }
                }

                entitlementState.isUnlockedAny -> {
                    when (wallpaperDownloadState) {
                        is WallpaperDownloadState.None, is WallpaperDownloadState.Error, is WallpaperDownloadState.Cancelled -> {
                            mapGetButtonState()
                        }

                        is WallpaperDownloadState.Queued, is WallpaperDownloadState.DownloadStarting, is WallpaperDownloadState.Downloading -> {
                            wallpaperActionButtonMapper.mapDownloadingButtonState(
                                wallpaperId,
                                wallpaperDownloadState,
                            )
                        }

                        is WallpaperDownloadState.Success -> {
                            if (hasMediaPermission(mediaPermissionStatus)) {
                                mapGetButtonState()
                            } else {
                                Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                                wallpaperActionButtonMapper.mapPermissionDeniedState(wallpaperId, staticWallpaperSize)
                            }
                        }
                    }
                }

                else -> {
                    mapGetButtonState()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWallpaperShowcaseActionButtonState(
        wallpaperId: WallpaperId,
        staticWallpaperSizeStandardResolution: StaticWallpaperSize,
        staticWallpaperSizeFullResolution: StaticWallpaperSize,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState> {
        val entitlementStateFlow = entitlementRepository.getEntitlementState(wallpaperId)

        val staticWallpaperStandardResolutionCacheStatusFlow = entitlementStateFlow.flatMapConcat { state ->
            if (state.isUnlockedAny) {
                wallpaperImageCache.getStaticWallpaperCacheStatus(
                    wallpaperId,
                    staticWallpaperSizeStandardResolution,
                )
            } else {
                flow { emit(WallpaperCacheStatus.NotCached) }
            }
        }

        val staticWallpaperFullResolutionCacheStatusFlow = entitlementStateFlow.flatMapConcat { state ->
            if (state.isUnlockedAny) {
                wallpaperImageCache.getStaticWallpaperCacheStatus(
                    wallpaperId,
                    staticWallpaperSizeFullResolution,
                )
            } else {
                flow { emit(WallpaperCacheStatus.NotCached) }
            }
        }

        return combine(
            entitlementRepository.getEntitlementState(wallpaperId),
            contentRepository.getWallpaperContent(wallpaperId),
            wallpaperDownloadManager.getWallpaperDownloadState(wallpaperId),
            activeWallpaperDownloadManager.activeDownloadStatus,
            currentWallpaperManager.currentHomeScreenWallpaperInfo,
            currentWallpaperManager.currentLockScreenWallpaperInfo,
            systemPermissionManager.systemMediaPermissionStatus,
            staticWallpaperStandardResolutionCacheStatusFlow,
            staticWallpaperFullResolutionCacheStatusFlow,
            wallpaperSystemPhotoStatusManager.getSystemPhotoStatusFlow(wallpaperId),
        ) {
                entitlementState,
                wallpaperItem,
                wallpaperDownloadState,
                activeDownloadStatus,
                currentHomeScreenSystemWallpaperInfo,
                currentLockScreenSystemWallpaperInfo,
                mediaPermissionStatus,
                staticWallpaperStandardResolutionCacheStatus,
                staticWallpaperFullResolutionCacheStatus,
                systemPhotoStatus,
            ->

            val isWallpaperCachedStandardResolution =
                (entitlementState.isUnlockedSd) &&
                        wallpaperImageCache.isCached(wallpaperId, staticWallpaperSizeStandardResolution)

            val isWallpaperCachedFullResolution =
                (entitlementState.isUnlockedHd) &&
                        wallpaperImageCache.isCached(wallpaperId, staticWallpaperSizeFullResolution)

            val mapGetButtonState = {
                wallpaperActionButtonMapper.mapGetButtonState(
                    wallpaperId,
                    wallpaperItem.wallpaper,
                    appearAsPhotosButton = canShowDownloadedToPhotos && systemPhotoStatus.existsInPhotoLibrary,
                    navigateToBottomSheet,
                )
            }

            when {
                currentHomeScreenSystemWallpaperInfo?.wallpaperId == wallpaperId -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(
                        wallpaperId,
                        navigateToBottomSheet,
                    )
                }

                currentLockScreenSystemWallpaperInfo?.wallpaperId == wallpaperId -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(
                        wallpaperId,
                        navigateToBottomSheet,
                    )
                }

                activeDownloadStatus != null -> {
                    wallpaperActionButtonMapper.mapDownloadingButtonState(
                        wallpaperId,
                        wallpaperDownloadState,
                    )
                }

                (!isWallpaperCachedStandardResolution && !isWallpaperCachedFullResolution) && wallpaperDownloadState !is WallpaperDownloadState.Success -> {
                    mapGetButtonState()
                }

                (isWallpaperCachedStandardResolution && isWallpaperCachedFullResolution) && wallpaperDownloadState !is WallpaperDownloadState.Success -> {
                    if (hasMediaPermission(mediaPermissionStatus)) {
                        when (staticWallpaperFullResolutionCacheStatus) {
                            is WallpaperCacheStatus.Loading -> {
                                mapGetButtonState()
                            }

                            is WallpaperCacheStatus.NotCached, is WallpaperCacheStatus.Cached -> {
                                mapGetButtonState()
                            }
                        }
                    } else {
                        Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                        // The hardcoding of StaticWallpaperSize.StandardResolution needs to be
                        // looked at - ideally this step will never be reached as this button
                        // shouldn't display on the Showcase action button.
                        wallpaperActionButtonMapper.mapPermissionDeniedState(
                            wallpaperId = wallpaperId,
                            staticWallpaperSize = StaticWallpaperSize.StandardResolution,
                        )
                    }
                }

                (isWallpaperCachedStandardResolution || isWallpaperCachedFullResolution) && wallpaperDownloadState !is WallpaperDownloadState.Success -> {
                    mapGetButtonState()
                }

                entitlementState.isUnlockedAny -> {
                    when (wallpaperDownloadState) {
                        is WallpaperDownloadState.None, is WallpaperDownloadState.Error, is WallpaperDownloadState.Cancelled -> {
                            mapGetButtonState()
                        }

                        is WallpaperDownloadState.Queued, is WallpaperDownloadState.DownloadStarting, is WallpaperDownloadState.Downloading -> {
                            wallpaperActionButtonMapper.mapDownloadingButtonState(
                                wallpaperId,
                                wallpaperDownloadState,
                            )
                        }

                        is WallpaperDownloadState.Success -> {
                            mapGetButtonState()
                        }
                    }
                }

                else -> {
                    mapGetButtonState()
                }
            }
        }
    }

    private fun hasMediaPermission(mediaPermissionStatus: SystemPermissionStatus): Boolean =
        mediaPermissionStatus is SystemPermissionStatus.Authorized || mediaPermissionStatus is SystemPermissionStatus.Limited
}
