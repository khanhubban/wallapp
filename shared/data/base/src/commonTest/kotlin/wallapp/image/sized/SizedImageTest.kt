package wallapp.image.sized

import kotlin.test.Test
import kotlin.test.assertEquals

class SizedImageTest {

    @Test
    fun sizedImageKeysAreUnique() {
        assertEquals(
            SizedImage.entries.size,
            SizedImage.entries.map { it.key }.distinct().size,
            "duplicate wire keys in SizedImage",
        )
    }
}
