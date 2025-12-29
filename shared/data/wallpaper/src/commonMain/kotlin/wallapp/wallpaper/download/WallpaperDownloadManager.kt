package wallapp.wallpaper.download

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.download.ActiveDownloadStatus

interface WallpaperDownloadManager {

    val activeDownloadsStatus: Flow<ActiveDownloadStatus>

    fun downloadStaticWallpaper(
        wallpaper: WallpaperRemix,
        staticWallpaperSize: StaticWallpaperSize,
        saveToSystemGallery: Boolean = true,
    ): Flow<WallpaperDownloadState>

    fun getWallpaperDownloadState(wallpaperId: RemixId, staticWallpaperSize: StaticWallpaperSize? = null): Flow<WallpaperDownloadState>

    fun cancelWallpaperDownload(wallpaper: WallpaperRemix, staticWallpaperSize: StaticWallpaperSize)

}
