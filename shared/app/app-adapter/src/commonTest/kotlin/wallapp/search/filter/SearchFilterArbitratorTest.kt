package wallapp.search.filter

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.RemixId
import wallapp.search.SearchDataType
import wallapp.search.SearchDataType.SearchTerms
import wallapp.search.SearchMatchStrategies
import wallapp.search.SearchMatchStrategyDefinitions
import wallapp.search.SearchMatchStrategyDefinitions.ExactAndPartialStartAny
import wallapp.search.SearchMatchStrategyDefinitions.Fuzzy1Step
import wallapp.search.SearchMatchStrategyDefinitions.PartialContains
import wallapp.search.filter.SearchFilterArbitrator.filterArtistResults
import wallapp.search.filter.SearchFilterArbitrator.filterByFuzzyMatch
import wallapp.search.filter.SearchFilterArbitrator.filterRemixResults
import wallapp.search.filter.SearchFilterArbitrator.mergeSearchFilterResults
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFilterEntryPreset
import wallapp.search.model.SearchFilterMatch
import wallapp.search.model.SearchFilterResult
import wallapp.search.model.SearchRemixMetadata
import wallapp.search.model.SearchRemixMetadataPreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SearchFilterArbitratorTest {

    fun SearchArtistMetadataPreset(
        artistId: ArtistId,
        artistNames: List<String> = emptyList(),
    ) = SearchArtistMetadata(
        artistId = artistId,
        artistNames = artistNames.map { SearchFilterEntryPreset(artistId, it) },
    )

    fun SearchRemixMetadataPreset(
        remixId: RemixId,
        searchTerms: List<String> = emptyList(),
        tags: List<String> = emptyList(),
    ) = SearchRemixMetadataPreset(
        remixId = remixId,
        searchTerms = searchTerms.map { SearchFilterEntryPreset(remixId, it) },
        tags = tags.map { SearchFilterEntryPreset(remixId, it) },
    )

    private fun filterRemix(
        data: List<SearchRemixMetadata>,
        query: String,
        dataType: SearchDataType,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): Pair<SearchFilterResult, List<SearchRemixMetadata>?> =
        filterRemix(
            data,
            query,
            listOf(dataType),
            searchMatchStrategies,
            filterByQueryQuality,
        )

    private fun filterRemix(
        data: List<SearchRemixMetadata>,
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): Pair<SearchFilterResult, List<SearchRemixMetadata>?> {
        val result = filterRemixResults(
            data = data,
            query = query,
            dataTypes = dataTypes,
            searchMatchStrategies = searchMatchStrategies,
            filterByQueryQuality = filterByQueryQuality,
        )
        return if (result is SearchFilterResult.Results) {
            result to result.matches
                .mapNotNull { match -> data.find { metadata -> metadata.remixId == match.id } }
                .ifEmpty { null }
        } else {
             result to null
        }
    }

    @Test
    fun `addSearchFilterResults should return null when both exact and fuzzy results are null`() {
        val exact: List<SearchFilterMatch>? = null
        val fuzzy: List<SearchFilterMatch>? = null
        val result = mergeSearchFilterResults(exact, fuzzy)
        assertEquals(null, result)
    }

    @Test
    fun `addSearchFilterResults should return exact results when fuzzy results are null`() {
        val exact = listOf(
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id1"), "term1"), 0.5f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id2"), "term2"), 0.8f)
        )
        val fuzzy: List<SearchFilterMatch>? = null
        val result = mergeSearchFilterResults(exact, fuzzy)
        assertEquals(exact, result)
    }

    @Test
    fun `addSearchFilterResults should return fuzzy results when exact results are null`() {
        val exact: List<SearchFilterMatch>? = null
        val fuzzy = listOf(
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id1"), "term1"), 0.5f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id2"), "term2"), 0.8f)
        )
        val result = mergeSearchFilterResults(exact, fuzzy)
        assertEquals(fuzzy, result)
    }

    @Test
    fun `add should combine exact and fuzzy results`() {
        val exact = listOf(
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id1"), "term1"), 0.5f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id2"), "term2"), 0.8f)
        )
        val fuzzy = listOf(
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id1"), "term1"), 0.3f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id3"), "term3"), 0.7f)
        )
        val expected = listOf(
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id1"), "term1"), 0.8f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id2"), "term2"), 0.8f),
            SearchFilterMatch.EntryMatch(SearchFilterEntryPreset(RemixId("id3"), "term3"), 0.7f)
        )
        val result = mergeSearchFilterResults(exact, fuzzy)
        assertEquals(expected, result)
    }

    @Test
    fun `validatedQuery should trim leading and trailing whitespace`() {
        val query = "  forest  "
        val result = SearchFilterArbitrator.validatedQuery(query)
        assertEquals("forest", result)
    }

    @Test
    fun `validateQuery should return the same string when no leading or trailing whitespace`() {
        val query = "forest"
        val result = SearchFilterArbitrator.validatedQuery(query)
        assertEquals("forest", result)
    }

    @Test
    fun `filter should return data with search terms containing query - multiple uses of the term are prioritized`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("forest", "trees", "gloomy forest"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2, data3)

        val (result, dataResults) = filterRemix(data, "forest", SearchTerms, ExactAndPartialStartAny)
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data2, data1), dataResults)
    }

    @Test
    fun `filter should return empty list when no data matches query`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2)

        val (result, _) = filterRemix(data, "mountain", SearchTerms, ExactAndPartialStartAny)
        assertTrue(result is SearchFilterResult.NoResults)
    }

    @Test
    fun `filter should return NoResults when no search terms partially match query`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forrest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("forrest", "trees", "gloomy forrest"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2, data3)

        val (result, _) = filterRemix(
            data,
            query = "mou",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.NoResults)
    }


    @Test
    fun `filter should return data with search terms containing query - single occurrence`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2)

        val (result, dataResults) = filterRemix(
            data = data,
            query = "beach",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data2), dataResults)
    }

    @Test
    fun `filter should prioritize multiple occurrences of query in search terms`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest", "snowy forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("forest", "trees", "gloomy forest"),
        )
        val data = listOf(data1, data2)

        val (result, dataResults) = filterRemix(
            data = data,
            query = "forest",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data1, data2), dataResults)
    }

    @Test
    fun `filter should ignore case when matching query to search terms`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("Beach", "Sunrise"),
        )
        val data = listOf(data1, data2)

        val (result, dataResults) = filterRemix(
            data = data,
            query = "BEACH",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data2), dataResults)
    }

    @Test
    fun `filter should return data with search terms containing partial query`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snowy", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("forest", "trees", "gloomy forest"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2, data3)

        val (result, dataResults) = filterRemix(
            data = data,
            query = "sno",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data1), dataResults)
    }

    @Test
    fun `filter should return data with search terms partially matching query`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("wall", "forgotten"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("forest", "trees", "gloomy forest"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
        )
        val data = listOf(data1, data2, data3)

        val (result, dataResults) = filterRemix(
            data = data,
            query = "for",
            dataType = SearchTerms,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(listOf(data2, data1), dataResults)
    }

    @Test
    fun `test fuzzy search with valid query`() {
        val data = listOf(
            SearchRemixMetadataPreset(RemixId("id~1"), listOf("apple", "banana", "orange")),
            SearchRemixMetadataPreset(RemixId("id~2"), listOf("grape", "peach", "plum")),
            SearchRemixMetadataPreset(RemixId("id~3"), listOf("kiwi", "melon", "strawberry")),
        )

        assertEquals(listOf(data[0]), filterRemix(data, "aple", SearchTerms, SearchMatchStrategyDefinitions.Fuzzy1Step).second)
        assertEquals(listOf(data[1]), filterRemix(data, "grap", SearchTerms, SearchMatchStrategyDefinitions.Fuzzy1Step).second)
        assertEquals(listOf(data[1]), filterRemix(data, "plu", SearchTerms, SearchMatchStrategyDefinitions.Fuzzy1Step).second)
        assertEquals(SearchFilterResult.NoResults, filterRemix(data, "frut", SearchTerms, SearchMatchStrategyDefinitions.Fuzzy1Step).first)
    }

    @Test
    fun `test fuzzy search`() {
        val data = listOf(
            SearchRemixMetadataPreset(RemixId("id~1"), listOf("apple", "banana", "orange")),
            SearchRemixMetadataPreset(RemixId("id~2"), listOf("grape", "peach", "plum")),
            SearchRemixMetadataPreset(RemixId("id~3"), listOf("kiwi", "melon", "strawberry")),
        )

        val (result1, dataResults1) = filterRemix(data, "ap", SearchTerms, ExactAndPartialStartAny)
        assertTrue(result1 is SearchFilterResult.Results)
        assertEquals(data[0], dataResults1?.get(0))

        val (result2, dataResults2) = filterRemix(data, "pl", SearchTerms, PartialContains)
        assertTrue(result2 is SearchFilterResult.Results)
        assertEquals(data[0], dataResults2?.get(0))

        val (result3, dataResults3) = filterRemix(data, "ki", SearchTerms, ExactAndPartialStartAny)
        assertTrue(result3 is SearchFilterResult.Results)
        assertEquals(data[2], dataResults3?.get(0))
    }

    @Test
    fun `allowFuzzyMatches == false`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("snow", "sunrise"),
        )
        val data = listOf(data1, data2)

        val result = filterRemixResults(
            data = data,
            query = "snaw",
            dataTypes = SearchDataType.All,
            searchMatchStrategies = PartialContains,
        )
        assertTrue(result is SearchFilterResult.NoResults)
    }

    @Test
    fun `allowFuzzyMatches == true`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("snow", "sunrise"),
        )
        val data = listOf(data1, data2)

        val result = filterRemixResults(
            data = data,
            query = "snaw",
            dataTypes = SearchDataType.All,
            searchMatchStrategies = Fuzzy1Step,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(2, result.matches.size)
        assertEquals(data1.remixId, result.matches[0].id)
        assertEquals(data2.remixId, result.matches[1].id)
    }


    @Test
    fun `search terms and tags are included`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
            tags = listOf("fireworks", "ai"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("beach", "sunrise"),
            tags = listOf("snow", "mountain"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
            tags = listOf("fireworks", "ai"),
        )
        val data = listOf(data1, data2, data3)

        val result = filterRemixResults(
            data,
            "snow",
            SearchDataType.All,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(2, result.matches.size)
        val resultIds = result.matches.map { it.id }
        assertTrue(resultIds.contains(data1.remixId))
        assertTrue(resultIds.contains(data2.remixId))
    }

    @Test
    fun `search terms and tags are merged`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
            tags = listOf("fireworks", "ai"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("snow", "sunrise"),
            tags = listOf("snow", "mountain"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
            tags = listOf("fireworks", "ai"),
        )
        val data = listOf(data1, data2, data3)

        val result = filterRemixResults(
            data,
            "snow",
            SearchDataType.All,
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(2, result.matches.size)
        assertEquals(data2.remixId, result.matches[0].id)
        assertEquals(data1.remixId, result.matches[1].id)
    }

    @Test
    fun `search terms and tags ignored when using SearchDataType-Title`() {
        val data1 = SearchRemixMetadataPreset(
            remixId = RemixId("id~1"),
            searchTerms = listOf("snow", "forest"),
            tags = listOf("fireworks", "ai"),
        )
        val data2 = SearchRemixMetadataPreset(
            remixId = RemixId("id~2"),
            searchTerms = listOf("snow", "sunrise"),
            tags = listOf("snow", "mountain"),
        )
        val data3 = SearchRemixMetadataPreset(
            remixId = RemixId("id~3"),
            searchTerms = listOf("beach", "sunrise"),
            tags = listOf("fireworks", "ai"),
        )
        val data = listOf(data1, data2, data3)

        val result = filterRemixResults(
            data,
            "snow",
            listOf(SearchDataType.Title),
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.NoResults)
    }

    @Test
    fun `search artists when using SearchDataType-Title`() {
        val data1 = SearchArtistMetadataPreset(
            artistId = ArtistId("id~1"),
            artistNames = listOf("person", "camera"),
        )
        val data2 = SearchArtistMetadataPreset(
            artistId = ArtistId("id~2"),
            artistNames = listOf("foo one"),
        )
        val data3 = SearchArtistMetadataPreset(
            artistId = ArtistId("id~3"),
            artistNames = listOf("foo two"),
        )
        val data = listOf(data1, data2, data3)

        val result = filterArtistResults(
            data,
            "foo",
            listOf(SearchDataType.Artist),
            searchMatchStrategies = ExactAndPartialStartAny,
        )
        assertTrue(result is SearchFilterResult.Results)
        assertEquals(2, result.matches.size)
    }

    @Test fun filterByFuzzyMatch() {
        val data = listOf(
            Triple("baller", "barler", 1),
            Triple("baller", "baler", 1),
            Triple("baller", "barller", 1),
        )

        data.forEach { (term, query, threshold) ->
            val result = filterByFuzzyMatch(term, query, threshold)
            println("term: $term, query: $query, threshold: $threshold, result: $result")
            assertNotNull(result, "term: $term, query: $query, threshold: $threshold")
        }
    }
}