package wallapp.remoteapi

import app.cash.turbine.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import wallapp.media.network.model.NetworkMediaMapResult
import wallapp.media.network.repository.NetworkMediaMapRepositoryConfig
import wallapp.media.network.repository.NetworkMediaMapRepositoryKtor
import wallapp.search.network.repository.NetworkSearchContentRepositoryConfig
import wallapp.search.network.repository.NetworkSearchContentRepositoryKtor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorRepositoriesTest {

    private fun clientReturning(body: String) = HttpClient(MockEngine { _ ->
        respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    })

    @Test
    fun mediaMapKtor_fetchesAndParses() = runTest {
        val url = MutableStateFlow("https://media-staging.example.com/api/99999999/media-1a-c-p~s")
        val body = """{"version":1,"data":{"7":{"am":"https://cdn.example.com/a.png"}}}"""
        val repo = NetworkMediaMapRepositoryKtor(
            config = object : NetworkMediaMapRepositoryConfig {
                override val endpointUrl = url
            },
            httpClient = clientReturning(body),
        )
        repo.fetchNetworkMediaMap(forceRefresh = false).test {
            val result = awaitItem()
            assertTrue(result is NetworkMediaMapResult.Success)
            assertEquals(url.value, (result as NetworkMediaMapResult.Success).sourceId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchKtor_emitsNullOnServerError() = runTest {
        val url = MutableStateFlow("https://media-staging.example.com/api/99999999/content-metadata-1a")
        val failingClient = HttpClient(MockEngine { _ ->
            respond("boom", HttpStatusCode.InternalServerError)
        })
        val repo = NetworkSearchContentRepositoryKtor(
            config = object : NetworkSearchContentRepositoryConfig {
                override val endpointUrl = url
            },
            httpClient = failingClient,
        )
        repo.fetchNetworkContent(forceRefresh = false).test {
            assertEquals(null, awaitItem())   // null-on-error contract, matching the Network impl
            cancelAndIgnoreRemainingEvents()
        }
    }
}
