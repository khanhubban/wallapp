package wallapp.network.httpclient

import io.ktor.client.HttpClient

fun createNetworkHttpClient(): HttpClient = createNetworkHttpClient(loggingEnabled = true)

expect fun createNetworkHttpClient(loggingEnabled: Boolean): HttpClient
