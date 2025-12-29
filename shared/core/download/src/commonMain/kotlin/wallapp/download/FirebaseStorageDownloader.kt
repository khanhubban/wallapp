package wallapp.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface FirebaseStorageDownloader {
    fun downloadFile(path: String, appendData: Boolean = true): Flow<DownloadState>
}

object FirebaseStorageDownloaderNoOp : FirebaseStorageDownloader {
    override fun downloadFile(path: String, appendData: Boolean): Flow<DownloadState> = emptyFlow()
}

const val MAX_OPERATION_RETRY_TIME_SECONDS = 30