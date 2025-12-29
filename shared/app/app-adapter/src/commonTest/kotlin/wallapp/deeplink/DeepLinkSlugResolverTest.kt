package wallapp.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinkSlugResolverTest {

    private val slugResolver = DeepLinkSlugResolver

    @Test
    fun `resolve slug with valid HTTP URL returns correct slug`() {
        val url = "http://example.com/some/path?query=param"
        val expectedSlug = "some/path"
        assertEquals(expectedSlug, DeepLinkSlugResolver.resolve(url))
    }

    @Test
    fun `resolve slug with valid HTTPS URL returns correct slug`() {
        val url = "https://example.com/another/path?query=param"
        val expectedSlug = "another/path"
        assertEquals(expectedSlug, DeepLinkSlugResolver.resolve(url))
    }

    @Test
    fun `resolve slug with invalid URL returns null`() {
        val url = "https://invalid.url/some/path"
        assertNull(DeepLinkSlugResolver.resolve(url))
    }

    @Test
    fun `resolve slug with URL not containing slug returns empty string`() {
        val url = "https://example.com/?query=param"
        val expectedSlug = ""
        assertEquals(expectedSlug, DeepLinkSlugResolver.resolve(url))
    }

    @Test
    fun `resolve slug with URL without query parameters returns full slug`() {
        val url = "https://example.com/some/path"
        val expectedSlug = "some/path"
        assertEquals(expectedSlug, DeepLinkSlugResolver.resolve(url))
    }

    @Test
    fun `resolve slug with URL with trailing slash returns slug with no slash`() {
        val url = "https://example.com/some/path/"
        val expectedSlug = "some/path"
        assertEquals(expectedSlug, DeepLinkSlugResolver.resolve(url))
    }
}