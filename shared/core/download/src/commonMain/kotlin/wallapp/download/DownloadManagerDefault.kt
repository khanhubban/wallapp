package wallapp.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import wallapp.log.Logger

class DownloadManagerDefault(
    private val urlDownloader: UrlDownloader,
    private val activeDownloadManager: ActiveDownloadManager,
    private val coroutineScopeIo: CoroutineScope,
) : DownloadManager {

    companion object {
        val Log = Logger("WallpaperDownload")
    }

    private val maxConcurrentDownloads = 1
    private val semaphore = Semaphore(maxConcurrentDownloads)

    override fun downloadUrl(url: String): Flow<DownloadState> {
        val result = flow {
            // Check if the download will be queued due to the semaphore's state
            val isQueued = semaphore.availablePermits == 0
            if (isQueued) {
                emit(DownloadState.Queued(url))
            }

            // Wait to acquire a permit, effectively queuing if necessary
            semaphore.acquire()
            try {
                emit(DownloadState.DownloadStarting(url))

                // Perform the actual download, emitting its states
                urlDownloader.downloadUrl(url).collect {
                    emit(it)
                }
            } finally {
                Log.d("Download finished: $url")
                semaphore.release()
            }
        }
            .flowOn(Dispatchers.IO)
            .onEach { activeDownloadManager.updateDownloadState(it) }

        return result
    }

    override val activeDownloadsStatus: Flow<ActiveDownloadStatus>
        get() = activeDownloadManager.activeDownloadsStatus

    override fun cancelDownload(url: String) {
        coroutineScopeIo.launch {
            urlDownloader.cancelDownload(url)
        }
    }

    override fun clearCompletedDownloadStatus() {
        activeDownloadManager.clearCompletedDownloadStatus()
    }

}
