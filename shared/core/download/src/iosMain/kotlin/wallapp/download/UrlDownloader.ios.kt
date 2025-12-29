package wallapp.download

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.data.DataHandle
import wallapp.log.Log

class UrlDownloaderIos(
    private val urlDownloadCoordinatorForIos: UrlDownloadCoordinatorForIos
) : UrlDownloader {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override fun downloadUrl(url: String): Flow<DownloadState> {
        return callbackFlow {
            trySendBlocking(DownloadState.DownloadStarting(url))
            urlDownloadCoordinatorForIos.downloadUrlToFile(
                url = url,
                downloadProgress = { downloadProgress ->
                    trySendBlocking(
                        DownloadState.Downloading(
                            url,
                            downloadProgress
                        )
                    )
                },
                downloadCompleted = { downloadFilePath ->
                    trySendBlocking(
                        DownloadState.Success(
                            url,
                            DataHandle.DiskBacked(downloadFilePath)
                        )
                    )
                    close()
                },
                downloadError = { downloadError ->
                    crashTracking.logNonFatalException(
                        NonFatalException("Error downloading $url", Exception(downloadError))
                    )
                    trySendBlocking(DownloadState.Error(url, "Request timed out or failed"))
                    close()
                },
                downloadCancelled = {
                    trySendBlocking(DownloadState.Cancelled(url))
                    close()
                }
            )

            awaitClose {
                Log.d("[UrlDownloadCoordinator] [UrlDownloaderIos] closed")
                cancelDownload(url)
            }
        }
    }

    override fun cancelDownload(url: String) {
        urlDownloadCoordinatorForIos.cancelDownload(url)
    }
}