package wallapp.search

import kotlinx.coroutines.flow.first
import wallapp.content.model.ContentCategory
import wallapp.di.resolveDependency
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * These tests are @Ignore-d pending a proper DI setup for [SearchSessionManagerDefault].
 *
 * They originally passed `SearchModule`/`ContentModule` (from `:shared:di:di-base`) to
 * [waeTest]. Those references were removed because `:shared:di:di-base` depends on
 * `:shared:app:app-adapter`, so they cannot be placed on app-adapter's test classpath without
 * introducing a project dependency cycle. When these tests are revived they will need the
 * required Koin modules wired in from a module that does not create such a cycle.
 */
class SearchSessionManagerDefaultTest : WaeTest {

    private fun createInstance(): SearchSessionManagerDefault {
        return resolveDependency<SearchSessionManagerDefault>()
    }

    @Test
    @Ignore("Test requires complex DI setup with ContentRepository dependencies - needs to be fixed")
    fun `selectedContentTypes default selections`() = waeTest {
        val manager = createInstance()
        assertEquals(emptyList(), manager.selectedContentCategories.value)
    }

    @Test
    @Ignore("Test requires complex DI setup with ContentRepository dependencies - needs to be fixed")
    fun `selectedContentTypes toggle multiple selections`() = waeTest {
        val manager = createInstance()

        assertEquals(emptyList(), manager.selectedContentCategories.value)
        assertEquals(SearchContentType.AllWallpapers, manager.selectedSearchContentTypes.first())

        manager.toggleContentCategory(ContentCategory.Singles)
        assertEquals(listOf(ContentCategory.Singles), manager.selectedContentCategories.value)
        assertEquals(SearchContentTypes(SearchContentType.Singles), manager.selectedSearchContentTypes.first())

        manager.toggleContentCategory(ContentCategory.Singles)
        assertEquals(emptyList(), manager.selectedContentCategories.value)
        assertEquals(SearchContentType.AllWallpapers, manager.selectedSearchContentTypes.first())

        manager.toggleContentCategory(ContentCategory.Singles)
        manager.toggleContentCategory(ContentCategory.Collection)
        assertEquals(listOf(ContentCategory.Collection), manager.selectedContentCategories.first())
    }

    @Test
    @Ignore
    fun `basic query`() = waeTest {
        val manager = createInstance()

        manager.resetAll()
        manager.setSearchQueryFocused(true)
        manager.updateSearchQuery("snow")
        val result = manager.searchResults.waitForFirstNonLoadingResult()

        assertTrue(result is SearchResult.Results)

        assertEquals(emptyList(), manager.selectedContentCategories.value)
    }
}