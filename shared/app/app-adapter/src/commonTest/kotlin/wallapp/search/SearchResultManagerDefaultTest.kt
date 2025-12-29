package wallapp.search

import kotlinx.coroutines.ExperimentalCoroutinesApi
import wallapp.content.model.WallpaperId
import wallapp.di.resolveDependency
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchColor
import wallapp.search.model.SearchFilterMatch
import wallapp.string.quote
import wallapp.test.WaeTest
import wallapp.test.WaeTestModule
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SearchResultManagerDefaultTest : WaeTest {

    private fun createInstance(): SearchResultManagerDefault {
        return resolveDependency()
    }

    @Test
    @Ignore
    fun `getQueryResults searchColor contains blue`() = waeTest {
        val searchResultsManager = createInstance()

        val searchColors = listOf(SearchColor.Blue)

        val resultsFlow = searchResultsManager.getSearchQueryResult(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Singles),
            searchColors = searchColors,
            searchCategories = null,
        )

        val results: SearchQueryResult = resultsFlow.waitForFirstNonLoadingResult()
        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        val matches = results.itemResults.map { it.searchFilterMatch }
        assertTrue(matches.all { it is SearchFilterMatch.EntryMatch }, "Expected all matches to be EntryMatch")
        assertTrue(matches.all { match -> (match as SearchFilterMatch.EntryMatch).entries.all { it.term.contains("blue") }}, "Expected all matches to be Blue")
    }

    @Test
    @Ignore
    fun `getQueryResults null query only Single returns only Singles`() = waeTest {
        val searchResultsManager = createInstance()

        val resultsFlow = searchResultsManager.getSearchQueryResult(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Singles),
            searchColors = null,
            searchCategories = null,
        )

        val results: SearchQueryResult = resultsFlow.waitForFirstNonLoadingResult()
        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        assertTrue(results.itemResults.all { it is SearchQueryItemResult.ResultWallpaper }, "Expected all items to be a Wallpaper")
    }

    @Test
    @Ignore
    fun `getQueryResults + Tracks + Illustration`() = waeTest {
        val searchResultsManager = createInstance()

        val searchCategory = SearchCategory.Photography

        val resultsFlow = searchResultsManager.getSearchQueryResult(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Tracks),
            searchColors = null,
            searchCategories = listOf(searchCategory),
        )

        val results: SearchQueryResult = resultsFlow.waitForFirstNonLoadingResult()
        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        assertTrue(results.itemResults.all { it is SearchQueryItemResult.ResultWallpaper }, "Expected all items to be a Wallpaper")
        val matches = results.itemResults.map { it.searchFilterMatch }
        assertTrue(matches.all { it is SearchFilterMatch.EntryMatch }, "Expected all matches to be EntryMatch")
        assertTrue(matches.all { match -> (match as SearchFilterMatch.EntryMatch).entries.all { it.term.contains(searchCategory.key) }}, "Expected all matches to match ${searchCategory.key.quote()}")
        println("matches: ${matches.map { it.id }}")
    }

    @Test
    @Ignore
    fun `getSearchQueryResult Red + Tracks`() = waeTest(WaeTestModule) {
        val searchResultsManager = createInstance()

        val searchColors = listOf(SearchColor.Red)
        val results = searchResultsManager.getSearchQueryResult(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Tracks),
            searchColors = searchColors,
            searchCategories = null,
        ).waitForFirstNonLoadingResult()

        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        assertNull(results.itemResults.singles)
        val tracks = results.itemResults.tracks
        assertTrue(tracks != null, "Expected collectionStates to be non-null")
        val wallpaperIds = tracks.map { it.id }
        val id = WallpaperId("a~artistname_0a93d423")
        assertTrue(wallpaperIds.contains(id), "Expected collectionStates to contain $id, got $wallpaperIds")
    }

    @Test
    @Ignore
    fun `getSearchQueryResult Green + Singles + Photography`() = waeTest {
        val searchResultsManager = createInstance()

        val searchColors = listOf(SearchColor.Green)
        val searchCategories = listOf(SearchCategory.Photography)
        val results = searchResultsManager.getSearchQueryResult(
            query = null,
            searchContentTypes = SearchContentTypes(SearchContentType.Singles),
            searchColors = searchColors,
            searchCategories = searchCategories,
        ).waitForFirstNonLoadingResult()

        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        val singles: List<SearchQueryItemResult.ResultWallpaper>? = results.itemResults.singles
        assertTrue(singles != null, "Expected collectionStates to be non-null")

        var lastWeight: Float? = null
        singles.forEach { single ->
            val weight = single.searchFilterMatch.weight
            lastWeight?.also { lastWeight ->
                assertTrue(single.searchFilterMatch.weight <= lastWeight, "Expected weight ($weight) to be less than or equal to last weight ($lastWeight)")
                println("${single.wallpaper.id.name.quote()}: weight: ${single.searchFilterMatch.weight}")
            }
            lastWeight = weight
        }

        val greenWallpaperIds = listOf(
            "a~artistname_7d2b92eb",
            "a~artistname_bbebd514",
        )
        greenWallpaperIds.forEach { wallpaperId ->
            assertTrue(singles.any { it.wallpaper.id == WallpaperId(wallpaperId) }, "Expected singles to contain $wallpaperId")
        }

        val notGreenWallpaperIds = listOf(
            "a~artistname_95ccf0f8",
            "a~artistname_45229100",
            "a~artistname_7639fb64",
        ).map { WallpaperId(it) }
        notGreenWallpaperIds.forEach { wallpaperId ->
            assertTrue(singles.none { it.wallpaper.id == wallpaperId }, "Expected singles to not contain $wallpaperId")
        }
    }

    @Test
    @Ignore
    fun `getSearchQueryResult query results are sorted`() = waeTest {
        val searchResultsManager = createInstance()

        val results = searchResultsManager.getSearchQueryResult(
            query = "mountain",
            searchContentTypes = SearchContentTypes(SearchContentType.Tracks, SearchContentType.Singles),
            searchColors = null,
            searchCategories = null,
        ).waitForFirstNonLoadingResult()

        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        val singles = results.itemResults.singles
        assertTrue(singles != null, "Expected collectionStates to be non-null")
        val tracks = results.itemResults.tracks
        assertTrue(tracks != null, "Expected collectionStates to be non-null")
        val wallpapers = results.itemResults.wallpapers
        assertNotNull(wallpapers) { "Expected wallpapers to be non-null" }
        assertEquals(wallpapers.size, singles.size + tracks.size, "Expected all items to be singles or tracks")

//        wallpapers.forEach {
//            println("${it.id.name.quote()}: ${it.searchFilterMatch.weight}")
//        }
        for (i in 0 until wallpapers.size - 1) {
            assertTrue(
                wallpapers[i].searchFilterMatch.weight >= wallpapers[i + 1].searchFilterMatch.weight,
                "Wallpapers are not sorted by weight at index $i: ${wallpapers[i].searchFilterMatch.weight} > ${wallpapers[i + 1].searchFilterMatch.weight}"
            )
        }
    }

    @Test
    @Ignore
    fun `getSearchQueryResult single letter query does not include fuzzy Curators`() = waeTest {
        val searchResultsManager = createInstance()
        val results = searchResultsManager.getSearchQueryResult(
            query = "h",
            searchContentTypes = SearchContentTypes(SearchContentType.Artists),
            searchColors = null,
            searchCategories = null,
        ).waitForFirstNonLoadingResult()

        assertTrue(results is SearchQueryResult.Results, "Expected Results, got $results")
        val curators = results.itemResults.curators
        assertNotNull(curators) { "Expected curators to be non-null" }

        assertEquals(2, curators.size, "Expected 2 curators, got ${curators.size}")
        val expectedCuratorIds = listOf(
            "a~artistname",
            "a~artistname",
        )
        expectedCuratorIds.forEach { curatorId ->
            assertTrue(curators.any { it.id.name == curatorId }, "Expected curators to contain $curatorId")
        }
    }

    @Test
    @Ignore
    fun `getSearchQueryResult queries that should not return folder results`() = waeTest {
        val searchResultsManager = createInstance()

        val searchContentTypes = SearchContentTypes(SearchContentType.Folders)

        listOf(
            "mall",
        ).forEach { query ->
            val result = searchResultsManager.getSearchQueryResult(
                query = query,
                searchContentTypes = searchContentTypes,
                searchColors = null,
                searchCategories = null,
            ).waitForFirstNonLoadingResult()

            assertTrue(result is SearchQueryResult.NoResults, "Searching for ${query.quote()}, expected NoResults, got $result")
        }
    }

    @Test
    @Ignore
    fun `getSearchQueryResult queries that should return results`() = waeTest {
        val searchResultsManager = createInstance()
        val searchContentTypes = SearchContentTypes(SearchContentType.Artists)

        listOf(
            "a~artistname" to "maler",
            "a~artistname" to "marler",
            "a~artistname" to "marller",
        ).forEach { (expectedArtistId, query) ->
            val results = searchResultsManager.getSearchQueryResult(
                query = query,
                searchContentTypes = searchContentTypes,
                searchColors = null,
                searchCategories = null,
            ).waitForFirstNonLoadingResult()

            assertTrue(results is SearchQueryResult.Results)
            val artists = results.itemResults.artists
            assertNotNull(artists, "Expected curators to be non-null")
            assertTrue(artists.any { it.id.name.contains(expectedArtistId) }, "Expected folders to contain Maller")
        }
    }

}