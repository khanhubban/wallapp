package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import wallapp.ad.reward.RewardAdWatchManager
import wallapp.ads.reward.RewardAdManager
import wallapp.ads.reward.RewardAdState
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.DataHandle
import wallapp.data.content.ContentRepository
import wallapp.data.entitlement.isUnlockedAny
import wallapp.data.entitlement.isUnlockedHd
import wallapp.data.entitlement.isUnlockedSd
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.entitlement.EntitlementRepository
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
import wallapp.wallpaper.static.staticWallpaperSize
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager


class WallpaperActionButtonManagerDefault(
    private val contentRepository: ContentRepository,
    private val appStateManager: AppStateManager,
    private val entitlementRepository: EntitlementRepository,
    private val wallpaperDownloadManager: WallpaperDownloadManager,
    private val wallpaperImageCache: WallpaperImageCache,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val currentWallpaperManager: CurrentWallpaperManager,
    private val rewardAdWatchManager: RewardAdWatchManager,
    private val staticWallpaperManager: StaticWallpaperManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemPhotoViewer: SystemPhotoViewer,
    private val wallpaperActionButtonMapper: WallpaperActionButtonMapper,
    private val rewardAdManager: RewardAdManager,
    private val networkState: NetworkState,
    private val appConfig: AppConfig,
) : WallpaperActionButtonManager {

    private val canOpenToSystemPhotosApp: Boolean
        get() = PlatformFeature.CanOpenToSystemPhotosApp

    override fun getWallpaperActionWithDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean,
        staticWallpaperSize: StaticWallpaperSize,
        onHeroAction: (() -> Unit)?,
        showRewardAd: (() -> Unit)?
    ): Flow<WallpaperActionButtonState> {
        return combine(
            entitlementRepository.getEntitlementState(wallpaperId),
            rewardAdWatchManager.getRemainingAdWatchCount(wallpaperId),
            contentRepository.getWallpaperContent(wallpaperId),
            wallpaperDownloadManager.getWallpaperDownloadState(wallpaperId, staticWallpaperSize),
            currentWallpaperManager.currentHomeScreenWallpaperInfo,
            currentWallpaperManager.currentLockScreenWallpaperInfo,
            systemPermissionManager.systemMediaPermissionStatus,
            wallpaperSystemPhotoStatusManager.getSystemPhotoStatusFlow(wallpaperId),
            rewardAdManager.rewardAdState,
            staticWallpaperManager.currentSettingWallpaper,
        ) {
                entitlementState,
                remainingAds,
                wallpaperItem,
                wallpaperDownloadState,
                currentHomeScreenSystemWallpaperInfo,
                currentLockScreenSystemWallpaperInfo,
                mediaPermissionStatus,
                systemPhotoStatus,
                rewardAdState,
                currentSettingWallpaper,
            ->

            if (currentSettingWallpaper.contains(RemixIdSizeKey(wallpaperId, staticWallpaperSize))) {
                return@combine wallpaperActionButtonMapper.mapApplyingButtonState(wallpaperId)
            }

            val isWallpaperCached =
                if (entitlementState.isUnlockedAny) {
                    wallpaperImageCache.isCached(wallpaperId, staticWallpaperSize)
                } else {
                    false
                }

            val showDownloadedToPhotos =
                appConfig.alwaysShowDownloadedToPhotos || staticWallpaperSize == StaticWallpaperSize.FullResolution

            val mapGetButtonState = {
                wallpaperActionButtonMapper.mapGetButtonState(
                    wallpaperId,
                    wallpaperItem.wallpaper,
                    staticWallpaperSize,
                    ::downloadWallpaper,
                )
            }

            val mapSetWallpaper: (DataHandle?) -> WallpaperActionButtonState = { dataHandle ->
                if (showDownloadedToPhotos) {
                    if (systemPhotoStatus is SystemPhotoStatus.ExistsInPhotoLibrary) {
                        if (canOpenToSystemPhotosApp) {
                            wallpaperActionButtonMapper.mapOpenInPhotosButtonState(
                                wallpaperId,
                                staticWallpaperSize,
                                systemPhotoStatus.systemPhotoId,
                                onOpenInPhotosClick = ::openInPhotos,
                            )
                        } else {
                            wallpaperActionButtonMapper
                                .mapDownloadedToPhotosState(wallpaperId, staticWallpaperSize)
                        }
                    } else {
                        mapGetButtonState()
                    }
                } else if (dataHandle == null) {
                    wallpaperActionButtonMapper.mapSetWallpaperButtonState(
                        wallpaperId,
                        wallpaperItem.wallpaper,
                        staticWallpaperSize,
                        onSetClick = ::setWallpaper
                    )
                } else {
                    wallpaperActionButtonMapper.mapSetWallpaperButtonStateWithWallpaperDownloadState(
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

            Log.d("getWallpaperActionWithDownloadButtonState OutSide wallpaperDownloadState: $wallpaperDownloadState, staticWallpaperSize: $staticWallpaperSize")

            when {
                currentHomeScreenSystemWallpaperInfo != null
                        && currentHomeScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentHomeScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                currentLockScreenSystemWallpaperInfo != null
                        && currentLockScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentLockScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                isWallpaperCached -> {
                    if (hasMediaPermission(mediaPermissionStatus)) {
                        Log.d("getWallpaperActionWithDownloadButtonState isWallpaperCached wallpaperDownloadState: $wallpaperDownloadState, staticWallpaperSize: $staticWallpaperSize")
                        mapSetWallpaper(null)
                    } else {
                        Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                        wallpaperActionButtonMapper.mapPermissionDeniedState(wallpaperId, staticWallpaperSize)
                    }
                }

                entitlementState.isUnlockedSd
                        && staticWallpaperSize == StaticWallpaperSize.StandardResolution -> {
                    Log.d("getWallpaperActionWithDownloadButtonState isUnlockedSd wallpaperDownloadState: $wallpaperDownloadState, staticWallpaperSize: $staticWallpaperSize")

                    if (wallpaperDownloadState.staticWallpaperSize == staticWallpaperSize) {
                        when (wallpaperDownloadState) {
                            is WallpaperDownloadState.None, is WallpaperDownloadState.Cancelled -> {
                                mapGetButtonState()
                            }

                            is WallpaperDownloadState.Queued,
                            is WallpaperDownloadState.DownloadStarting,
                            is WallpaperDownloadState.Downloading -> {
                                wallpaperActionButtonMapper.mapDownloadingButtonState(
                                    wallpaperId,
                                    wallpaperDownloadState,
                                    entitlementState.staticWallpaperSize,
                                    wallpaperItem.wallpaper,
                                    ::cancelWallpaperDownload,
                                )
                            }

                            is WallpaperDownloadState.Success -> {
                                mapSetWallpaper(wallpaperDownloadState.dataHandle)
                            }

                            is WallpaperDownloadState.Error -> {
                                wallpaperActionButtonMapper.mapErrorButtonState(
                                    wallpaperId,
                                    wallpaperItem.wallpaper
                                ) {
                                    wallpaperItem.wallpaper?.let {
                                        downloadWallpaper(it, staticWallpaperSize)
                                    }
                                }
                            }
                        }
                    } else {
                        mapGetButtonState()
                    }
                }

                entitlementState.isUnlockedHd
                        && staticWallpaperSize == StaticWallpaperSize.FullResolution -> {
                    Log.d("getWallpaperActionWithDownloadButtonState isUnlockedHd wallpaperDownloadState: $wallpaperDownloadState, staticWallpaperSize: $staticWallpaperSize")

                    if (wallpaperDownloadState.staticWallpaperSize == staticWallpaperSize) {
                        when (wallpaperDownloadState) {
                            is WallpaperDownloadState.None, is WallpaperDownloadState.Cancelled -> {
                                mapGetButtonState()
                            }

                            is WallpaperDownloadState.Queued,
                            is WallpaperDownloadState.DownloadStarting,
                            is WallpaperDownloadState.Downloading -> {
                                wallpaperActionButtonMapper.mapDownloadingButtonState(
                                    wallpaperId,
                                    wallpaperDownloadState,
                                    entitlementState.staticWallpaperSize,
                                    wallpaperItem.wallpaper,
                                    ::cancelWallpaperDownload,
                                )
                            }

                            is WallpaperDownloadState.Success -> {
                                mapSetWallpaper(wallpaperDownloadState.dataHandle)
                            }

                            is WallpaperDownloadState.Error -> {
                                wallpaperActionButtonMapper.mapErrorButtonState(
                                    wallpaperId,
                                    wallpaperItem.wallpaper
                                ) {
                                    wallpaperItem.wallpaper?.let {
                                        downloadWallpaper(it, staticWallpaperSize)
                                    }
                                }
                            }
                        }
                    } else {
                        mapGetButtonState()
                    }
                }

                canShowPlusButton -> {
                    wallpaperActionButtonMapper.mapPlusButtonState(wallpaperId)
                }

                canShowRewardAdLoadingButton && rewardAdState == RewardAdState.Loading -> {
                    wallpaperActionButtonMapper.mapRewardAdLoadingButtonState(wallpaperId)
                }

                else -> {
                    if (staticWallpaperSize != StaticWallpaperSize.FullResolution) {
                        Log.w("Static wallpaper size must be FullResolution if the wallpaper is not unlocked.")
                    }
                    require(remainingAds != 0) { "Remaining ads should not be 0 if the wallpaper is not unlocked" }
                    require(showRewardAd != null) { "showRewardAd should not be null for displaying ads." }
                    wallpaperActionButtonMapper.mapWatchRewardAdButtonState(
                        wallpaperId = wallpaperId,
                        remainingAds = remainingAds,
                        showRewardAd = showRewardAd,
                    )
                }
            }

        }
    }

    override fun getWallpaperActionWithoutDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean,
        staticWallpaperSize: StaticWallpaperSize,
        onHeroAction: (() -> Unit)?,
        showRewardAd: (() -> Unit)?
    ): Flow<WallpaperActionButtonState> {
        return combine(
            entitlementRepository.getEntitlementState(wallpaperId),
            rewardAdWatchManager.getRemainingAdWatchCount(wallpaperId),
            contentRepository.getWallpaperContent(wallpaperId),
            currentWallpaperManager.currentHomeScreenWallpaperInfo,
            currentWallpaperManager.currentLockScreenWallpaperInfo,
            systemPermissionManager.systemMediaPermissionStatus,
            wallpaperSystemPhotoStatusManager.getSystemPhotoStatusFlow(wallpaperId),
            rewardAdManager.rewardAdState,
            staticWallpaperManager.currentSettingWallpaper,
        ) {
                entitlementState,
                remainingAds,
                wallpaperItem,
                currentHomeScreenSystemWallpaperInfo,
                currentLockScreenSystemWallpaperInfo,
                mediaPermissionStatus,
                systemPhotoStatus,
                rewardAdState,
                currentSettingWallpaper,
            ->

            if (currentSettingWallpaper.contains(RemixIdSizeKey(wallpaperId, staticWallpaperSize))) {
                return@combine wallpaperActionButtonMapper.mapApplyingButtonState(wallpaperId)
            }

            val isWallpaperCached =
                if (entitlementState.isUnlockedAny) {
                    wallpaperImageCache.isCached(wallpaperId, staticWallpaperSize)
                } else {
                    false
                }

            val mapGetButtonState = {
                wallpaperActionButtonMapper.mapGetButtonState(
                    wallpaperId,
                    wallpaperItem.wallpaper,
                    staticWallpaperSize,
                    ::downloadWallpaper,
                )
            }

            when {
                currentHomeScreenSystemWallpaperInfo != null
                        && currentHomeScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentHomeScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                currentLockScreenSystemWallpaperInfo != null
                        && currentLockScreenSystemWallpaperInfo.wallpaperId == wallpaperId
                        && currentLockScreenSystemWallpaperInfo.staticWallpaperSize == staticWallpaperSize -> {
                    wallpaperActionButtonMapper.mapCurrentButtonState(wallpaperId)
                }

                isWallpaperCached -> {
                    if (hasMediaPermission(mediaPermissionStatus)) {
                        wallpaperActionButtonMapper.mapSetWallpaperButtonState(
                            wallpaperId,
                            wallpaperItem.wallpaper,
                            staticWallpaperSize,
                            onSetClick = ::setWallpaper,
                        )
                    } else {
                        Log.w("Media permission denied - wallpaperId: $wallpaperId, mediaPermissionStatus: $mediaPermissionStatus")
                        wallpaperActionButtonMapper.mapPermissionDeniedState(wallpaperId, staticWallpaperSize)
                    }
                }

                entitlementState.isUnlockedSd
                        && staticWallpaperSize == StaticWallpaperSize.StandardResolution -> {
                    mapGetButtonState()
                }

                entitlementState.isUnlockedHd
                        && staticWallpaperSize == StaticWallpaperSize.FullResolution -> {
                    mapGetButtonState()
                }

                canShowPlusButton -> {
                    wallpaperActionButtonMapper.mapPlusButtonState(wallpaperId)
                }

                canShowRewardAdLoadingButton && rewardAdState == RewardAdState.Loading -> {
                    wallpaperActionButtonMapper.mapRewardAdLoadingButtonState(wallpaperId)
                }

                else -> {
                    if (staticWallpaperSize != StaticWallpaperSize.FullResolution) {
                        Log.w("Static wallpaper size must be FullResolution if the wallpaper is not unlocked.")
                    }
                    require(remainingAds != 0) { "Remaining ads should not be 0 if the wallpaper is not unlocked" }
                    require(showRewardAd != null) { "showRewardAd should not be null for displaying ads." }
                    wallpaperActionButtonMapper.mapWatchRewardAdButtonState(
                        wallpaperId = wallpaperId,
                        remainingAds = remainingAds,
                        showRewardAd = showRewardAd,
                    )
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

    override fun downloadWallpaper(
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

    private fun cancelWallpaperDownload(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
    ) {
        wallpaperDownloadManager.cancelWallpaperDownload(
            wallpaper = wallpaper,
            staticWallpaperSize = staticWallpaperSize,
        )
    }

    private fun openInPhotos(systemPhotoId: SystemPhotoId) =
        systemPhotoViewer.openPhotoViewer(systemPhotoId)
}