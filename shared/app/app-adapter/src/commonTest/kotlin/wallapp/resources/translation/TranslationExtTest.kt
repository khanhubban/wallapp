package wallapp.resources.translation

import wallapp.resources.translation.TranslationExt.verifyFormatting
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TranslationExtTest {

    @Test
    fun verifyFormatting() {
        assertTrue(verifyFormatting("Hello, %s!", "%s Hola!"))
        assertFalse(verifyFormatting("Hello %d", "Hello %s"))
        assertFalse(verifyFormatting("Hello %d", "Hello %d %d"))
        assertFalse(verifyFormatting("Hello %d%s", "Hello %d %d"))
        assertTrue(verifyFormatting("Value: %d", "मूल्य: %d"))
        assertTrue(verifyFormatting("Coordinates: %d, %d", "Coordinates: %d, %d"))
        assertFalse(verifyFormatting("Value: %d", "मूल्य: %s"))
        assertFalse(verifyFormatting("Coordinates: %d, %d", "Coordinates: %d, %s"))
        assertTrue(verifyFormatting("Price: %d%s", "मूल्य: %d%s"))
        assertTrue(verifyFormatting("%d%% discount", "%d%% छूट"))
        assertFalse(verifyFormatting("%d%% discount", "%s%% छूट"))
        assertTrue(verifyFormatting("File size: %dMB", "फाइल का आकार: %dMB"))
        assertFalse(verifyFormatting("File size: %dMB", "फाइल का आकार: %sMB"))
        assertTrue(verifyFormatting("Progress: %d of %d", "प्रगति: %d में से %d"))
        assertFalse(verifyFormatting("Progress: %d of %d", "प्रगति: %s में से %d"))
    }
}