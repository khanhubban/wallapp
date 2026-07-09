package wallapp.pipeline

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainArgsTest {

    @Test fun dryRunFlagIsRecognisedInAnyPosition() {
        assertTrue(parseArgs(arrayOf("m.json", "--dry-run")).dryRun)
        assertTrue(parseArgs(arrayOf("--dry-run", "m.json")).dryRun)
        assertFalse(parseArgs(arrayOf("m.json")).dryRun)
    }

    @Test fun manifestPathIsTheFirstNonFlagArgument() {
        assertEquals("m.json", parseArgs(arrayOf("--dry-run", "m.json")).manifestPath)
    }
}
