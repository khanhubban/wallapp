package wallapp.wallpaper.download

import wallapp.content.model.Id.RemixId
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.download.DownloadState

fun DownloadState.mapToWallpaperDownloadState(
    wallpaperId: RemixId,
    staticWallpaperSize: StaticWallpaperSize,
): WallpaperDownloadState {
    return when (val downloadState = this) {
        is DownloadState.Cancelled -> {
            WallpaperDownloadState.Cancelled(wallpaperId, staticWallpaperSize, downloadState.url)
        }
        is DownloadState.DownloadStarting -> {
            WallpaperDownloadState.DownloadStarting(wallpaperId, staticWallpaperSize)
        }
        is DownloadState.Downloading -> {
            WallpaperDownloadState.Downloading(wallpaperId, staticWallpaperSize, downloadState.progress)
        }
        is DownloadState.Error -> {
            WallpaperDownloadState.Error(wallpaperId, staticWallpaperSize, downloadState.url, downloadState.message)
        }
        is DownloadState.Queued -> {
            WallpaperDownloadState.Queued(wallpaperId, staticWallpaperSize)
        }
        is DownloadState.Success -> {
            WallpaperDownloadState.Success(
                id = wallpaperId,
                dataHandle = downloadState.dataHandle,
                staticWallpaperSize = staticWallpaperSize,
                logData = downloadState.url,
            )
        }
    }
}