package wallapp.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.WallpaperId
import wallapp.search.model.SearchColor
import wallapp.string.quote
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchQueryManagerDefaultTest : WaeTest {

    private suspend fun Flow<SearchQueryResult>.waitForFirstNonLoadingResult(): SearchQueryResult {
        return first { it !is SearchQueryResult.Loading }
    }

    @Test
    @Ignore
    fun `search query match returns Results`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchFlow = searchQueryManager.search(SearchArguments("snow"))
        assertEquals(SearchQueryResult.Loading, searchFlow.first())
        val result = searchFlow.waitForFirstNonLoadingResult()
        assertTrue(result is SearchQueryResult.Results)
    }

    @Test
    @Ignore
    fun `search query non-match returns NoResults`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchFlow = searchQueryManager.search(SearchArguments("snowwerewrewrwerwe"))
        assertEquals(SearchQueryResult.Loading, searchFlow.first())
        val result = searchFlow.waitForFirstNonLoadingResult()
        assertTrue(result is SearchQueryResult.NoResults)
    }

    @Test
    @Ignore
    fun `search query multiple returns Results`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val arguments = listOf("red", "blue")
            .map { SearchArguments(it, filterByQueryQuality = false) }
        val searchFlow = searchQueryManager.search(arguments)
        val result = searchFlow.waitForFirstNonLoadingResult()
        assertTrue(result is SearchQueryResult.Results)
    }

    @Test
    @Ignore
    fun `Tracks + Singles`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchContentTypes = SearchContentTypes(SearchContentType.Tracks, SearchContentType.Singles)
        val searchArguments = SearchArgumentMapper.mapQuerySearchArguments(
            query = null,
            searchContentTypes = searchContentTypes,
            searchColors = null,
            searchCategories = null,
            filterByQueryQuality = true,
        )

        val result = searchQueryManager.search(searchArguments)
            .waitForFirstNonLoadingResult()

        assertTrue(result is SearchQueryResult.Results)

        val singles = result.itemResults.singles
        assertNotNull(singles)
        val tracks = result.itemResults.tracks
        assertNotNull(tracks)
    }

    @Test
    @Ignore
    fun `Tracks + Red`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchColors = listOf(SearchColor.Red)
        val searchContentTypes = SearchContentTypes(SearchContentType.Tracks)
        val result = searchQueryManager.search(
            SearchArgumentMapper.mapSearchArguments(
                searchColors,
                searchContentTypes,
            )!!
        ).waitForFirstNonLoadingResult()

        assertTrue(result is SearchQueryResult.Results)

        assertNull(result.itemResults.singles)
        val tracks = result.itemResults.tracks
        assertNotNull(tracks)
        val wallpaperIds = tracks.map { it.wallpaper.id }
        val wallpaperId = WallpaperId("a~artistname_0a93d423")
        assertTrue(wallpaperIds.contains(wallpaperId), "Expected collectionStates to contain $wallpaperId, got $wallpaperIds")
    }

    @Test
    @Ignore
    fun `Tracks + Green`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchColors = listOf(SearchColor.Green)
        val searchContentTypes = SearchContentTypes(SearchContentType.Tracks)
        val result = searchQueryManager.search(
            SearchArgumentMapper.mapSearchArguments(
                searchColors,
                searchContentTypes,
            )!!
        ).waitForFirstNonLoadingResult()

        assertTrue(result is SearchQueryResult.Results)

        assertNull(result.itemResults.singles)
        val tracks = result.itemResults.tracks
        assertNotNull(tracks)
        val trackIds = tracks.map { it.wallpaper.id }
        val wallpaperId = WallpaperId("a~artistname_c24544fb")
        assertTrue(trackIds.contains(wallpaperId), "Expected trackIds to contain $wallpaperId, got $trackIds")
    }

    @Test
    @Ignore
    fun `query - snow - fuzzy results not included`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()
        val fuzzySnowItemIds = listOf(
            "a~artistname_58c6d01d",
            "a~artistname_8cf39db6",
            "a~artistname_5edc1f12",
            "a~artistname_93ddc446",
            "a~artistname_c93a2cdb",
        ).map { WallpaperId(it) }

        val result = searchQueryManager.search(
            SearchArguments("snow"),
        ).waitForFirstNonLoadingResult()
        assertTrue(result is SearchQueryResult.Results)

        val wallpapers = result.itemResults.wallpapers
        requireNotNull(wallpapers)

        fuzzySnowItemIds.forEach { wallpaperId ->
            assertTrue(wallpapers.none { it.wallpaper.id == wallpaperId }, "Expected wallpapers to not contain ${wallpaperId.name.quote()}")
        }
    }

    @Test
    @Ignore
    fun `query - snow - fuzzy results included`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()
        val fuzzySnowItemIds = listOf(
            "a~artistname_f982590b",
            "a~artistname_95ccf0f8",
            "a~artistname_2863d439",
            "a~artistname_1b2aa4d6",
            "a~artistname_5edc1f12",
            "a~artistname_3fa55fd8",
        ).map { WallpaperId(it) }

        val result = searchQueryManager.search(
            SearchArguments(
                query = "snow",
                filterByQueryQuality = false,
                searchMatchStrategies = listOf(SearchMatchStrategyDefinitions.Fuzzy1Step),
            ),
        ).waitForFirstNonLoadingResult()
        assertTrue(result is SearchQueryResult.Results)

        val wallpapers = result.itemResults.wallpapers
        requireNotNull(wallpapers)

        fuzzySnowItemIds.forEach { wallpaperId ->
            assertTrue(wallpapers.any { it.wallpaper.id == wallpaperId }, "Expected wallpapers to contain ${wallpaperId.name.quote()}")
        }
    }

    @Test
    @Ignore
    fun `Artists + Indigo`() = waeTest {
        val searchQueryManager = SearchQueryManagerDefault()

        val searchContentTypes = SearchContentTypes(SearchContentType.Artists)
        val result = searchQueryManager.search(
            SearchArgumentMapper.mapQuerySearchArguments(
                query = "indigo",
                searchContentTypes = searchContentTypes,
                searchColors = null,
                searchCategories = null,
                filterByQueryQuality = true,
            )
        ).waitForFirstNonLoadingResult()

        assertTrue(result is SearchQueryResult.Results, "Expected result to be SearchQueryResult.Results, got $result")

        val curators = result.itemResults.curators
        assertNotNull(curators)
        val curatorIds = curators.map { it.id }
        val artistId = ArtistId("a~artistname")
        assertTrue(curatorIds.contains(artistId), "Expected artistIds to contain $artistId, got $curatorIds")
    }
}