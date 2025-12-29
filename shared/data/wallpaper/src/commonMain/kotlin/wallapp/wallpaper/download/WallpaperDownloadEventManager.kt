package wallapp.wallpaper.download

import wallapp.content.model.Id

interface WallpaperDownloadEventManager {
    suspend fun registerWallpaperDownloadEvent(id: Id.RemixId)
}