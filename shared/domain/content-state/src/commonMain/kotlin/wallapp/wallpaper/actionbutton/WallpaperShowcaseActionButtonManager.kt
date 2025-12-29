package wallapp.wallpaper.actionbutton

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.StaticWallpaperSize

interface WallpaperShowcaseActionButtonManager {

    fun getWallpaperShowcaseActionButtonState(
        wallpaperId: WallpaperId,
        staticWallpaperSize: StaticWallpaperSize = StaticWallpaperSize.StandardResolution,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState>

    fun getWallpaperShowcaseActionButtonState(
        wallpaperId: WallpaperId,
        staticWallpaperSizeStandardResolution: StaticWallpaperSize,
        staticWallpaperSizeFullResolution: StaticWallpaperSize,
        navigateToBottomSheet: (WallpaperId) -> Unit,
    ): Flow<WallpaperShowcaseActionButtonState>
}