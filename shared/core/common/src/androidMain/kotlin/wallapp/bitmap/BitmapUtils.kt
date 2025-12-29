package wallapp.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max


fun getClampSizes(width: Int, height: Int, maxDimension: Int): Pair<Int, Int>? {
    return if (width <= maxDimension && height <= maxDimension) {
        null
    } else {
        val scaler = maxDimension.toFloat() /
                if (width == height) { width.toFloat() } else { max(width, height).toFloat() }
        (width * scaler).toInt() to (height * scaler).toInt()
    }
}

/**
 * Helper function to clamp a Bitmap to a [maxDimension].
 *
 * So if a Bitmap is 400x200, and [maxDimension] is 100, the resulting
 * [Bitmap] will be 100x50.
 */
fun Bitmap.clampSize(maxDimension: Int): Bitmap {
    return getClampSizes(width, height, maxDimension)?.let {
        Bitmap.createScaledBitmap(this, it.first, it.second, true)
    } ?: this
}


fun ByteArray.asBitmap(): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inMutable = true
    }
    return BitmapFactory.decodeByteArray(this, 0, size, options).apply {
        Canvas(this)
    }
}

fun Bitmap?.isValid() : Boolean {
    return this != null && !isRecycled && width > 0 && height > 0
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int,
                          flipHeightWidth: Boolean = false): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run {
        if (flipHeightWidth) {
            outWidth to outHeight
        } else {
            outHeight to outWidth
        }
    }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

fun decodeSampledBitmapFromResource(
    res: Resources,
    resId: Int,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    // First decode with inJustDecodeBounds=true to check dimensions
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, this)

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        inJustDecodeBounds = false

        BitmapFactory.decodeResource(res, resId, this)
    }
}

fun saveBitmap(path: String, bitmap: Bitmap): Boolean {
    return try {
        FileOutputStream(File(path)).use { out ->
            if (bitmap.isRecycled) return false
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}


fun View.getAsBitmap(width: Int, height: Int): Bitmap? {
    // Get the dimensions of the view so we can re-layout the view at its current size
    // and create a bitmap of the same size
//    val width: Int = getWidth()
//    val height: Int = getHeight()
    val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

    //Cause the view to re-layout
    measure(measuredWidth, measuredHeight)
    layout(0, 0, getMeasuredWidth(), getMeasuredHeight())

    //Create a bitmap backed Canvas to draw the view into
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
    draw(canvas)
    return bitmap
}