package wallapp.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import wallapp.data.DataHandle

class DownloadManagerMock(
    private var finalResult: DownloadState = DownloadState.Success(
        url = "https://example.com",
        dataHandle = DataHandle.fromBytesCompat(ByteArray(0)),
    )
) : DownloadManager {

    override val activeDownloadsStatus: Flow<ActiveDownloadStatus>
        get() = flowOf()

    override fun cancelDownload(url: String) {
        /*NOOP*/
    }

    override fun downloadUrl(url: String): Flow<DownloadState> = flow {
        // Simulate adding to queue
        emit(DownloadState.Queued(url))

        // Simulate download starting
        emit(DownloadState.DownloadStarting(url))

        // Check if the final result is an Error to skip downloading simulation
        if (finalResult is DownloadState.Error) {
            emit(finalResult)
            return@flow
        }

        // Simulate download progress
        val totalDownloadSteps = 5
        val stepDuration = 1000L // milliseconds
        for (step in 1..totalDownloadSteps) {
            emit(DownloadState.Downloading(url, progress = step * 20f))
        }

        // Emit the finalResult which could be Success or Error based on the constructor parameter
        emit(finalResult)
    }

    // Example method to update the final result, if needed
    fun updateFinalResult(newResult: DownloadState) {
        finalResult = newResult
    }

    override fun clearCompletedDownloadStatus() { }

}
