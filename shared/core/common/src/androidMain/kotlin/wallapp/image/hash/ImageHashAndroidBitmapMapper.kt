package wallapp.image.hash

import android.graphics.Bitmap

object ImageHashAndroidBitmapMapper {

    fun map(imageHashDecoded: ImageHashDecoded): Bitmap? {
        val intArray = imageHashDecoded.intArray
        val width = imageHashDecoded.width
        val height = imageHashDecoded.height

        /**
         * TODO: Investigate whether a [Bitmap.Config.RGB_565] impl should be added.
         */
        return Bitmap.createBitmap(intArray, width, height, Bitmap.Config.ARGB_8888)
    }

}