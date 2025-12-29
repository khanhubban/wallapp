package wallapp.collection

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MutableMapConcurrentTest {

    @Test
    fun isEmptyOnNewMap() {
        val map = MutableMapConcurrent<Int, String>()
        assertTrue(map.isEmpty())
    }

    @Test
    fun putAndGet() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        assertEquals("one", map[1])
    }

    @Test
    fun putOverridesExistingValue() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        map.put(1, "uno")
        assertEquals("uno", map[1])
        assertEquals(1, map.size)
    }

    @Test
    fun removeExistingKey() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        assertEquals("one", map.remove(1))
        assertNull(map[1])
        assertTrue(map.isEmpty())
    }

    @Test
    fun removeNonExistingKey() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        assertNull(map.remove(2))
        assertFalse(map.isEmpty())
    }

    @Test
    fun size() {
        val map = MutableMapConcurrent<Int, String>()
        assertEquals(0, map.size)
        map.put(1, "one")
        map.put(2, "two")
        assertEquals(2, map.size)
        map.remove(1)
        assertEquals(1, map.size)
    }

    @Test
    fun clear() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        map.put(2, "two")
        map.clear()
        assertEquals(0, map.size)
        assertTrue(map.isEmpty())
    }

    @Test
    fun keys() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        map.put(2, "two")
        val keys = map.keys
        assertTrue(keys.containsAll(setOf(1, 2)))
    }

    @Test
    fun checkValues() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        map.put(2, "two")
        val values = map.values
        assertTrue(values.containsAll(listOf("one", "two")))
    }

    @Test
    fun containsKey() {
        val map = MutableMapConcurrent<Int, String>()
        map[1] = "one"
        assertTrue(map.containsKey(1))
        assertFalse(map.containsKey(2))
    }

    @Test
    fun containsValue() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        assertTrue(map.containsValue("one"))
        assertFalse(map.containsValue("two"))
    }

    @Test
    fun entrySet() {
        val map = MutableMapConcurrent<Int, String>()
        map.put(1, "one")
        map.put(2, "two")
        val entries = map.entries
        assertNotNull(entries.find { it.key == 1 && it.value == "one" })
        assertNotNull(entries.find { it.key == 2 && it.value == "two" })
        assertEquals(2, entries.size)
    }

    @Test
    fun putAll() {
        val map = MutableMapConcurrent<Int, String>()
        val anotherMap = mapOf(3 to "three", 4 to "four")
        map.putAll(anotherMap)
        assertTrue(map.keys.containsAll(listOf(3, 4)))
        assertEquals("three", map[3])
        assertEquals("four", map[4])
    }

    @Test
    fun testConcurrentPut() = runTest {
        val map = MutableMapConcurrent<Int, String>()
        val jobList = ArrayList<Job>()

        repeat(1000) { index ->
            jobList += launch {
                map[index] = "value$index"
            }
        }
        jobList.forEach { it.join() }  // Wait for all coroutines to finish

        assertEquals(1000, map.size)
    }

    @Test
    fun testConcurrentPutAndGet() = runTest {
        val map = MutableMapConcurrent<Int, String>()
        val jobList = ArrayList<Job>()

        repeat(1000) { index ->
            jobList += launch {
                map[index] = "value$index"
            }
        }

        repeat(1000) { index ->
            jobList += launch {
                assertEquals("value$index", map.get(index))
            }
        }

        jobList.forEach { it.join() }  // Wait for all coroutines to finish
        assertEquals(1000, map.size)
    }

    @Test
    fun testConcurrentPutAndRemove() = runTest {
        val map = MutableMapConcurrent<Int, String>()
        val jobList = ArrayList<Job>()

        repeat(1000) { index ->
            jobList += launch {
                map[index] = "value$index"
            }
        }

        repeat(1000) { index ->
            jobList += launch {
                map.remove(index)
            }
        }

        jobList.forEach { it.join() }  // Wait for all coroutines to finish

        assertTrue(map.isEmpty())
    }
}
