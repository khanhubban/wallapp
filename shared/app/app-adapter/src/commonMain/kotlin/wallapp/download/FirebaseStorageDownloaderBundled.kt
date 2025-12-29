package wallapp.download

import wallapp.data.DataBlob
import wallapp.data.DataHandle
import wallapp.download.DownloadState
import wallapp.download.FirebaseStorageDownloader
import wallapp.log.Logger
import wallapp.string.quote
import wallapp.resources.LocalFileAssetBundled
import wallapp.resources.readBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This file is a hack for the open source release, which allows bundling the API data with the app
 * instead of downloading it from Firebase Storage, thus making the project easier to set up and run
 * locally.
 */
object FirebaseStorageDownloaderBundled : FirebaseStorageDownloader {

    private val Log = Logger("[FirebaseStorageDownloaderBundled]")

    override fun downloadFile(
        path: String,
        appendData: Boolean,
    ): Flow<DownloadState> = flow {
        if (path.isEmpty()) {
            emit(DownloadState.Error(url = "", message = "Empty path"))
            return@flow
        }

        emit(DownloadState.DownloadStarting(path))

        val finalPath = if (appendData) "$path.data" else path

        Log.d("downloadFile(finalPath=${finalPath.quote()})")

        val fileAssetBundled = LocalFileAssetBundled.All.find { it.fileName == finalPath }
        if (fileAssetBundled == null) {
            emit(DownloadState.Error(url = finalPath, message = "Bundled asset not found: ${finalPath.quote()} - Check LocalFileAssetBundled"))
            return@flow
        }

        val resource = fileAssetBundled.toResource()
        val bytes = resource.readBytes()
        if (bytes == null) {
            emit(DownloadState.Error(url = finalPath, message = "Failed to read bytes from bundled resource: ${finalPath.quote()}, ${finalPath.quote()} - Check LocalFileAssetBundled"))
            return@flow
        }

        emit(
            DownloadState.Success(
                url = finalPath,
                dataHandle = DataHandle.InMemory(DataBlob(bytes))
            )
        )
    }
}