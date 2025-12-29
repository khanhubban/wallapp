package wallapp.string

import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun `to and from base64`() {
        val input = "Hello, Multiplatform!"
        val encoded = Base64.encodeToBase64(input)
        val decoded = Base64.decodeFromBase64(encoded)
        assertEquals(input, decoded, "Base64 decoding failed.")
    }

    @Test
    fun `test encodeToBase64`() {
        val input = "Hello, Multiplatform!"
        val expectedBase64 = "SGVsbG8sIE11bHRpcGxhdGZvcm0h"

        val result = Base64.encodeToBase64(input)
        assertEquals(expectedBase64, result, "Base64 encoding failed.")
    }

    @Test
    fun `test decodeFromBase64`() {
        val base64Input = "SGVsbG8sIE11bHRpcGxhdGZvcm0h"
        val expectedOutput = "Hello, Multiplatform!"

        val result = Base64.decodeFromBase64(base64Input)
        assertEquals(expectedOutput, result, "Base64 decoding failed.")
    }

}
