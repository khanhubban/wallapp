package wallapp.wallpaper.static

import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.model.RemixIdSizeKey

interface StaticWallpaperManager {

    val canSetWallpaper: Boolean

    val currentSettingWallpaper: StateFlow<Set<RemixIdSizeKey>>

    fun setWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
    )

    fun setWallpaperWithRemixAndSize(
        wallpaperRemix: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
    )
}