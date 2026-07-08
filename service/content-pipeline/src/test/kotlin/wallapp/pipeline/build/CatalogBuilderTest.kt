package wallapp.pipeline.build

import wallapp.content.network.model.NetworkContent
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogBuilderTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp")),
    )

    @Test fun catalogHasAllRequiredArraysAndResolvesReferences() {
        val c: NetworkContent = CatalogBuilder.build(manifest())
        assertEquals(1, c.wallpapers.size); assertEquals(1, c.categories.size)
        assertEquals(1, c.artists.size); assertEquals(1, c.folders.size)
        val w = c.wallpapers[0]
        assertEquals("stillscenes", w.artistId)
        assertEquals(c.categories[0].id, w.categoryId)          // categoryId resolves
        assertEquals(mediaId("stillscenes_1a2b3c4d:download"), w.wallpaperDownloadMedia.hdMediaId)
        assertEquals(mediaId("stillscenes_1a2b3c4d:preview"), w.previews.standard[0].id)
        assertTrue(c.categories[0].remixIds.contains(w.id))      // category lists the remix
    }

    @Test fun roundTripsThroughClientModel() {
        val c = CatalogBuilder.build(manifest())
        assertEquals(c, NetworkContent.fromExportString(c.exportString))
    }
}
