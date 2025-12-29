package wallapp.download

data class FirebaseStorageDownloadError(
    val message: String,
    val code: Int,
    val userCancelled: Boolean = false
)