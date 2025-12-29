package wallapp.bitmap

import android.graphics.Bitmap
import kotlin.math.min

object BitmapTransformer {

    fun clipToSquare(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }
    @JvmName("clipToSquareFromBitmap")
    fun Bitmap.clipToSquare(): Bitmap {
        return clipToSquare(this)
    }

    fun resize(bitmap: Bitmap, maxWidth: Int = 512, maxHeight: Int = 512): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val maxRatio = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (bitmapRatio > maxRatio) {
            finalHeight = (maxWidth.toFloat() / bitmapRatio).toInt()
        } else {
            finalWidth = (maxHeight.toFloat() * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
    @JvmName("resizeFromBitmap")
    fun Bitmap.resize(maxWidth: Int = 512, maxHeight: Int = 512): Bitmap {
        return resize(this, maxWidth, maxHeight)
    }
}