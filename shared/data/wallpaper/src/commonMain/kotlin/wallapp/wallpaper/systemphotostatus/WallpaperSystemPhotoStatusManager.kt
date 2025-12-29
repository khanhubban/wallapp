package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id
import wallapp.system.photo.status.SystemPhotoStatus

interface WallpaperSystemPhotoStatusManager {

    suspend fun getSystemPhotoStatus(id: Id): SystemPhotoStatus
    fun getSystemPhotoStatusFlow(id: Id): Flow<SystemPhotoStatus>

    fun setSystemPhotoStatus(id: Id, status: SystemPhotoStatus)

    /**
     * If non-null, the system photo status for the given ID will be refreshed as the app
     * becomes visible. Used to handle the case where a user may manually delete items from
     * the system Photos.app.
     *
     * This is intended to be set only for screens that have UI that require the [SystemPhotoStatus]
     * be up-to-date, such as Wallpaper of UnlockWallpaper.
     */
    fun setPollSystemPhotoStatusForId(id: Id?)
}