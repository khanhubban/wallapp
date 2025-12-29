package wallapp.network.url

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class UrlValidatorHttpClient(
    private val httpClient: HttpClient,
) : UrlValidator {

    override suspend fun isValidUrl(url: String): Boolean {
        return try {
            val response: HttpResponse = httpClient.get(url)
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }
}