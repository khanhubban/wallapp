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

    @Test fun previewIdCarriesFeedFullscreenAndCollectionLayerKeys() {
        val data = MediaMapBuilder.build(manifest())
        val pv = data.mediaMap[mediaId("stillscenes_1a2b3c4d:preview")]!!

        // A collection card stacks three preview layers and looks each up by its own SizedImage
        // key (SizedImage.kt: wcs0..2 small, wcl0..2 large). Without them the card renders as a
        // broken image. The demo catalog carries the same shape for collection members.
        assertEquals(
            setOf("s", "wfs", "wft", "fs", "wcs0", "wcs1", "wcs2", "wcl0", "wcl1", "wcl2"),
            pv.keys,
        )
    }

    @Test fun everyCollectionLayerKeyResolvesToThatWallpapersPreview() {
        val data = MediaMapBuilder.build(manifest())
        val pv = data.mediaMap[mediaId("stillscenes_1a2b3c4d:preview")]!!

        val expected = "https://media-staging.stillscenes.app/media/stillscenes_1a2b3c4d/preview.webp"
        for (key in listOf("wcs0", "wcs1", "wcs2", "wcl0", "wcl1", "wcl2")) {
            assertEquals(expected, pv[key], "layer key $key")
        }
    }

    @Test fun artistProfileIdCarriesArtistKeys() {
        val data = MediaMapBuilder.build(manifest())
        val ar = data.mediaMap[mediaId("stillscenes:profile")]!!
        assertEquals(setOf("am", "as", "e"), ar.keys)
    }

    @Test fun folderProfileCarriesWfsKey() {
        val data = MediaMapBuilder.build(manifest())
        val profile = data.mediaMap[mediaId("f~justadded:profile")]!!
        assertEquals(setOf("wfs"), profile.keys)
    }

    @Test fun folderBannerCarriesExhibitKey() {
        val data = MediaMapBuilder.build(manifest())
        val banner = data.mediaMap[mediaId("f~justadded:banner")]!!

        // The folder's carousel highlight asks for SizedImage.Exhibit ("e"), not the feed key.
        // With only "wfs" the card renders on a blank background. The demo catalog's
        // f~justadded banner carries exactly ["e"].
        assertEquals(setOf("e"), banner.keys)
        assertTrue(banner["e"]!!.endsWith("/media/folder/justadded/banner.webp"))
    }
}
