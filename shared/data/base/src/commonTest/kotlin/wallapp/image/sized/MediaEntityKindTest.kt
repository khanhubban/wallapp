package wallapp.image.sized

import kotlin.test.Test
import kotlin.test.assertEquals

class MediaEntityKindTest {

    /**
     * A SizedImage that no media-map entry carries is a renderer asking for something the pipeline
     * never emits. This is what let wcs0..wcl2 sit unemitted while collection cards rendered broken.
     */
    @Test
    fun everySizedImageIsCarriedBySomeEntityKind() {
        val emitted = MediaEntityKind.entries.flatMap { it.requiredKeys }.toSet()
        assertEquals(
            emptySet(),
            SizedImage.entries.toSet() - emitted,
            "SizedImage values that no MediaEntityKind emits",
        )
    }

    /**
     * Set.equals() is order-insensitive, so asserting against a Set cannot observe the declaration
     * order that determines published wire-key order. Compare lists.
     */
    @Test
    fun requiredKeyStringsPreserveDeclarationOrder() {
        assertEquals(
            listOf("dhd", "dsd"),
            MediaEntityKind.WallpaperDownload.requiredKeyStrings.toList(),
        )
        assertEquals(
            listOf("s", "wfs", "wft", "fs", "wcs0", "wcs1", "wcs2", "wcl0", "wcl1", "wcl2"),
            MediaEntityKind.WallpaperPreview.requiredKeyStrings.toList(),
        )
        assertEquals(
            listOf("am", "as", "e"),
            MediaEntityKind.ArtistProfile.requiredKeyStrings.toList(),
        )
        assertEquals(listOf("wfs"), MediaEntityKind.FolderProfile.requiredKeyStrings.toList())
        assertEquals(listOf("e"), MediaEntityKind.FolderBanner.requiredKeyStrings.toList())
    }
}
