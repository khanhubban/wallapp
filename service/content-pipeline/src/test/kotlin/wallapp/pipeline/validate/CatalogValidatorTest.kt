package wallapp.pipeline.validate

import wallapp.pipeline.IMGIX_HOST_PREFIX
import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CatalogValidatorTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp",
            tags = listOf("dark"))),
    )
    private fun bundle(base: String = "https://media-staging.stillscenes.app") = WireBundle(
        content = CatalogBuilder.build(manifest().copy(baseUrl = base)),
        search = SearchBuilder.build(manifest()),
        media = MediaMapBuilder.build(manifest().copy(baseUrl = base)),
        baseUrl = base,
        imgixHostPrefix = IMGIX_HOST_PREFIX,
    )

    @Test fun passesForACoherentBundle() {
        CatalogValidator.validate(bundle()) // no throw
    }

    @Test fun failsWhenAMediaIdIsMissingFromTheMap() {
        val b = bundle()
        val broken = b.copy(media = wallapp.media.network.model.NetworkMediaData(1, emptyMap()))
        assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
    }

    @Test fun failsWhenRenditionUrlIsOnImgixHost() {
        assertFailsWith<IllegalStateException> {
            CatalogValidator.validate(bundle(base = "https://stillscenes.imgix.net"))
        }
    }
}
