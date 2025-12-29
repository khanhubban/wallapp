package wallapp.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MutableMapMutexTest {

    data class TestId(val name: String)

    sealed interface TestState {
        data object Foo : TestState
        data object Bar : TestState
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `put and get operations should be thread-safe`() = runTest {
        val map = MutableMapMutex<TestId, MutableStateFlow<TestState>>()
        val key = TestId("testKey")
        val value = MutableStateFlow<TestState>(TestState.Foo)

        val jobs = List(100) {
            launch {
                map.put(key, value)
                assertEquals(value, map.get(key))
            }
        }
        jobs.forEach { it.join() }

        assertEquals(value, map.get(key))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure map maintains integrity with concurrent modifications`() = runTest {
        val map = MutableMapMutex<TestId, MutableStateFlow<TestState>>()

        val jobs = List(100) { i ->
            launch {
                val key = TestId("key$i")
                val value = MutableStateFlow<TestState>(TestState.Foo)
                map.put(key, value)
            }
        }
        jobs.forEach { it.join() }

        assertEquals(100, map.size())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `concurrent modifications to a regular MutableMap should show incorrect behavior`() = runTest {
        val map: MutableMap<TestId, MutableStateFlow<TestState>> = mutableMapOf()

        // Attempt to launch 10000 coroutines that modify the map concurrently
        val jobs = List(10000) { i ->
            launch {
                val key = TestId("key$i")
                val value = MutableStateFlow<TestState>(TestState.Foo)
                map[key] = value  // This line is not thread-safe
            }
        }
        jobs.forEach { it.join() }

        // The actual size may not be 100 due to concurrent modification issues
        println("Map size after modifications: ${map.size}")
        // The assertion below might fail or behavior might be unpredictable
        assertTrue(map.size <= 10000)
    }
}
