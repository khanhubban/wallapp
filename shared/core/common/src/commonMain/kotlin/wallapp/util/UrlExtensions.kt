package wallapp.util

import io.ktor.http.Url

/**
 * Extracts the base domain URL from a given URL.
 */
fun buildBaseDomainUrl(url: String): String? {
    return try {
        val parsedUrl = Url(url)
        "${parsedUrl.protocol.name}://${parsedUrl.host}"
    } catch (e: Exception) {
        null
    }
}