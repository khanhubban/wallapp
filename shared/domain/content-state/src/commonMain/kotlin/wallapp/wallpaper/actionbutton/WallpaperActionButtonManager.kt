package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize

interface WallpaperActionButtonManager {

    fun getWallpaperActionWithDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean = false,
        staticWallpaperSize: StaticWallpaperSize = StaticWallpaperSize.StandardResolution,
        onHeroAction: (() -> Unit)? = null,
        showRewardAd: (() -> Unit)? = null,
    ): Flow<WallpaperActionButtonState>

    fun getWallpaperActionWithoutDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean = false,
        staticWallpaperSize: StaticWallpaperSize = StaticWallpaperSize.StandardResolution,
        onHeroAction: (() -> Unit)? = null,
        showRewardAd: (() -> Unit)? = null,
    ): Flow<WallpaperActionButtonState>

    fun downloadWallpaper(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize = StaticWallpaperSize.StandardResolution,
    )
}