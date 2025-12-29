package wallapp.collection

class MutableStack<T> {

    private val elements: MutableList<T> = mutableListOf()

    fun push(item: T) {
        elements.add(item)
    }

    fun pop(): T {
        if (isEmpty) {
            throw NoSuchElementException("Stack is empty.")
        }
        return elements.removeAt(elements.size - 1)
    }

    fun peek(): T {
        if (isEmpty) {
            throw NoSuchElementException("Stack is empty.")
        }
        return elements[elements.size - 1]
    }

    fun contains(item: T): Boolean = elements.contains(item)

    fun remove(item: T): Boolean = elements.remove(item)

    fun find(predicate: (T) -> Boolean): T? = elements.find(predicate)

    val isEmpty: Boolean
        get() = elements.isEmpty()

    val size: Int
        get() = elements.size

    fun get(index: Int): T {
        if (index < 0 || index >= elements.size) {
            throw IndexOutOfBoundsException("Index: $index, Size: ${elements.size}")
        }
        return elements[index]
    }

    fun lastOrNull(): T? {
        if (isEmpty) {
            return null
        }
        return elements.last()
    }

    override fun toString(): String {
        return elements.toString()
    }
}

