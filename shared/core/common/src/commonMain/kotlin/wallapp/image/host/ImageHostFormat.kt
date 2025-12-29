package wallapp.image.host

sealed class ImageHostFormat {

    abstract val formatCode: String

    data object Auto : ImageHostFormat() {
        override val formatCode: String = "auto"
    }

    data object Avif : ImageHostFormat() {
        override val formatCode: String = "avif"
    }

    data object Jpg : ImageHostFormat() {
        override val formatCode: String = "jpg"
    }

    data object Png : ImageHostFormat() {
        override val formatCode: String = "png"
    }

    data object WebP : ImageHostFormat() {
        override val formatCode: String = "webp"
    }

    companion object {

        fun fromFormatCode(formatCode: String): ImageHostFormat {
            return when (formatCode) {
                "auto" -> Auto
                "avif" -> Avif
                "jpg" -> Jpg
                "png" -> Png
                "webp" -> WebP
                else -> throw IllegalArgumentException("Unsupported format code: $formatCode")
            }
        }

        val Values: List<ImageHostFormat> by lazy {
            listOf(Avif, Jpg, Png, WebP)
        }
    }
}