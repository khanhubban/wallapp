package wallapp.worker

import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.wallpaper.download.WallpaperDownloadManager

/**
 * Temporary implementation for iOS until platform specific implementation is created
 */
class BackgroundWorkSchedulerIos(
    private val wallpaperDownloadManager: WallpaperDownloadManager
) : BackgroundWorkScheduler {

    override fun scheduleWallpaperDownloads(
        wallpapers: List<WallpaperRemix>,
        staticWallpaperSize: StaticWallpaperSize,
        saveToGallery: Boolean
    ) {
        wallpapers.forEach { wallpaper ->
            wallpaperDownloadManager.downloadStaticWallpaper(
                wallpaper = wallpaper,
                staticWallpaperSize = StaticWallpaperSize.FullResolution,
            )
        }
    }
}