package wallapp.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import wallapp.data.DataHandle

/**
 * This implementation works, but downloads all content into a single ByteArray. This is generally
 * fine for testing but not suitable for runtime.
 */
class UrlDownloaderCompat(private val httpClient: HttpClient) : UrlDownloader {

    override fun downloadUrl(url: String): Flow<DownloadState> = flow {

        emit(DownloadState.DownloadStarting(url))

        val response: HttpResponse = httpClient.get(url)
        val contentLength = response.contentLength()

        val bufferSize = 8 * 1024
        var accumulatedData = ByteArray(0)

        val channel = response.body<ByteReadChannel>()
        while (!channel.isClosedForRead) {
            val buffer = ByteArray(bufferSize)
            val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
            if (bytesRead <= 0) break

            val readBytes = buffer.copyOf(bytesRead)
            accumulatedData += readBytes

            if (contentLength != null) {
                val totalBytesRead = accumulatedData.size.toLong()
                val percentage = (totalBytesRead.toDouble() / contentLength).toFloat()
                emit(DownloadState.Downloading(url, percentage))
            } else {
                emit(DownloadState.Downloading(url, progress = null))
            }
        }

        val dataHandle = DataHandle.fromBytesCompat(accumulatedData)
        emit(DownloadState.Success(url, dataHandle))
    }.catch {
        emit(DownloadState.Error(url, it.message ?: "Unknown error: $it"))
    }

    override fun cancelDownload(url: String) {
        TODO("Not yet implemented")
    }
}