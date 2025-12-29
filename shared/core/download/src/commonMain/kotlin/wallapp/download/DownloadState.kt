package wallapp.download

import wallapp.data.DataHandle

sealed interface DownloadState {

    val url: String

    data class Queued(
        override val url: String,
    ): DownloadState

    data class DownloadStarting(
        override val url: String,
    ): DownloadState

    data class Downloading(
        override val url: String,
        /**
         * Progress as a percentage, or null if unknown
         */
        val progress: Float?,
    ): DownloadState

    data class Success(
        override val url: String,
        val dataHandle: DataHandle,
    ): DownloadState

    data class Error(
        override val url: String,
        val message: String,
    ): DownloadState

    data class Cancelled(
        override val url: String,
    ): DownloadState
}
