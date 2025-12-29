package wallapp.worker

import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize

object BackgroundWorkSchedulerNoOp : BackgroundWorkScheduler {
    override fun scheduleWallpaperDownloads(
        wallpapers: List<WallpaperRemix>,
        staticWallpaperSize: StaticWallpaperSize,
        saveToGallery: Boolean,
    ) {
       /*NoOp*/
    }
}