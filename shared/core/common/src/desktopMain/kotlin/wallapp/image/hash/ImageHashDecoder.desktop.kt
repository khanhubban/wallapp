package wallapp.image.hash

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import wallapp.image.hash.blur.BlurHashDecoderBitmapDesktop
import wallapp.resource.ImageHash
import wallapp.resource.ImageHash.ImageHashBlur

class ImageHashDecoderDesktop(
    private val blurHashDecoder: BlurHashDecoderBitmapDesktop,
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
        return blurHashDecoder.decode(blurHash, width, height)?.asComposeImageBitmap()
    }

    override suspend fun flushCache() {
        blurHashDecoder.clearCache()
    }
}