package wallapp.system.photo.status

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.system.photo.SystemPhotoId

object SystemPhotoStatusCheckerNoOp : SystemPhotoStatusChecker {

    override fun getSystemPhotoStatus(systemPhotoId: SystemPhotoId): Flow<SystemPhotoStatus> =
        flowOf(SystemPhotoStatus.NotAuthorizedToCheck)

    override suspend fun getSystemPhotoStatusSuspend(systemPhotoId: SystemPhotoId): SystemPhotoStatus =
        SystemPhotoStatus.NotAuthorizedToCheck
}