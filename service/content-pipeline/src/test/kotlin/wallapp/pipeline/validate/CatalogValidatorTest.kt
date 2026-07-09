package wallapp.pipeline.validate

import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.IMGIX_HOST_PREFIX
import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

    // NetworkMediaData is a plain class (no `copy`), so corruptions build a fresh instance from
    // its `version` and a mutated `mediaMap` rather than `.copy(mediaMap = ...)`.

    @Test fun rejectsAFolderBannerCarryingTheFeedKeyInsteadOfExhibit() {
        val b = bundle()
        val bannerId = b.content.folders.first().featureBannerImage.id
        val broken = b.copy(
            media = NetworkMediaData(
                version = b.media.version,
                mediaMap = b.media.mediaMap.toMutableMap().apply {
                    put(bannerId, mapOf("wfs" to "https://cdn/banner.webp"))
                },
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("folder banner"))
    }

    @Test fun rejectsAnUnknownSizedImageKey() {
        val b = bundle()
        val previewId = b.content.wallpapers.first().previews.standard.first().id
        val broken = b.copy(
            media = NetworkMediaData(
                version = b.media.version,
                mediaMap = b.media.mediaMap.toMutableMap().apply {
                    put(previewId, b.media.mediaMap[previewId]!! + ("wsc0" to "https://cdn/typo.webp"))
                },
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("unknown SizedImage key"), "actual: ${e.message}")
        assertTrue(e.message!!.contains("wsc0"), "actual: ${e.message}")
    }

    @Test fun rejectsAnOrphanMediaMapEntry() {
        val b = bundle()
        val broken = b.copy(
            media = NetworkMediaData(
                version = b.media.version,
                mediaMap = b.media.mediaMap + (999_999_999L to mapOf("wfs" to "https://cdn/x.webp")),
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("orphan"))
    }
}
