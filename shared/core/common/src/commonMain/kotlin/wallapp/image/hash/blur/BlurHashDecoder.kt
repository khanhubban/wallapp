package wallapp.image.hash.blur

import wallapp.image.hash.ImageHashDecoded

interface BlurHashDecoder {

    fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float = 1f,
        useCache: Boolean = true,
    ): ImageHashDecoded?

    fun clearCache()
}