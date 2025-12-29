package wallapp.download

import com.google.cloud.storage.Blob
import com.google.cloud.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import wallapp.coroutine.CoroutineScopes
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.data.DataHandle
import wallapp.googlecloud.GoogleStorageRepository
import wallapp.log.Log
import java.io.File

class FirebaseStorageDownloaderAdmin(
    private val googleStorageRepository: GoogleStorageRepository,
    private val coroutineScopes: CoroutineScopes,
) : FirebaseStorageDownloader {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    private val storage: Storage
        get() = googleStorageRepository.storage
    private val bucketName = "<set_me>"

    override fun downloadFile(path: String, appendData: Boolean): Flow<DownloadState> = callbackFlow {
        if (path.isEmpty()) {
            trySendBlocking(DownloadState.Error(url = "", message = "Empty path"))
            return@callbackFlow
        }

        trySendBlocking(DownloadState.DownloadStarting(path))

        val finalPath = if (appendData) "$path.data" else path

        try {
            val tempFile = File.createTempFile("content", "json")
            Log.d("[FirebaseStorageDownloaderGoogleCloud] tempFile: ${tempFile.absolutePath}")

            // Retrieve the blob from the Google Cloud Storage bucket
            val blob: Blob = storage.get(bucketName, finalPath) ?: throw Exception("File not found in bucket: $finalPath")
            blob.downloadTo(tempFile.toPath())

            Log.d("[FirebaseStorageDownloaderGoogleCloud] Downloaded file: ${tempFile.absolutePath}")
            trySendBlocking(
                DownloadState.Success(
                    url = finalPath,
                    dataHandle = DataHandle.DiskBacked(tempFile.absolutePath)
                )
            )
        } catch (e: Exception) {
            Log.e("[FirebaseStorageDownloaderGoogleCloud] Failed to download content: ${e.message}")
            val firebaseStorageDownloadError = createFirebaseStorageDownloadError(e)
            val message = "Failed to download content: ${e.message}, code: ${firebaseStorageDownloadError?.code}"

            if (firebaseStorageDownloadError?.userCancelled == true) {
                trySendBlocking(DownloadState.Cancelled(url = finalPath))
            } else {
                crashTracking.logNonFatalException(
                    NonFatalException(
                        message = message,
                        cause = e
                    )
                )
                trySendBlocking(
                    DownloadState.Error(
                        url = finalPath,
                        message = message
                    )
                )
            }
        }

        awaitClose {
            Log.d("[FirebaseStorageDownloaderGoogleCloud] downloadFile: awaitClose(), path: $path")
        }
    }.catch {
        Log.e("[FirebaseStorageDownloaderGoogleCloud] downloadFile: error: $it")
    }.flowOn(Dispatchers.IO)

    private fun createFirebaseStorageDownloadError(exception: Exception): FirebaseStorageDownloadError? {
        return if (exception is com.google.cloud.storage.StorageException) {
            FirebaseStorageDownloadError(
                message = exception.message ?: "",
                code = exception.code,
                userCancelled = false//exception.code == StorageException.ERROR_CANCELED
            )
        } else {
            null
        }
    }
}