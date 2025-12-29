package wallapp.download

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import wallapp.coroutine.CoroutineContexts
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.data.DataHandle
import wallapp.environment.Environment
import wallapp.log.Logger
import wallapp.network.NetworkState
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap


class UrlDownloaderAndroid(
    private val httpClient: HttpClient,
    private val networkState: NetworkState,
    private val environment: Environment,
    private val coroutineContexts: CoroutineContexts,
) : UrlDownloader {

    companion object {
        private val Log = Logger("UrlDownloaderAndroid")
        private const val TempFolderPrefix = ".downloads_tmp"
    }

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    private val downloadJobs = ConcurrentHashMap<String, Job>()

    override fun downloadUrl(url: String) = channelFlow {

        suspend fun sendState(state: DownloadState) {
            send(state)
            if (state is DownloadState.Error) {
                Log.w("Error downloading $url: ${state.message}")
            }
        }

        if (!networkState.isConnected) {
            sendState(DownloadState.Error(url, "Network connection is not available"))
            return@channelFlow
        }

        val job = launch {
            sendState(DownloadState.DownloadStarting(url))
            try {
                val response: HttpResponse = httpClient.get(url)
                if (!response.status.isSuccess()) {
                    sendState(DownloadState.Error(url, "HTTP error with status code: ${response.status.value}"))
                    return@launch
                }
                val contentLength = response.contentLength()
                val tempFile = createTempFile(url)
                val byteReadChannel = response.body<ByteReadChannel>()
                try {
                    tempFile.sink().buffer().use { fileSink ->
                        var totalBytesRead = 0L
                        val buffer = ByteArray(32 * 1024) // Adjust buffer size as needed

                        while (!byteReadChannel.isClosedForRead && isActive) {
                            val bytesRead = byteReadChannel.readAvailable(buffer)
                            if (bytesRead <= 0) break

                            fileSink.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            val percentage =
                                contentLength?.let { (totalBytesRead.toDouble() / it).toFloat() }
                            sendState(DownloadState.Downloading(url, percentage))
                        }
                    }
                    sendState(DownloadState.Success(url, DataHandle.DiskBacked(tempFile.absolutePath)))
                } catch (exception: CancellationException) {
                    trySendBlocking(DownloadState.Cancelled(url))
                } catch (exception: ResponseException) {
                    trySendBlocking(DownloadState.Error(url, exception.localizedMessage ?: "Unknown error"))
                } finally {
                    byteReadChannel.cancel() // Ensure the channel is closed to free resources
                }
            } catch (exception: IOException) {
                crashTracking.logNonFatalException(
                    NonFatalException("Download failed for $url", exception)
                )
                sendState(DownloadState.Error(url, "Request timed out or failed due to IOException"))
            }
        }

        downloadJobs[url] = job

        // Await job completion to remove it from the map
        job.invokeOnCompletion {
            downloadJobs.remove(url)
            close()
        }

        // Await close is needed to keep the channel open until either manually closed or the coroutine completes.
        awaitClose {}
    }.flowOn(coroutineContexts.io)

    override fun cancelDownload(url: String) {
        // Note: the url will be removed from the map when the job completes
        downloadJobs[url]?.cancel()
    }

    private fun createTempFile(url: String): File {
        val downloadsTmpDir = File(environment.cacheDirectory, TempFolderPrefix)
        if (!downloadsTmpDir.exists()) downloadsTmpDir.mkdirs()

        val fileName = url.toMD5Hash() + ".tmp"
        return File(downloadsTmpDir, fileName)
    }

    private fun String.toMD5Hash(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
    }
}
