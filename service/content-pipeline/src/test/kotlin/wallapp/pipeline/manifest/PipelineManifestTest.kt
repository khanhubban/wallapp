package wallapp.pipeline.manifest

import kotlin.test.Test
import kotlin.test.assertEquals

class PipelineManifestTest {
    @Test fun parsesMinimalManifest() {
        val json = """
        {"version":"20260708-01","baseUrl":"https://media-staging.stillscenes.app",
         "artist":{"id":"stillscenes","label":"StillScenes","profileImagePath":"media/artist/stillscenes/profile.webp"},
         "folder":{"id":"f~justadded","title":"Just Added","profileImagePath":"media/folder/justadded/profile.webp","featureBannerImagePath":"media/folder/justadded/banner.webp"},
         "wallpapers":[{"id":"stillscenes_1a2b3c4d","label":"Aurora 01","isDark":true,"width":1440,"height":3120,
           "downloadRenditionPath":"media/stillscenes_1a2b3c4d/download.webp","previewRenditionPath":"media/stillscenes_1a2b3c4d/preview.webp",
           "styles":["amoled"],"tags":["dark","minimal"],"colors":["dark"]}]}
        """.trimIndent()
        val m = PipelineManifest.parse(json)
        assertEquals("20260708-01", m.version)
        assertEquals(1, m.wallpapers.size)
        assertEquals("stillscenes_1a2b3c4d", m.wallpapers[0].id)
        assertEquals("media/stillscenes_1a2b3c4d/download.webp", m.wallpapers[0].downloadRenditionPath)
    }
}
