package wallapp.util

/**
 * A filter to normalize values being returned from the sensors so that an average of the
 * last [bufferSize] values is taken
 */
class NormalizeFilter(private val bufferSize: Int = 10) {
    var values = FloatArray(bufferSize)
    var currentIndex = 0

    fun append(value: Float): Float {
        values[currentIndex] = value
        if (++currentIndex == bufferSize) currentIndex = 0
        return avg()
    }

    fun avg(ignoreZeros: Boolean = false): Float {
        var sum = 0f
        var count = 0
        for (x in values) {
            if (ignoreZeros && x == 0f) continue
            sum += x
            count++
        }
        return sum / count
    }

    override fun toString(): String {
        val sb = StringBuilder().apply {
            append("[")
            values.forEach { append("$it, ") }
            append("]")
        }
        return sb.toString()
    }
}