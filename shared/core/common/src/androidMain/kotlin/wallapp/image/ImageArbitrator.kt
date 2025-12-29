package wallapp.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
import androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wallapp.bitmap.calculateInSampleSize
import wallapp.crashtracking.CrashTrackingHolder.crashTracking
import wallapp.log.Log
import java.io.File


/**
 * Resizes a Bitmap based on certain conditions. An example might be to ensure a Bitmap is
 * no larger than a device's max resolution.
 */
abstract class ImageArbitrator(
    val displayWidth: Int,
    val displayHeight: Int,
    val displayDensity: Float
) {

    abstract fun arbitrate(imageWidth: Int, imageHeight: Int): Pair<Int, Int>?

    fun arbitrateBitmap(bitmap: Bitmap): Bitmap {
        return arbitrate(bitmap.width, bitmap.height)?.let {
            if (it.first != bitmap.width || it.second != bitmap.height) {
                Bitmap.createScaledBitmap(bitmap, it.first, it.second, true)
            } else bitmap
        } ?: bitmap
    }

    suspend fun arbitrateBitmapFile(file: File, identifier: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            BitmapFactory.Options().run {
                // First decode with inJustDecodeBounds=true to check dimensions
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.path, this)
                val (height: Int, width: Int) = this.run { outHeight to outWidth }

                require(height > 0 && width > 0) {
                    "Invalid width ($width) and/or height ($height). Ensure input file is a Bitmap, filePath: ${file.path}, ($identifier)"
                }

                val (reqWidth, reqHeight) = arbitrate(height, width) ?: return@withContext null

                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                val decodedBitmap = BitmapFactory.decodeFile(file.path, this)
                    ?: /* See #723 */ throw crashTracking.logFatalException(
                        IllegalStateException("Failed to decode Bitmap from file"), "Bitmap decode failure: filePath: ${file.path}, $identifier")

                Log.d(
                    "[ResizeImage] Resized using inSampleSize: %s -> %s, " +
                            "originalWidth: %s, originalWidth: %s, reqWidth: %s, reqHeight: %s, (%s)",
                    "${width}x${height}", "${decodedBitmap.width}x${decodedBitmap.height}",
                    width, height, reqWidth, reqHeight, identifier
                )

                arbitrateBitmap(decodedBitmap).also {
                    Log.d(
                        "[ResizeImage] Resized [%s saving]: %s (%d) -> %s (%d), $identifier",
                        "${((1f - it.byteCount.toFloat() / decodedBitmap.byteCount.toFloat()) * 100).toInt()}%",
                        "${decodedBitmap.width}x${decodedBitmap.height}", decodedBitmap.byteCount,
                        "${it.width}x${it.height}", it.byteCount
                    )
                }
            }
        }
    }

    suspend fun arbitrateBitmapFileDescriptor(context: Context, uriString: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val fileUri = Uri.parse(uriString)
            try {
                val orientation = context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val exifInterface = ExifInterface(inputStream)
                    exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL)
                }
                val orientationResult = getOrientatedMatrix(orientation)
                val orientatedMatrix = orientationResult.first
                val flipHeightWidth = orientationResult.second

                context.contentResolver.openFileDescriptor(fileUri, "r")?.use { fileDescriptor ->
                    val decodedBitmap = BitmapFactory.Options().run {
                        // First decode with inJustDecodeBounds=true to check dimensions
                        inJustDecodeBounds = true
                        BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, this)
                        val (height: Int, width: Int) = this.run {
                            if (flipHeightWidth) {
                                outWidth to outHeight
                            } else {
                                outHeight to outWidth
                            }
                        }

                        val (reqWidth, reqHeight) = arbitrate(width, height) ?: return@withContext null

                        // Calculate inSampleSize
                        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight, flipHeightWidth)
                        // Decode bitmap with inSampleSize set
                        inJustDecodeBounds = false

                        val finalBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, this)

                        Log.d(
                            "[ResizeImage] Resized using inSampleSize size=%s, %s -> %s, " +
                                    "originalWidth: %s, originalHeight: %s, reqWidth: %s, reqHeight: %s",
                            inSampleSize, "${width}x${height}", "${finalBitmap?.width}x${finalBitmap?.height}",
                            width, height, reqWidth, reqHeight
                        )

                        finalBitmap
                    }
                    Bitmap.createBitmap(decodedBitmap, 0, 0, decodedBitmap.width,
                        decodedBitmap.height, orientatedMatrix, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Returns a pair of 1) A matrix with correct rotation and scale applied and
     * 2) a flag that is true if the matrix has been rotated by +/- 90 degrees and the width,
     * height of the bitmap should be swapped.
     */
    private fun getOrientatedMatrix(orientation: Int?): Pair<Matrix, Boolean> {
        val matrix = Matrix()
        var flipHeightWidth = false
        if (orientation == null) return Pair(matrix, flipHeightWidth)

        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
                flipHeightWidth = true
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.setRotate(90f)
                flipHeightWidth = true
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
                flipHeightWidth = true
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.setRotate(-90f)
                flipHeightWidth = true
            }
            else -> return Pair(matrix, flipHeightWidth)
        }

        return Pair(matrix, flipHeightWidth)
    }
}
