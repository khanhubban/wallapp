package wallapp.image.scaler

import wallapp.image.bucket.ImageBucketManager
import wallapp.image.host.ImageHostOptions
import kotlin.math.max
import kotlin.math.roundToInt

abstract class ImageScaler {

    companion object {
        const val Scaler1x = 1f
        const val Scaler2x = 1.25f
        const val Scaler3x = 1.5f
    }

    internal abstract val density: Float

    /**
     * See [ImageBucketManager.currentImageBucketWidthScale] comments
     */
    internal abstract val widthScale: Float
    internal abstract val heightScale: Float

    /**
     * Adjusts a size to find a middle ground between a size that fills the available space, whilst
     * also being conscious of image size / memory usage / download time.
     *
     * Typically the result will be 1.x% larger than the [dpSize].which is a middle ground that
     * visually looks acceptable, whilst also being conscious of system resources.
     *
     * [dpSize] is the size of dimension as returned by the platform. For iOS, this is points.
     * Android is DP.
     */
    fun getAdjustedScale(dpSize: Float): Float {
        val scaler = when {
            density <= 1.0 -> Scaler1x
            density <= 2.0 -> Scaler2x
            else -> Scaler3x
        }
        return (dpSize * scaler)
//            .also {
//                Log.d("ImageScaler.getAdjustedScale(): platformSize=$platformSize, density=$density, scaler=$scaler, result=$it")
//            }
    }

    fun apply(options: ImageHostOptions, widthDp: Float, heightDp: Float) {
        val scalerWidthScale = widthScale
        val scalerHeightScale = heightScale
        // Hack to ensure square images are the same aspect ratio. The fact that this code is
        // required points to a deeper seeded issue that needs to be investigated further at some
        // point. See #1890.
        val (widthScale, heightScale) = if (widthDp == heightDp) {
            val maxScale = max(scalerWidthScale, scalerHeightScale)
            maxScale to maxScale
        } else {
            scalerWidthScale to scalerHeightScale
        }

        val adjustedWidth = (getAdjustedScale(widthDp) * widthScale).roundToInt()
        val adjustedHeight = (getAdjustedScale(heightDp) * heightScale).roundToInt()
        options.width = adjustedWidth
//        Log.v("ImageScaler.setSize: widthDp=$widthDp, heightDp=$heightDp, adjustedWidth=$adjustedWidth, adjustedHeight=$adjustedHeight")
        options.height = adjustedHeight
    }
}