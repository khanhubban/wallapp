package wallapp.search

import wallapp.content.model.ContentCategory
import wallapp.search.SearchResultArbitrator.arbitrateSearchContentTypes
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchResultArbitratorTest {

    @Test fun `arbitrateSearchContentTypes Singles`() {
        assertEquals(
            SearchContentTypes(SearchContentType.Singles),
            arbitrateSearchContentTypes(
                listOf(Pair(ContentCategory.Singles, true)),
            ),
        )
    }

    @Test fun `arbitrateSearchContentTypes Tracks`() {
        assertEquals(
            SearchContentTypes(SearchContentType.Tracks),
            arbitrateSearchContentTypes(
                listOf(Pair(ContentCategory.Collection, true)),
            ),
        )
    }

    @Test fun `arbitrateSearchContentTypes Tracks + Singles`() {
        assertEquals(
            SearchContentTypes(SearchContentType.Tracks, SearchContentType.Singles),
            arbitrateSearchContentTypes(
                listOf(Pair(ContentCategory.Collection, true), Pair(ContentCategory.Singles, true)),
            ),
        )
    }
}