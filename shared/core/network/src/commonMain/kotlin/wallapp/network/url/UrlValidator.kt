package wallapp.network.url

interface UrlValidator {

    /**
     * Returns true if the given URL is valid (200 status code).
     *
     * Note this function assumes there's a valid network connection.
     */
    suspend fun isValidUrl(url: String): Boolean
}