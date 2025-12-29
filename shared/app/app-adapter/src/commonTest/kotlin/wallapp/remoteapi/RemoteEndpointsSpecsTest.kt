package wallapp.remoteapi

import wallapp.string.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteEndpointsSpecsTest {

    private val RemoteEndpointsSpecStagingUnobfuscated = RemoteEndpointsSpec(
        bucketRoot = "api",
        apiVersion = "v0",
        base = "api/v0/",
    )

    @Test
    fun `Staging apiVersion should return the api version`() {
        assertEquals(RemoteEndpointsSpecStagingUnobfuscated, RemoteEndpointsSpecs.Staging)
    }

    @Test
    fun `Staging log base64 values`() {
        val remoteEndpointsSpec = RemoteEndpointsSpecStagingUnobfuscated
        // Note: the random garbage suffixes are there to prevent the "=" being added to the end of
        // the base64 string.
        println("bucketRoot: ${Base64.encodeToBase64(remoteEndpointsSpec.bucketRoot + "vads89fadsfa")}, .take(${remoteEndpointsSpec.bucketRoot.length})")
        println("apiVersion: ${Base64.encodeToBase64(remoteEndpointsSpec.apiVersion + "fdsffss")}, .take(${remoteEndpointsSpec.apiVersion.length})")
        println("base: ${Base64.encodeToBase64(remoteEndpointsSpec.base + "yergfbhv")}, .take(${remoteEndpointsSpec.base.length})")
    }
}