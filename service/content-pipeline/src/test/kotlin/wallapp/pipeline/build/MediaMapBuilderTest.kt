package wallapp.pipeline.build

import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaMapBuilderTest {
    private fun manifest() = PipelineManifest(
        version = "20260708-01", baseUrl = "https://media-staging.stillscenes.app",
        artist = ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        folder = ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        wallpapers = listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp")),
    )

    @Test fun downloadIdCarriesDhdDsd() {
        val data = MediaMapBuilder.build(manifest())
        val dl = data.mediaMap[mediaId("stillscenes_1a2b3c4d:download")]!!
        assertEquals(setOf("dhd", "dsd"), dl.keys)
        assertTrue(dl["dhd"]!!.endsWith("/media/stillscenes_1a2b3c4d/download.webp"))
    }

    @Test fun previewIdCarriesFeedAndFullscreenKeys() {
        val data = MediaMapBuilder.build(manifest())
        val pv = data.mediaMap[mediaId("stillscenes_1a2b3c4d:preview")]!!
        assertEquals(setOf("s", "wfs", "wft", "fs"), pv.keys)
    }

    @Test fun artistProfileIdCarriesArtistKeys() {
        val data = MediaMapBuilder.build(manifest())
        val ar = data.mediaMap[mediaId("stillscenes:profile")]!!
        assertEquals(setOf("am", "as", "e"), ar.keys)
    }
}
