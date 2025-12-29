package wallapp.search.content

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import wallapp.content.model.WallpaperId
import wallapp.search.SearchContentRepositoryDefault
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class SearchContentRepositoryDefaultTest : WaeTest {

    suspend fun <T> Flow<List<T>>.waitForFirstNonEmptyResult(): List<T> {
        return this.filter { it.isNotEmpty() }.first()
    }

    @Test
    @Ignore
    fun `create and monitor remix results`() = waeTest {
        val searchContentRepository = SearchContentRepositoryDefault(waitUntilReady = false)
        val results = searchContentRepository.allSearchRemixMetadata.waitForFirstNonEmptyResult()
        println(results)
        assertTrue(results.isNotEmpty())
    }

    @Test
    @Ignore
    fun getSearchRemixMetadata() = waeTest {
        val searchContentRepository = SearchContentRepositoryDefault(waitUntilReady = false)
        searchContentRepository.allSearchRemixMetadata.waitForFirstNonEmptyResult()
        val wallpaperId = WallpaperId("a~artistname_0e732a6f")
        val results = searchContentRepository.getSearchRemixMetadata(wallpaperId).first()
        println(results)
        assertTrue(results != null)
    }
}