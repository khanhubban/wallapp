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

    @Test
    fun sizedImageKeysAreUnique() {
        assertEquals(
            SizedImage.entries.size,
            SizedImage.entries.map { it.key }.distinct().size,
            "duplicate wire keys in SizedImage",
        )
    }

    @Test
    fun requiredKeyStringsMatchTheEnumKeys() {
        assertEquals(setOf("dhd", "dsd"), MediaEntityKind.WallpaperDownload.requiredKeyStrings)
        assertEquals(setOf("e"), MediaEntityKind.FolderBanner.requiredKeyStrings)
    }
}
