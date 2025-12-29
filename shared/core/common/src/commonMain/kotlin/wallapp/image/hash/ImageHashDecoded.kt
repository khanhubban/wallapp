package wallapp.image.hash

data class ImageHashDecoded(
    val intArray: IntArray,
    val width: Int,
    val height: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ImageHashDecoded

        if (!intArray.contentEquals(other.intArray)) return false
        if (width != other.width) return false
        return height == other.height
    }

    override fun hashCode(): Int {
        var result = intArray.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}
