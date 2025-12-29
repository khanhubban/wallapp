package wallapp.download

import kotlinx.coroutines.flow.Flow

interface ActiveDownloadManager {

    suspend fun updateDownloadState(downloadState: DownloadState)

    val activeDownloadsStatus: Flow<ActiveDownloadStatus>

    fun clearCompletedDownloadStatus()
}