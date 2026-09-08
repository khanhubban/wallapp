package wallapp.pipeline.build

import wallapp.content.network.model.NetworkContent
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
        assertEquals(1, c.wallpapers.size); assertEquals(2, c.categories.size)
        assertEquals(1, c.artists.size); assertEquals(1, c.folders.size)
        val w = c.wallpapers[0]
        assertEquals("stillscenes", w.artistId)
        assertEquals(c.categories[0].id, w.categoryId)          // categoryId resolves
        assertEquals(mediaId("stillscenes_1a2b3c4d:download"), w.wallpaperDownloadMedia.hdMediaId)
        assertEquals(mediaId("stillscenes_1a2b3c4d:download"), w.wallpaperDownloadMedia.sdMediaId)
        assertEquals(mediaId("stillscenes_1a2b3c4d:preview"), w.previews.standard[0].id)
        assertTrue(w.isDark)                                    // isDark flows through from manifest
        assertTrue(c.categories[0].remixIds.contains(w.id))      // category lists the remix
    }

    @Test fun emitsAFreeCollectionAlongsideTheSinglesCategory() {
        val c = CatalogBuilder.build(manifest())

        val singles = c.categories.single { it.categoryType == "Singles" }
        val collection = c.categories.single { it.categoryType == "Collection" }

        // Explore requires at least one Collection-type category to render collection rows.
        assertEquals("stillscenes~singles", singles.id)
        assertEquals("stillscenes~featured", collection.id)

        // splitByCategoryType() classifies anything not ending in "~singles" as a collection.
        assertTrue(!collection.id.endsWith("~singles"))

        // No store product => free => never locked. WallpaperCategory tolerates a null here.
        assertNull(collection.purchasableProductIds)

        // The wallpapers stay Singles so the For You feed keeps listing them.
        assertEquals("stillscenes~singles", c.wallpapers[0].categoryId)
        assertTrue(c.wallpapers[0].isSingle)

        // ...while the collection references the same remixes.
        assertEquals(c.wallpapers.map { it.id }, collection.remixIds)
        assertEquals(c.wallpapers[0].id, collection.previewRemixId)
    }

    @Test fun artistAdvertisesBothCategories() {
        val c = CatalogBuilder.build(manifest())

        assertEquals(listOf("stillscenes~singles", "stillscenes~featured"), c.artists[0].categoryIds)
    }

    @Test fun roundTripsThroughClientModel() {
        val c = CatalogBuilder.build(manifest())
        assertEquals(c, NetworkContent.fromExportString(c.exportString))
    }
}
