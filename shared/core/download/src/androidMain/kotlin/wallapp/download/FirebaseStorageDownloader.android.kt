package wallapp.download

import com.google.firebase.storage.StorageException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.android
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.data.DataHandle
import wallapp.log.Log
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class FirebaseStorageDownloaderAndroid : FirebaseStorageDownloader {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override fun downloadFile(path: String, appendData: Boolean): Flow<DownloadState> =
        callbackFlow {
            if (path.isEmpty()) {
                trySendBlocking(DownloadState.Error(url = "", message = "Empty path"))
                return@callbackFlow
            }
            trySendBlocking(DownloadState.DownloadStarting(path))
            val finalPath = if (appendData) "$path.data" else path
            val storage = Firebase.storage.android
            val ref = storage.reference
            val dataRef = ref.child(finalPath)
            val tempFile = File.createTempFile("content", "json")
            Log.d("[FirebaseStorageDownloaderAndroid] tempFile: ${tempFile.absolutePath}")
            val task = dataRef.getFile(tempFile)
            task.addOnSuccessListener {
                Log.d("[FirebaseStorageDownloaderAndroid] Downloaded file: ${tempFile.absolutePath}")
                trySendBlocking(
                    DownloadState.Success(
                        url = finalPath,
                        dataHandle = DataHandle.DiskBacked(tempFile.absolutePath)
                    )
                )
            }.addOnFailureListener {
                Log.e("[FirebaseStorageDownloaderAndroid] Failed to download content: ${it.message}")
                val firebaseStorageDownloadError = createFirebaseStorageDownloadError(it)
                val message = "Failed to download content: ${it.message}, code: ${firebaseStorageDownloadError?.code}"
                if (firebaseStorageDownloadError?.userCancelled == true) {
                    trySendBlocking(DownloadState.Cancelled(url = finalPath))
                } else {
                    crashTracking.logNonFatalException(
                        NonFatalException(
                            message = message,
                            cause = it
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
                Log.d("[FirebaseStorageDownloaderAndroid] downloadFile: awaitClose(), path: $path")
                task.cancel()
            }
        }.catch {
            Log.e("[FirebaseStorageDownloaderAndroid] downloadFile: error: $it")
        }.flowOn(Dispatchers.IO)

    private fun createFirebaseStorageDownloadError(exception: Exception): FirebaseStorageDownloadError? {
        if (exception !is StorageException) {
            return null
        }
        return FirebaseStorageDownloadError(
            message = exception.message ?: "",
            code = exception.errorCode,
            userCancelled = exception.errorCode == StorageException.ERROR_CANCELED
        )
    }

    init {
        Firebase.storage.setMaxOperationRetryTime(MAX_OPERATION_RETRY_TIME_SECONDS.toDuration(DurationUnit.SECONDS))
    }
}