package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id
import wallapp.system.photo.status.SystemPhotoStatus

/**
 * Should not be accessed directly. Use [WallpaperSystemPhotoStatusManager] instead.
 */
interface WallpaperSystemPhotoStatusRepository {

    suspend fun getSystemPhotoStatus(id: Id): SystemPhotoStatus
    fun getSystemPhotoStatusFlow(id: Id): Flow<SystemPhotoStatus>

    fun setSystemPhotoStatus(id: Id, status: SystemPhotoStatus)
}
