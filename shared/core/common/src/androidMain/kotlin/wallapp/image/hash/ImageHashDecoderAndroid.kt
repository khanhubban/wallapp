package wallapp.image.hash

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import wallapp.image.hash.blur.BlurHashDecoderAndroidBitmap
import wallapp.resource.ImageHash
import wallapp.resource.ImageHash.ImageHashBlur

class ImageHashDecoderAndroid(
    private val blurHashDecoderAndroidBitmap: BlurHashDecoderAndroidBitmap,
) : ImageHashDecoder {

    override suspend fun decode(
        imageHash: ImageHash,
        width: Int,
        height: Int,
        asByteArray: Boolean,
    ): ImageHashDecoder.Result {
        when (imageHash) {
            is ImageHashBlur -> {
                decode(imageHash, width, height)
            }
        }.let {
            return if (it != null) {
                ImageHashDecoder.Result.SuccessCompose(it)
            } else {
                ImageHashDecoder.Result.Failure
            }
        }
    }

    private fun decode(
        imageHash: ImageHashBlur,
        width: Int,
        height: Int,
    ): ImageBitmap? {
        val blurHash = imageHash.blurHash
        return blurHashDecoderAndroidBitmap.decode(blurHash, width, height)?.asImageBitmap()
    }

    override suspend fun flushCache() {
        blurHashDecoderAndroidBitmap.clearCache()
    }
}