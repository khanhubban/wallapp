package wallapp.content

import kotlinx.serialization.json.Json
import wallapp.content.network.model.NetworkContent
import wallapp.content.network.model.NetworkMedia
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetworkContentCompatTest {

    // Forward-compat guard: unknown keys anywhere must be ignored (already true; regression lock).
    @Test
    fun contentDecode_ignoresUnknownKeys() {
        val json = """{"wallpapers":[],"categories":[],"artists":[],"folders":[],"futureField":{"x":1}}"""
        val content = NetworkContent.fromExportString(json)
        assertEquals(0, content.wallpapers.size)
    }

    // blurHash is nullable but has no default today -> missing key throws. Spec §3.4 forbids that.
    @Test
    fun mediaDecode_toleratesMissingBlurHash() {
        val media = Json.decodeFromString<NetworkMedia>("""{"id":42}""")
        assertNull(media.blurHash)
    }

}
