package wallapp.pipeline.build

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaIdsTest {
    @Test fun mediaIdIsStableAndPositive() {
        val a = mediaId("stillscenes_1a2b3c4d:download")
        assertEquals(a, mediaId("stillscenes_1a2b3c4d:download")) // stable
        assertTrue(a > 0L)
        assertTrue(a != mediaId("stillscenes_1a2b3c4d:preview")) // distinct roles differ
    }
    @Test fun renditionUrlJoinsWithoutDoubleSlash() {
        assertEquals(
            "https://media-staging.stillscenes.app/media/x/download.webp",
            renditionUrl("https://media-staging.stillscenes.app", "media/x/download.webp"),
        )
    }
}
