package wallapp.data


data class DataBlob(val byteArray: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DataBlob
        return byteArray.contentEquals(other.byteArray)
    }

    override fun hashCode(): Int {
        return byteArray.contentHashCode()
    }

    /**
     * Returns a string representation of the object, but clamp the byte array to 256 bytes to avoid
     * accidentally logging large amounts of data.
     */
    override fun toString(): String {
        val maxLength = 256
        val displayedBytes = if (byteArray.size > maxLength) {
            byteArray.copyOfRange(0, maxLength)
        } else {
            byteArray
        }
        val suffix = if (byteArray.size > maxLength) "..." else ""
        return "DataBlob(byteArray=${displayedBytes.contentToString()}$suffix)"
    }
}
