package wallapp.collection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MutableStackTest {

    @Test
    fun testPushAndPop() {
        val stack = MutableStack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)

        assertEquals(3, stack.pop())
        assertEquals(2, stack.pop())
        assertEquals(1, stack.pop())
    }

    @Test
    fun testPeek() {
        val stack = MutableStack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)

        assertEquals(3, stack.peek())
        assertEquals(3, stack.size)
    }

    @Test
    fun testIsEmpty() {
        val stack = MutableStack<Int>()

        assertTrue(stack.isEmpty)

        stack.push(1)
        assertFalse(stack.isEmpty)

        stack.pop()
        assertTrue(stack.isEmpty)
    }

    @Test
    fun testSize() {
        val stack = MutableStack<Int>()

        assertEquals(0, stack.size)

        stack.push(1)
        assertEquals(1, stack.size)

        stack.push(2)
        assertEquals(2, stack.size)

        stack.pop()
        assertEquals(1, stack.size)
    }

    @Test
    fun testGet() {
        val stack = MutableStack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)

        assertEquals(1, stack.get(0))
        assertEquals(2, stack.get(1))
        assertEquals(3, stack.get(2))
    }

    @Test
    fun testLast() {
        val stack = MutableStack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)

        assertEquals(3, stack.lastOrNull())
    }

    @Test
    fun testPopThrowsExceptionWhenEmpty() {
        val stack = MutableStack<Int>()

        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
    }

    @Test
    fun testPeekThrowsExceptionWhenEmpty() {
        val stack = MutableStack<Int>()

        assertFailsWith<NoSuchElementException> {
            stack.peek()
        }
    }

    @Test
    fun testLastThrowsExceptionWhenEmpty() {
        val stack = MutableStack<Int>()
        assertNull(stack.lastOrNull())
    }
}