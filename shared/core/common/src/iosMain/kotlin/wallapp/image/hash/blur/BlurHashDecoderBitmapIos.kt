package wallapp.image.hash.blur

import org.jetbrains.skia.Bitmap


interface BlurHashDecoderBitmapIos {

    suspend fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float = 1f,
        useCache: Boolean = true,
    ): Bitmap?

    suspend fun clearCache()
}