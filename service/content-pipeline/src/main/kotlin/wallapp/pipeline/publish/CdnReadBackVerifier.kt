package wallapp.pipeline.publish

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking

class CdnReadBackVerifier : ObjectFetcher {
    private val client = HttpClient(OkHttp)
    override fun fetch(url: String): ByteArray? = runBlocking {
        val resp = client.get(url)
        if (resp.status.isSuccess()) resp.readBytes() else null
    }
}
