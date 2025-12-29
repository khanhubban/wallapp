package wallapp.image.hash.blur

import org.jetbrains.skia.Bitmap


interface BlurHashDecoderBitmapDesktop {

    fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float = 1f,
        useCache: Boolean = true,
    ): Bitmap?

    fun clearCache()
}