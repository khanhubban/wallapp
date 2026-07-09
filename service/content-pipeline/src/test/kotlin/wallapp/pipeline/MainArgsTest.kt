package wallapp.pipeline

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    // I2: a typo'd flag must be rejected, not silently swallowed into "not the manifest."
    @Test fun unknownFlagIsRejected() {
        val e1 = assertFailsWith<IllegalStateException> { parseArgs(arrayOf("m.json", "--dryrun")) }
        assertTrue(e1.message!!.contains("--dryrun"), "actual: ${e1.message}")

        val e2 = assertFailsWith<IllegalStateException> { parseArgs(arrayOf("m.json", "--dry-run=true")) }
        assertTrue(e2.message!!.contains("--dry-run=true"), "actual: ${e2.message}")
    }

    @Test fun missingManifestErrors() {
        assertFailsWith<IllegalStateException> { parseArgs(arrayOf()) }
        assertFailsWith<IllegalStateException> { parseArgs(arrayOf("--dry-run")) }
    }

    // C1: the bucket and the RC key must come from the manifest's baseUrl, not a flag.
    @Test fun bucketIsDerivedFromTheStagingBaseUrl() {
        assertEquals("stillscenes-content-staging", bucketFor("https://media-staging.stillscenes.app"))
    }

    @Test fun bucketIsDerivedFromTheProdBaseUrl() {
        assertEquals("stillscenes-content-prod", bucketFor("https://media.stillscenes.app"))
    }

    @Test fun unknownBaseUrlHostPublishesNowhere() {
        assertFailsWith<IllegalStateException> { bucketFor("https://evil.example.com") }
    }

    @Test fun catalogVersionKeyIsDerivedFromTheBaseUrl() {
        assertEquals("catalog_version_staging", catalogVersionKeyFor("https://media-staging.stillscenes.app"))
        assertEquals("catalog_version", catalogVersionKeyFor("https://media.stillscenes.app"))
    }
}
