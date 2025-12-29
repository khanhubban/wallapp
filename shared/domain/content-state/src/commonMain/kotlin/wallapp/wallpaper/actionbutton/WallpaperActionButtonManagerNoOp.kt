package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.pixel.menu.MenuItem

object WallpaperActionButtonManagerNoOp : WallpaperActionButtonManager {
    override fun getWallpaperActionWithDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean,
        staticWallpaperSize: StaticWallpaperSize,
        onHeroAction: (() -> Unit)?,
        showRewardAd: (() -> Unit)?
    ): Flow<WallpaperActionButtonState> =
        flowOf(WallpaperActionButtonState.None(wallpaperId, buttonViewState = MenuItem.MenuItemDivider))

    override fun getWallpaperActionWithoutDownloadButtonState(
        wallpaperId: RemixId,
        canShowPlusButton: Boolean,
        canShowRewardAdLoadingButton: Boolean,
        staticWallpaperSize: StaticWallpaperSize,
        onHeroAction: (() -> Unit)?,
        showRewardAd: (() -> Unit)?
    ): Flow<WallpaperActionButtonState> =
        flowOf(WallpaperActionButtonState.None(wallpaperId, buttonViewState = MenuItem.MenuItemDivider))

    override fun downloadWallpaper(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
    ) { }
}
