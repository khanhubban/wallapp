package wallapp.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.data.DataHandle
import wallapp.log.Log
import wallapp.network.NetworkConnectionException

class FirebaseStorageDownloaderIos(
    private val firebaseStorageDownloadCoordinator: FirebaseStorageDownloadCoordinatorIos
) : FirebaseStorageDownloader {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override fun downloadFile(path: String, appendData: Boolean): Flow<DownloadState> =
        callbackFlow {
            if (path.isEmpty()) {
                Log.d("[NRW] [FirebaseStorageDownloaderIos] Empty path")
                trySendBlocking(DownloadState.Error(url = path, message = "Empty path"))
                return@callbackFlow
            }
            val finalPath = if (appendData) "$path.data" else path
            trySendBlocking(DownloadState.DownloadStarting(finalPath))
            val cancellableWork = firebaseStorageDownloadCoordinator.downloadFile(finalPath) { downloadFilePath, firebaseStorageError ->
                when {
                    downloadFilePath != null -> {
                        Log.d("[NRW] [FirebaseStorageDownloaderIos] Downloaded file: $downloadFilePath")
                        trySendBlocking(
                            DownloadState.Success(
                                url = finalPath,
                                dataHandle = DataHandle.DiskBacked(downloadFilePath)
                            )
                        )
                    }
                    firebaseStorageError?.userCancelled == true -> {
                        Log.d("[NRW] [FirebaseStorageDownloaderIos] Download cancelled")
                        trySendBlocking(DownloadState.Cancelled(url = finalPath))
                    }
                    firebaseStorageError != null -> {
                        Log.e("[NRW] [FirebaseStorageDownloaderIos] Failed to download content: $firebaseStorageError")
                        val message = "Failed to download content: $firebaseStorageError, code: ${firebaseStorageError.code}"
                        crashTracking.logNonFatalException(
                            NetworkConnectionException(message)
                        )
                        trySendBlocking(
                            DownloadState.Error(
                                url = finalPath,
                                message = "Failed to download content: $firebaseStorageError, code: ${firebaseStorageError.code}"
                            )
                        )
                    }
                    else -> {
                        Log.e("[NRW] [FirebaseStorageDownloaderIos] Failed to download content: unknown error")
                        val message = "Failed to download content: unknown error"
                        crashTracking.logNonFatalException(
                            NetworkConnectionException(message)
                        )
                        trySendBlocking(
                            DownloadState.Error(
                                url = finalPath,
                                message = message
                            )
                        )
                    }
                }
            }

            awaitClose {
                cancellableWork.cancel()
            }
        }.flowOn(Dispatchers.IO)
}