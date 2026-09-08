package wallapp.pipeline.publish

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WranglerClientTest {

    @Test fun wireFileKeyGetsJsonContentTypeAndImmutableCache() {
        var capturedCmd: List<String>? = null
        val client = WranglerClient(bucket = "stillscenes-content-staging", exec = { cmd ->
            capturedCmd = cmd
            WranglerClient.ExecResult(0, "")
        })

        client.put("/tmp/content-1a.json", "api/20260708-01/content-1a")

        val cmd = capturedCmd ?: error("exec was never invoked")
        assertTrue(cmd.contains("--content-type=application/json"), "expected json content-type, got: $cmd")
        assertTrue(cmd.contains("--remote"), "expected --remote flag, got: $cmd")
        assertTrue(cmd.contains("--cache-control=public, max-age=31536000, immutable"), "expected immutable cache-control, got: $cmd")
        assertTrue(cmd.contains("--file=/tmp/content-1a.json"), "expected --file flag, got: $cmd")
        assertTrue(cmd.contains("stillscenes-content-staging/api/20260708-01/content-1a"), "expected bucket/key target, got: $cmd")
    }

    @Test fun webpKeyGetsImageWebpContentType() {
        var capturedCmd: List<String>? = null
        val client = WranglerClient(bucket = "stillscenes-content-staging", exec = { cmd ->
            capturedCmd = cmd
            WranglerClient.ExecResult(0, "")
        })

        client.put("/tmp/download.webp", "media/x/download.webp")

        val cmd = capturedCmd ?: error("exec was never invoked")
        assertTrue(cmd.contains("--content-type=image/webp"), "expected webp content-type, got: $cmd")
        assertTrue(cmd.contains("stillscenes-content-staging/media/x/download.webp"), "expected bucket/key target, got: $cmd")
    }

    @Test fun nonZeroExitCodeThrowsIllegalStateException() {
        var capturedCmd: List<String>? = null
        val client = WranglerClient(bucket = "stillscenes-content-staging", exec = { cmd ->
            capturedCmd = cmd
            WranglerClient.ExecResult(1, "boom: authentication expired")
        })

        assertFailsWith<IllegalStateException> {
            client.put("/tmp/content-1a.json", "api/20260708-01/content-1a")
        }

        val cmd = capturedCmd ?: error("exec was never invoked")
        assertTrue(cmd.contains("--file=/tmp/content-1a.json"), "expected well-formed command even on failure, got: $cmd")
        assertTrue(cmd.contains("stillscenes-content-staging/api/20260708-01/content-1a"), "expected bucket/key target, got: $cmd")
    }
}
