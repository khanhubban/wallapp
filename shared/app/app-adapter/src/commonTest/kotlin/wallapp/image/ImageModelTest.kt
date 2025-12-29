package wallapp.image

import wallapp.image.ImageModel.Companion.isVideoUrl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageModelTest {

    @Test fun `verify isVideoUrl`() {
        assertTrue(isVideoUrl("https://example.com/video.mp4"))
        assertTrue(isVideoUrl("https://example.com/video.MP4"))
        assertFalse(isVideoUrl("https://example.com/video.png"))
        assertFalse(isVideoUrl("https://example.com/video.jpg"))
    }
}