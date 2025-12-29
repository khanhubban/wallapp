package wallapp.image

import wallapp.theme.ColorToken
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


internal class ImageTest {

    @Test
    fun `imageFrom other Image`() {
        val image = Image.from("https://example.com")
        assertNull(image.contentDescription)
        assertNull(image.tintColorToken)

        val imageOptions = ImageOptions {
            tintColorToken = ColorToken.ThemeOnBackground
        }
        val imageAlt = Image.from(image, contentDescription = "foo", imageOptions = imageOptions)
        assertEquals("foo", imageAlt.contentDescription)
        assertEquals(ColorToken.ThemeOnBackground, imageAlt.tintColorToken)
    }
}