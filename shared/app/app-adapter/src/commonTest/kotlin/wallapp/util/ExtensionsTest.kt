package wallapp.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsTest {

    private val testList = listOf("a", "b", "c", "d", "e", "f")

    @Test fun `test getPreviousItems with valid inputs`() {
        val result = testList.getPreviousItems(fromIndex = 4, count = 2)
        assertEquals(listOf("c", "d"), result)
    }

    @Test fun `test getPreviousItems with count greater than available items`() {
        val result = testList.getPreviousItems(fromIndex = 2, count = 5)
        assertEquals(listOf("a", "b"), result)
    }

    @Test fun `test getPreviousItems with negative fromIndex`() {
        val result = testList.getPreviousItems(fromIndex = -1, count = 5)
        assertTrue(result.isEmpty())
    }

    @Test fun `test getPreviousItems with zero count`() {
        val result = testList.getPreviousItems(fromIndex = 3, count = 0)
        assertEquals(emptyList<String>(), result)
    }

    @Test fun `test getPreviousItems with invalid fromIndex`() {
        val result = testList.getPreviousItems(fromIndex = 10, count = 2)
        assertEquals(emptyList<String>(), result)
    }

    @Test fun `test getNextItems with valid inputs`() {
        val result = testList.getNextItems(fromIndex = 2, count = 2)
        assertEquals(listOf("d", "e"), result)
    }

    @Test fun `test getNextItems outside bounds`() {
        val result = testList.getNextItems(fromIndex = testList.size, count = 2)
        assertTrue(result.isEmpty())
    }

    @Test fun `test getNextItems with count greater than available items`() {
        val result = testList.getNextItems(fromIndex = 4, count = 3)
        assertEquals(listOf("f"), result)
    }

    @Test fun `test getNextItems with zero count`() {
        val result = testList.getNextItems(fromIndex = 3, count = 0)
        assertEquals(emptyList<String>(), result)
    }

    @Test fun `test getNextItems with invalid fromIndex`() {
        val result = testList.getNextItems(fromIndex = -1, count = 2)
        assertEquals(emptyList<String>(), result)
    }

    @Test fun `findItemsAppearingMoreThanOnce with no duplicates`() {
        val list = listOf("a", "b", "c", "b", "c", "c")
        val result = list.findItemsAppearingMoreThanOnce()
        assertTrue(result.isNotEmpty())
        assertEquals(2, result.size)
        assertEquals("c", result[0].first)
        assertEquals(3, result[0].second)
        assertEquals("b", result[1].first)
        assertEquals(2, result[1].second)
    }
}