package wallapp.wallpaper.static

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.model.RemixIdSizeKey

object StaticWallpaperManagerNoOp : StaticWallpaperManager {

    override val canSetWallpaper: Boolean
        get() = false

    override val currentSettingWallpaper: StateFlow<Set<RemixIdSizeKey>> = MutableStateFlow(emptySet())

    override fun setWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
    ) { }

    override fun setWallpaperWithRemixAndSize(
        wallpaperRemix: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize
    ) { }
}