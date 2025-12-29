package wallapp.util


fun Map<String, Any?>.joinToString(separator: String = "\n"): String {
    val builder = StringBuilder("")
    keys.forEach { key ->
        builder
            .append(key)
            .append(": ")
            .append(get(key))
            .append(separator)
    }
    return builder.toString()
}

fun <T> Set<T>?.nonEmptyList(): List<T>? {
    return this?.toList().nonEmptyList()
}

fun <T> List<T>?.nonEmptyList(): List<T>? {
    return if (isNullOrEmpty()) null else this
}

val <T> List<T>.isDistinct: Boolean
    get() = size == distinct().size

fun <T, U> Map<T, U>?.nonEmptyMap(): Map<T, U>? {
    return if (isNullOrEmpty()) null else this
}

fun <T> List<T>.getPreviousItems(fromIndex: Int, count: Int): List<T> {
    if (fromIndex <= 0) return emptyList()
    if (fromIndex !in indices || count < 0) return emptyList()

    val startIndex = (fromIndex - count).coerceAtLeast(0)
    return subList(startIndex, fromIndex)
}

fun <T> List<T>.getNextItems(fromIndex: Int, count: Int): List<T> {
    if (fromIndex !in indices || count < 0) return emptyList()

    val endIndex = (fromIndex + 1 + count).coerceAtMost(size)
    return subList(fromIndex + 1, endIndex)
}

fun <T> List<T>.findItemsAppearingMoreThanOnce(): List<Pair<T, Int>> {
    val frequencyMap = groupingBy { it }.eachCount()
    return frequencyMap.filter { it.value > 1 }
        .toList()
        .sortedByDescending { it.second }
}

fun <T> T.oneOf(list: Collection<T>): Boolean = list.contains(this)

inline fun loopUntil(condition: () -> Boolean, maxLoops: Int = -1, body: () -> Unit) {
    var loopCount = 0
    while (!condition() && (maxLoops == -1 || loopCount < maxLoops)) {
        body()
        loopCount++
    }
}

fun Int.zeroPrefixed(
    maxLength: Int,
): String {
    if (this < 0 || maxLength < 1) return ""

    val string = this.toString()
    val currentStringLength = string.length
    return if (maxLength <= currentStringLength) {
        string
    } else {
        val diff = maxLength - currentStringLength
        var prefixedZeros = ""
        repeat(diff) {
            prefixedZeros += "0"
        }
        "$prefixedZeros$string"
    }
}

fun Long.isEven(): Boolean = this % 2 == 0L

val Any.simpleClassName: String
    get() = this::class.simpleName ?: throw IllegalStateException("Class name not found")