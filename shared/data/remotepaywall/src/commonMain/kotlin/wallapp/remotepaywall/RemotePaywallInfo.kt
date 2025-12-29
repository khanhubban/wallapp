package wallapp.remotepaywall

data class RemotePaywallInfo(
    val databaseId: String,
    val identifier: String,
    val name: String,
    val url: String,
    val data: Any?,
)