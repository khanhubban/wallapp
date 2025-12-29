package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id
import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.pixel.menu.MenuItem

object WallpaperShowcaseActionButtonManagerNoOp : WallpaperShowcaseActionButtonManager {

    override fun getWallpaperShowcaseActionButtonState(
        wallpaperId: Id.RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState> =
        flowOf(WallpaperShowcaseActionButtonState.None(wallpaperId, buttonViewState = MenuItem.MenuItemDivider))

    override fun getWallpaperShowcaseActionButtonState(
        wallpaperId: Id.RemixId,
        staticWallpaperSizeStandardResolution: StaticWallpaperSize,
        staticWallpaperSizeFullResolution: StaticWallpaperSize,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState> =
        flowOf(WallpaperShowcaseActionButtonState.None(wallpaperId, buttonViewState = MenuItem.MenuItemDivider))

}