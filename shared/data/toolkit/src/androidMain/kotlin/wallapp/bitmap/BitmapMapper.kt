package wallapp.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapMapper {

    fun toSystemBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
    fun ByteArray.toBitmap(): Bitmap {
        return toSystemBitmap(this)
    }

    fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    @JvmName("toByteArrayFromBitmap")
    fun Bitmap.toByteArray(): ByteArray {
        return toByteArray(this)
    }
}