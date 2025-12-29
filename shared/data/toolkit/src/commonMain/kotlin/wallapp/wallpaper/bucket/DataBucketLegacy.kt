package wallapp.wallpaper.bucket

/**
 * The bucket this device is fetching images from. Not to be confused with [ImageBucket]
 */
data class DataBucketLegacy(
    val bucketId: String
) {
    val largestDimensionSize: Int
        get() {
            val index = bucketId.indexOf(SEPARATOR)
            return if (index == -1) -1
            else {
                try {
                    bucketId.substring(index + 1).toInt()
                } catch (ex: NumberFormatException) {
                    -1
                }
            }
        }

    companion object {
        private const val SEPARATOR = '_'

        val DEFAULT_IMAGE_BUCKET = DataBucketLegacy("default")
    }
}