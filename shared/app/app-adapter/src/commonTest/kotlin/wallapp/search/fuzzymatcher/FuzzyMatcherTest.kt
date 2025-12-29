package wallapp.search.fuzzymatcher

import kotlin.test.Test
import kotlin.test.assertEquals

class FuzzyMatcherTest {

    @Test
    fun `test levenshteinDistance`() {
        assertEquals(3, FuzzyMatcher.levenshteinDistance("kitten", "sitting"))
        assertEquals(3, FuzzyMatcher.levenshteinDistance("Saturday", "Sunday"))
        assertEquals(5, FuzzyMatcher.levenshteinDistance("hello", ""))
        assertEquals(0, FuzzyMatcher.levenshteinDistance("", ""))
    }

    @Test
    fun `test fuzzyMatch`() {
        val options = listOf("apple", "banana", "orange", "grape", "peach")
        assertEquals(listOf("apple"), FuzzyMatcher.fuzzyMatch("aple", options, 1))
        assertEquals(listOf("apple"), FuzzyMatcher.fuzzyMatch("pple", options, 1))
        assertEquals(listOf("banana"), FuzzyMatcher.fuzzyMatch("bannana", options, 2))
        assertEquals(listOf("orange"), FuzzyMatcher.fuzzyMatch("oragne", options, 2))
        assertEquals(listOf("peach"), FuzzyMatcher.fuzzyMatch("peech", options, 1))
        assertEquals(listOf("apple", "banana", "orange", "grape", "peach"), FuzzyMatcher.fuzzyMatch("peach", options, 5))
    }
}