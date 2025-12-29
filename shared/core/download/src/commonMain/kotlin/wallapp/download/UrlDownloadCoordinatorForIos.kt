package wallapp.download

interface UrlDownloadCoordinatorForIos {

    fun downloadUrlToFile(
        url: String,
        downloadProgress: (Float) -> Unit,
        downloadCompleted: (String) -> Unit,
        downloadError: (String) -> Unit,
        downloadCancelled: () -> Unit
    )

    fun cancelDownload(url: String)
}