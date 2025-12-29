package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id
import wallapp.system.photo.status.SystemPhotoStatus

object WallpaperSystemPhotoStatusRepositoryNoOp : WallpaperSystemPhotoStatusRepository  {

    override suspend fun getSystemPhotoStatus(id: Id): SystemPhotoStatus {
        return SystemPhotoStatus.NotAuthorizedToCheck
    }

    override fun getSystemPhotoStatusFlow(id: Id): Flow<SystemPhotoStatus> =
        flowOf(SystemPhotoStatus.NotAuthorizedToCheck)

    override fun setSystemPhotoStatus(id: Id, status: SystemPhotoStatus) { }
}