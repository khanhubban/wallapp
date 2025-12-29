package wallapp.download

import kotlinx.coroutines.flow.Flow

interface UrlDownloader {
    fun downloadUrl(url: String): Flow<DownloadState>

    fun cancelDownload(url: String)
}
