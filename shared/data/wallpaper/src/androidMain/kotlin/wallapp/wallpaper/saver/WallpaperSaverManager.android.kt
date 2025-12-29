package wallapp.wallpaper.saver

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.bitmap.BitmapMapper.toBitmap
import wallapp.content.model.WallpaperRemix
import wallapp.data.DataBlob
import wallapp.data.DataHandle
import wallapp.data.DataRepository
import wallapp.log.Logger
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.system.photo.SystemPhotoId
import wallapp.system.photo.status.SystemPhotoStatus
import wallapp.system.photo.status.SystemPhotoStatusChecker
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.systemphotostatus.WallpaperSystemPhotoStatusManager
import java.io.File
import java.io.FileInputStream

class WallpaperSaverManagerAndroid(
    private val context: Context,
    private val systemPermissionManager: SystemPermissionManager,
    private val wallpaperSystemPhotoStatusManager: WallpaperSystemPhotoStatusManager,
    private val systemPhotoStatusChecker: SystemPhotoStatusChecker,
    private val dataRepository: DataRepository,
    private val coroutineScopeIo: CoroutineScope,
) : WallpaperSaverManager {

    companion object {
        val Log = Logger("WallpaperSaverManagerAndroid")
    }

    private fun SystemPhotoId(long: Long): SystemPhotoId? {
        return (if (long > 0) {
            SystemPhotoId(long.toString())
        } else {
            null
        }).also {
            Log.i("SystemPhotoId($long) = $it")
        }
    }

    private val readWriteGalleryPermissionGranted: Boolean
        get() = systemPermissionManager.systemMediaPermissionStatus.value == SystemPermissionStatus.Authorized

    override suspend fun saveWallpaper(
        wallpaperDownloadState: WallpaperDownloadState.Success,
        wallpaperRemix: WallpaperRemix,
        photoAlbumName: String
    ) {
        if (readWriteGalleryPermissionGranted) {
            saveDataToGallery(
                wallpaperDownloadState.dataHandle,
                photoAlbumName,
                wallpaperRemix.label,
            )?.also { systemPhotoId ->
                wallpaperSystemPhotoStatusManager.setSystemPhotoStatus(
                    wallpaperRemix.id,
                    SystemPhotoStatus.ExistsInPhotoLibrary(systemPhotoId)
                )
            }
        }
    }

    private suspend fun saveDataToGallery(
        imageDataHandle: DataHandle,
        folderName: String,
        filename: String,
    ): SystemPhotoId? {
        return when (imageDataHandle) {
            is DataHandle.DiskBacked -> {
                return saveFileToGallery(imageDataHandle.filePath, folderName, filename)?.also { systemPhotoId ->
                    coroutineScopeIo.launch {
                        systemPhotoStatusChecker
                            .getSystemPhotoStatusSuspend(systemPhotoId = systemPhotoId)
                            .also {
                                Log.i("SystemPhotoStatus: $it, id: $systemPhotoId")
                            }
                    }
                }
            }
            is DataHandle.InMemory -> {
                dataRepository.getDataBlob(imageDataHandle)?.let {
                    return saveByteArrayToGallery(it, folderName, filename)
                }
            }
        }
    }

    private suspend fun saveFileToGallery(filePath: String, folderName: String, filename: String): SystemPhotoId? {
        val file = File(filePath)
        if (!file.exists()) return null

        val contentResolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png") // Adjust MIME type as necessary
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName)
            }
        }

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            // For pre-Q devices, manually manage file creation and URI
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/$folderName/")
            if (!directory.exists()) directory.mkdirs()
            val mediaFile = File(directory, filename)
            values.put(MediaStore.MediaColumns.DATA, mediaFile.absolutePath)
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        if (uri != null) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(file).use { inputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }

            Log.d("Saved image ($folderName / $filename) to system media with id: $uri")
            return SystemPhotoId(ContentUris.parseId(uri))
        }

        return null
    }

    private suspend fun saveByteArrayToGallery(
        dataBlob: DataBlob,
        folderName: String,
        filename: String
    ): SystemPhotoId? {
        val imageData = dataBlob.byteArray
        val contentResolver = context.contentResolver

        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        val filePath: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filePath = Environment.DIRECTORY_PICTURES + File.separator + folderName
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                filePath
            )
        } else {
            filePath =
                Environment.getExternalStorageDirectory().toString() + "/Pictures/$folderName/"
            values.put(
                MediaStore.MediaColumns.DATA,
                filePath + filename
            )
        }
        if (!File(filePath).exists()) {
            Log.d("File path directory does not exist")
            File(filePath).mkdirs()
        }

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                imageData
                    .toBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            return SystemPhotoId(ContentUris.parseId(uri))
        }

        return null
    }
}