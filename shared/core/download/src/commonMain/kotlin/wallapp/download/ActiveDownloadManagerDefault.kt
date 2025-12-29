package wallapp.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import wallapp.coroutine.collectIn
import wallapp.log.Logger

class ActiveDownloadManagerDefault(
    coroutineScopeIo: CoroutineScope,
) : ActiveDownloadManager {

    companion object {
        val Log = Logger("WallpaperDownload")
    }

    private var activeDownloadUrls = mutableListOf<String>()
    private val activeDownloads = mutableListOf<DownloadState>()
    private var finishedDownloadCount = 0

    override val activeDownloadsStatus: MutableStateFlow<ActiveDownloadStatus> =
        MutableStateFlow(ActiveDownloadStatus.None)

    override fun clearCompletedDownloadStatus() {
        activeDownloadsStatus.value = ActiveDownloadStatus.None
    }

    private val mutex = Mutex()

    override suspend fun updateDownloadState(downloadState: DownloadState) {
        Log.d("updateDownloadState(): $downloadState")
        mutex.withLock {
            when (downloadState) {
                is DownloadState.Queued, is DownloadState.DownloadStarting -> {
                    if (!activeDownloadUrls.contains(downloadState.url)) {
                        if (activeDownloadsStatus.value is ActiveDownloadStatus.None ||
                            activeDownloadsStatus.value is ActiveDownloadStatus.Completed) {
                            activeDownloadsStatus.value = ActiveDownloadStatusStarting()
                        }
                        activeDownloadUrls.add(downloadState.url)
                        activeDownloads.add(downloadState)
                    }
                }
                is DownloadState.Downloading -> {
                    // Find the matching state by URL and update it
                    val index = activeDownloads.indexOfFirst { it.url == downloadState.url }
                    if (index != -1) {
                        activeDownloads[index] = downloadState
                    }
                }
                is DownloadState.Success, is DownloadState.Error, is DownloadState.Cancelled -> {
                    // Remove completed or failed download
                    activeDownloads.removeAll { it.url == downloadState.url }
                    finishedDownloadCount++
                    if (downloadState is DownloadState.Error) {
                        // Handle error specifics if necessary
                    }
                }
            }
            updateActiveDownloadsStatus()
        }
    }

    private fun ActiveDownloadStatusStarting(): ActiveDownloadStatus {
        return ActiveDownloadStatus.InProgress(
            currentDownloadProgress = 0f,
            currentDownloadIndex = 0,
            totalDownloadCount = activeDownloadUrls.size
        )
    }

    private fun updateActiveDownloadsStatus() {
        val downloading = activeDownloads.filterIsInstance<DownloadState.Downloading>()
        val downloadingStarted = activeDownloads.filterIsInstance<DownloadState.DownloadStarting>()
        val progress = downloading.map { it.progress ?: 0f }.sum() / downloading.size.coerceAtLeast(1)
        val errors = activeDownloads.filterIsInstance<DownloadState.Error>()
        val activeDownloadIndex = finishedDownloadCount

        val status = when {
            activeDownloads.size == 0
                    && activeDownloadUrls.size != 0
                    && finishedDownloadCount == activeDownloadUrls.size -> {
                ActiveDownloadStatus.Completed(
                    successfulDownloadCount = activeDownloadUrls.size - errors.size,
                    errors = errors.takeIf { it.isNotEmpty() }
                ).also {
                    activeDownloadUrls.clear()
                    this.finishedDownloadCount = 0
                }
            }

            activeDownloadIndex > -1 && (downloading.isNotEmpty() || activeDownloads.size > 0) -> {
                ActiveDownloadStatus.InProgress(
                    currentDownloadProgress = progress.let { 0.12f + (it * (1f - 0.12f)) },
                    currentDownloadIndex = activeDownloadIndex,
                    totalDownloadCount = activeDownloadUrls.size
                )
            }

            activeDownloadIndex > -1 && (downloadingStarted.isNotEmpty() || activeDownloads.size > 0) -> {
                ActiveDownloadStatus.InProgress(
                    currentDownloadProgress = 0.1f,
                    currentDownloadIndex = activeDownloadIndex,
                    totalDownloadCount = activeDownloadUrls.size
                )
            }

            else -> {
                null
            }
        }

        if (status != null) {
            Log.d("update activeDownloadsStatus: $status")
            activeDownloadsStatus.value = status
        }
    }

    init {
        activeDownloadsStatus.collectIn(coroutineScopeIo) { status ->
            if (status is ActiveDownloadStatus.Completed) {
                clearCompletedDownloadStatus()
            }
        }
    }
}
