package wallapp.worker

import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize

interface BackgroundWorkScheduler {
    fun scheduleWallpaperDownloads(
        wallpapers: List<WallpaperRemix>,
        staticWallpaperSize: StaticWallpaperSize,
        saveToGallery: Boolean,
    )
}