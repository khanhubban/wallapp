package wallapp.utils



inline fun <T> Array<out T>.indexOfFirst(startIndex: Int, predicate: (T) -> Boolean): Int {
    for (i in startIndex until this.size) {
        if (predicate(this[i])) {
            return i
        }
    }
    return -1
}

fun <E> List<E>.plusIfDistinct(elements: List<E>): List<E> {
    return toMutableList().apply {
        elements.forEach {
            if (!contains(it)) add(it)
        }
    }
}

fun <E> List<E>.plusIfDistinct(element: E): List<E> {
    return toMutableList().apply {
        if (!contains(element)) add(element)
    }
}


inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Sequence<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}