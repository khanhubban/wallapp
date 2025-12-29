package wallapp.network.httpclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.UnknownHostException

actual fun createNetworkHttpClient(loggingEnabled: Boolean): HttpClient =
    HttpClient(OkHttp) {
        // Enable to test DNS resolution failure, #1740.
//        engine {
//            config {
//                this.createMockOkHttpClient()
//            }
//            preconfigured = OkHttpClient.Builder().createMockOkHttpClient()
//        }

        if (loggingEnabled) {
            install(Logging) {
                level = LogLevel.ALL
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
//                    explicitNulls = false
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }
    }


object MockDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return if (hostname.contains("imgix.net")) {
            throw UnknownHostException("Unable to resolve host: $hostname")
        } else {
            Dns.SYSTEM.lookup(hostname)
        }
    }
}

fun OkHttpClient.Builder.createMockOkHttpClient(): OkHttpClient {
    return dns(MockDns)
        .build()
}

