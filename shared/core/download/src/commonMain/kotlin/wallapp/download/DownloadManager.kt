package wallapp.download

import kotlinx.coroutines.flow.Flow

interface DownloadManager {

    fun downloadUrl(url: String): Flow<DownloadState>

    fun cancelDownload(url: String)

    val activeDownloadsStatus: Flow<ActiveDownloadStatus>

    fun clearCompletedDownloadStatus()
}
