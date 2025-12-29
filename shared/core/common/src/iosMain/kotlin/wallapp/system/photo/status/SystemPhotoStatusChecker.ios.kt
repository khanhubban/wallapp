package wallapp.system.photo.status

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.Foundation.NSPredicate
import platform.Foundation.NSString
import platform.Photos.PHAsset
import platform.Photos.PHFetchOptions
import wallapp.permission.PhotoPermissionTypeIos
import wallapp.permission.SystemPermissionManager
import wallapp.permission.isAuthorizedOrLimited
import wallapp.permission.photoPermissionTypeIos
import wallapp.system.photo.SystemPhotoId

class SystemPhotoStatusCheckerIos(
    private val systemPermissionManager: SystemPermissionManager,
) : SystemPhotoStatusChecker {

    private val hasSystemGalleryPermission: Boolean
        get() = systemPermissionManager.systemMediaPermissionStatus.value.isAuthorizedOrLimited()

    /**
     * Unfortunately, the iOS Photos framework does not allow us to check if a photo exists in the
     * user's photo library with the Add to Photos permission. The Full Photos Library permission
     * is required to verify if an image our app added exists in the system photos.
     *
     * Furthermore, calling [PHAsset.fetchAssetsWithLocalIdentifiers] with just Add to Photos
     * permission will see the system display the "Photos access required" alert, which is not
     * ideal.
     */
    private val hasFullPhotoLibraryPermission: Boolean
        get() = systemPermissionManager.photoPermissionTypeIos == PhotoPermissionTypeIos.FullPhotosLibrary

    private fun getSystemPhotoStatusInternal(systemPhotoId: SystemPhotoId): SystemPhotoStatus {
        if (!hasSystemGalleryPermission || !hasFullPhotoLibraryPermission) {
            return SystemPhotoStatus.NotAuthorizedToCheck
        }

        val phLocalIdentifier: NSString = systemPhotoId.id as NSString
        val identifiers = listOf(phLocalIdentifier)
        val fetchOptions = PHFetchOptions().apply {
            predicate = NSPredicate.predicateWithFormat("localIdentifier == %@", phLocalIdentifier as NSString)
        }
        val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(identifiers, fetchOptions)

        val exists = fetchResult.count.toInt() > 0
        return if (exists) {
            SystemPhotoStatus.ExistsInPhotoLibrary(systemPhotoId)
        } else {
            SystemPhotoStatus.NotInPhotoLibrary
        }
    }

    override suspend fun getSystemPhotoStatusSuspend(systemPhotoId: SystemPhotoId): SystemPhotoStatus =
        getSystemPhotoStatusInternal(systemPhotoId)

    override fun getSystemPhotoStatus(systemPhotoId: SystemPhotoId): Flow<SystemPhotoStatus> = flow {
        emit(getSystemPhotoStatusInternal(systemPhotoId))
    }
}
