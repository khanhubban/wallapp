package wallapp.search

import wallapp.search.SearchArgumentMapper.mapQuerySearchArguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SearchArgumentMapperTest {

    @Test
    fun `mapQuerySearchArguments Tracks + Singles content type`() {
        val result = mapQuerySearchArguments(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Tracks, SearchContentType.Singles),
            searchColors = null,
            searchCategories = null,
            filterByQueryQuality = true,
        )
        assertNotNull(result)
    }

    @Test
    fun `mapQuerySearchArguments Wallpaper content type ^ no other arguments`() {
        val result = mapQuerySearchArguments(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Singles),
            searchColors = null,
            searchCategories = null,
            filterByQueryQuality = true,
        )
        assertEquals(false, result?.requireValidQuery)
    }
}