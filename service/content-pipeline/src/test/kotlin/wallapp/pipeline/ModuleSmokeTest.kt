package wallapp.pipeline

import wallapp.content.network.model.NetworkContent
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleSmokeTest {
    @Test fun canConstructAndRoundTripEmptyCatalog() {
        val c = NetworkContent(wallpapers = emptyList(), categories = emptyList(), artists = emptyList(), folders = emptyList())
        assertEquals(c, NetworkContent.fromExportString(c.exportString))
    }
}
