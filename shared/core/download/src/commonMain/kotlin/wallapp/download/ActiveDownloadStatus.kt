package wallapp.download

sealed interface ActiveDownloadStatus {

     data object None: ActiveDownloadStatus

     data class InProgress(
        val currentDownloadProgress: Float,
        val currentDownloadIndex: Int,
        val totalDownloadCount: Int,
     ): ActiveDownloadStatus

    data class Completed(
        val successfulDownloadCount: Int,
        val errors: List<DownloadState.Error>?,
    ): ActiveDownloadStatus
}