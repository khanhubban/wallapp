package wallapp.data

sealed interface DataHandle {

    data class DiskBacked(val filePath: String) : DataHandle

    data class InMemory(val dataBlob: DataBlob) : DataHandle

    companion object {
        /**
         * Compatibility function to create a [DataHandle] from a [ByteArray].
         */
        fun fromBytesCompat(byteArray: ByteArray): DataHandle = InMemory(DataBlob(byteArray))
    }
}