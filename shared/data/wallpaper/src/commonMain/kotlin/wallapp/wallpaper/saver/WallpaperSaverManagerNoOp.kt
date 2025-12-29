package wallapp.wallpaper.saver

import wallapp.content.model.WallpaperRemix
import wallapp.wallpaper.download.WallpaperDownloadState

object WallpaperSaverManagerNoOp : WallpaperSaverManager {

    override suspend fun saveWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
        photoAlbumName: String
    ) = Unit
}