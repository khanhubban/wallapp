package wallapp.image.hash.blur

import android.graphics.Bitmap

interface BlurHashDecoderAndroidBitmap {

    fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float = 1f,
        useCache: Boolean = true,
    ): Bitmap?

    fun clearCache()
}