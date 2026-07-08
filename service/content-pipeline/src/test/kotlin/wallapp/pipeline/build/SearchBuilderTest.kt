package wallapp.pipeline.build

import wallapp.search.model.NetworkSearchMetadata
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchBuilderTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp",
            styles = listOf("amoled"), tags = listOf("dark", "minimal"), colors = listOf("dark"))),
    )

    @Test fun buildsRequiredArraysWithTitleSuggestions() {
        val s: NetworkSearchMetadata = SearchBuilder.build(manifest())
        assertEquals(1, s.remixMetadata.size)
        assertEquals(1, s.artistMetadata.size)
        assertEquals(1, s.folderMetadata.size)
        val r = s.remixMetadata[0]
        assertEquals("stillscenes_1a2b3c4d", r.remixId)
        assertTrue(r.titleSuggestions.isNotEmpty())   // required, non-empty
        assertTrue(r.tags.any { it.term == "dark" })
    }

    @Test fun roundTripsThroughClientModel() {
        val s = SearchBuilder.build(manifest())
        assertEquals(s, NetworkSearchMetadata.fromExportString(s.exportString))
    }
}
