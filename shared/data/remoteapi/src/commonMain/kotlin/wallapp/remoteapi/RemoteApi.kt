package wallapp.remoteapi

import kotlinx.coroutines.flow.Flow
import wallapp.download.DownloadState

interface RemoteApi {
    fun getFile(path: String): Flow<DownloadState>
}