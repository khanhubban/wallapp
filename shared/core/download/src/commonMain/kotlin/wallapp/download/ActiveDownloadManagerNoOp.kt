package wallapp.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object ActiveDownloadManagerNoOp : ActiveDownloadManager {
    override suspend fun updateDownloadState(downloadState: DownloadState) { }
    override val activeDownloadsStatus: Flow<ActiveDownloadStatus> = flowOf(ActiveDownloadStatus.None)
    override fun clearCompletedDownloadStatus() { }
}
