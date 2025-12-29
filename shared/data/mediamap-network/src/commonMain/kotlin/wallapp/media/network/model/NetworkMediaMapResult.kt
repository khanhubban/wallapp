package wallapp.media.network.model

sealed interface NetworkMediaMapResult {

    data class Success(
        val networkMediaData: NetworkMediaData,
        // Either a URL or local cache
        val sourceId: String?,
    ) : NetworkMediaMapResult

    data class Error(val errorMessage: String) : NetworkMediaMapResult
}
