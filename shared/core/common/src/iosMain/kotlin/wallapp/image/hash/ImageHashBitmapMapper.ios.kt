package wallapp.image.hash

import org.jetbrains.skia.Bitmap


object ImageHashBitmapMapperIos {

    fun map(imageHashDecoded: ImageHashDecoded): Bitmap? {
        val intArray: IntArray = imageHashDecoded.intArray
        val width = imageHashDecoded.width
        val height = imageHashDecoded.height

        val pixels = convertIntArrayToByteArray(intArray)

        // Create a new Skia Bitmap
        val bitmap = Bitmap()
        // Allocate pixels with N32 color type (32-bit RGBA)
        bitmap.allocN32Pixels(width, height, opaque = false)

        bitmap.installPixels(bitmap.imageInfo, pixels, bitmap.rowBytes)

        return bitmap
    }

    fun convertIntArrayToByteArray(intArray: IntArray): ByteArray {
        val byteArray = ByteArray(intArray.size * 4)

        intArray.forEachIndexed { index, value ->
            byteArray[index * 4] = (value and 0xFF).toByte()          // Blue
            byteArray[index * 4 + 1] = ((value shr 8) and 0xFF).toByte()  // Green
            byteArray[index * 4 + 2] = ((value shr 16) and 0xFF).toByte() // Red
            byteArray[index * 4 + 3] = ((value shr 24) and 0xFF).toByte()     // Alpha
        }
        return byteArray
    }

}