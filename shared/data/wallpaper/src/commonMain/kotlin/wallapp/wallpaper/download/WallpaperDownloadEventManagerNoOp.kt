package wallapp.wallpaper.download

import wallapp.content.model.Id

object WallpaperDownloadEventManagerNoOp : WallpaperDownloadEventManager {

    override suspend fun registerWallpaperDownloadEvent(id: Id.RemixId) { }
}