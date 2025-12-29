package wallapp.system.photo.status

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import wallapp.permission.SystemPermissionManager
import wallapp.permission.isAuthorizedOrLimited
import wallapp.system.photo.SystemPhotoId

class SystemPhotoStatusCheckerAndroid(
    private val context: Context,
    private val systemPermissionManager: SystemPermissionManager,
) : SystemPhotoStatusChecker {

    private fun getSystemPhotoStatusInternal(systemPhotoId: SystemPhotoId): SystemPhotoStatus {
        if (!systemPermissionManager.systemMediaPermissionStatus.value.isAuthorizedOrLimited()) {
            return SystemPhotoStatus.NotAuthorizedToCheck
        }

        try {
            val photoUri: Uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                systemPhotoId.id
            )
            context.contentResolver.query(
                photoUri,
                arrayOf(MediaStore.MediaColumns._ID),
                null,
                null,
                null
            )?.use { cursor ->
                return if (cursor.moveToFirst()) {
                    SystemPhotoStatus.ExistsInPhotoLibrary(systemPhotoId)
                } else {
                    SystemPhotoStatus.NotInPhotoLibrary
                }
            }
        } catch (e: SecurityException) {
            return SystemPhotoStatus.NotAuthorizedToCheck
        }
        return SystemPhotoStatus.NotAuthorizedToCheck
    }

    override suspend fun getSystemPhotoStatusSuspend(systemPhotoId: SystemPhotoId): SystemPhotoStatus =
        getSystemPhotoStatusInternal(systemPhotoId)

    override fun getSystemPhotoStatus(systemPhotoId: SystemPhotoId): Flow<SystemPhotoStatus> = flow {
        emit(getSystemPhotoStatusInternal(systemPhotoId))
    }
}
