package wallapp.wallpaper.saver

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage
import wallapp.content.model.Id
import wallapp.content.model.WallpaperRemix
import wallapp.data.DataRepository
import wallapp.log.Logger
import wallapp.permission.PhotoPermissionTypeIos
import wallapp.permission.SystemPermissionManager
import wallapp.permission.isAuthorizedOrLimited
import wallapp.permission.photoPermissionTypeIos
import wallapp.string.quote
import wallapp.system.photo.status.SystemPhotoStatus
import wallapp.system.photo.status.SystemPhotoStatusChecker
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager


class WallpaperSaverManagerIos(
    private val dataRepository: DataRepository,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemPhotoStatusChecker: SystemPhotoStatusChecker,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : WallpaperSaverManager {

    companion object {
        val Log = Logger("WallpaperSaverManagerIos")
    }

    private val systemMediaPermissionIsAuthorizedOrLimited: Boolean
        get() = systemPermissionManager.systemMediaPermissionStatus.value.isAuthorizedOrLimited()
    private val checkExistsInGallery: Boolean
        get() = false//systemMediaPermissionIsAuthorizedOrLimited

    private val photoPermissionType: PhotoPermissionTypeIos
        get() = systemPermissionManager.photoPermissionTypeIos

    override suspend fun saveWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
        photoAlbumName: String,
    ) {
        trySaveWallpaper(wallpaperDownloadState, photoAlbumName)
    }

    private suspend fun trySaveWallpaper(
        downloadState: WallpaperDownloadState.Success,
        photoAlbumName: String,
    ) {
        val id = downloadState.id
        if (id is Id.RemixId) {
            val systemPhotoStatus = wallpaperSystemPhotoStatusManager.getSystemPhotoStatus(id)
            if (systemPhotoStatus is SystemPhotoStatus.ExistsInPhotoLibrary) {
                Log.i("trySaveWallpaper() - early exit, SystemPhotoStatus: $systemPhotoStatus, id: ${id.name.quote()}")
                return
            }
        }

        saveImageToGallery(downloadState, photoAlbumName)
    }

    private suspend fun saveImageToGallery(state: WallpaperDownloadState.Success, albumName: String) {
        val dataHandle = state.dataHandle
        val id = state.id

        if (!systemMediaPermissionIsAuthorizedOrLimited) {
            Log.e("[SystemPermission] saveImageToAlbum($albumName) - permission not granted, permissionStatus: ${systemPermissionManager.systemMediaPermissionStatus.value}")
            return
        }
        val data = dataRepository.getDataBlob(dataHandle)?.byteArray
        val image = data?.let { mapToImage(data) } ?: return

        when (photoPermissionType) {
            PhotoPermissionTypeIos.AddToPhotos -> {
                addImageToLibrary(image, createImageSaverCallback(id))
            }
            PhotoPermissionTypeIos.FullPhotosLibrary -> {
                saveImageToAlbum(image, albumName, createImageSaverCallback(id))
            }
        }
    }

    private fun createImageSaverCallback(id: Id): SaveImageCallback {
        return { result ->
            when (result) {
                is SaveImageResult.Success -> {
                    Log.d("Save image: id: ${id.name.quote()} - success, localIdentifier: ${result.localIdentifier}")
                    val systemPhotoId = result.systemPhotoId
                    if (systemPhotoId != null) {
                        val systemPhotoStatus = SystemPhotoStatus.ExistsInPhotoLibrary(systemPhotoId)
                        Log.d("SystemPhotoStatus: $systemPhotoStatus, id: ${id.name.quote()}")
                        wallpaperSystemPhotoStatusManager
                            .setSystemPhotoStatus(
                                id = id,
                                status = systemPhotoStatus,
                            )
                        if (checkExistsInGallery) {
                            coroutineScopeIo.launch {
                                systemPhotoStatusChecker
                                    .getSystemPhotoStatusSuspend(systemPhotoId = systemPhotoId)
                            }
                        }
                    }
                }

                is SaveImageResult.Error -> {
                    Log.e("Save image: id: ${id.name.quote()} - error: ${result.error.localizedDescription}")
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun mapToImage(data: ByteArray): UIImage? {
        val nsData = data.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), data.size.toULong())
        }
        return UIImage.imageWithData(nsData)
    }
}
