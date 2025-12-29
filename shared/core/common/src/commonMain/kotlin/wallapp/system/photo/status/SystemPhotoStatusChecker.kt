package wallapp.system.photo.status

import kotlinx.coroutines.flow.Flow
import wallapp.system.photo.SystemPhotoId

interface SystemPhotoStatusChecker {

    fun getSystemPhotoStatus(systemPhotoId: SystemPhotoId): Flow<SystemPhotoStatus>

    suspend fun getSystemPhotoStatusSuspend(systemPhotoId: SystemPhotoId): SystemPhotoStatus
}
