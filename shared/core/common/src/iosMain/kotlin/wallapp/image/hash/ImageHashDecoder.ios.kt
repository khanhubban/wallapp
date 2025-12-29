package wallapp.image.hash

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import wallapp.image.hash.blur.BlurHashDecoderBitmapIos
import wallapp.resource.ImageHash
import wallapp.resource.ImageHash.ImageHashBlur

class ImageHashDecoderIos(
    private val blurHashDecoder: BlurHashDecoderBitmapIos,
) : ImageHashDecoder {

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override suspend fun decode(
        imageHash: ImageHash,
        width: Int,
        height: Int,
        asByteArray: Boolean,
    ): ImageHashDecoder.Result {
        when (imageHash) {
            is ImageHashBlur -> {
                if (asByteArray) {
                    decodeAsData(imageHash, width, height)
                } else {
                    decode(imageHash, width, height)
                }
            }
        }.let { result ->
            return when (result) {
                is ImageBitmap -> {
                    ImageHashDecoder.Result.SuccessCompose(result)
                }
                is Data -> {
                    ImageHashDecoder.Result.SuccessData(result.bytes)
                }
                else -> {
                    ImageHashDecoder.Result.Failure
                }
            }
        }
    }

    private suspend fun decode(
        imageHash: ImageHashBlur,
        width: Int,
        height: Int,
    ): ImageBitmap? {
        val blurHash = imageHash.blurHash
        return blurHashDecoder.decode(blurHash, width, height)?.asComposeImageBitmap()
    }

    private suspend fun decodeAsData(
        imageHash: ImageHashBlur,
        width: Int,
        height: Int,
    ): Data? {
        val blurHash = imageHash.blurHash
        return blurHashDecoder.decode(blurHash, width, height)?.let {
            Image.makeFromBitmap(it).encodeToData()
        }
    }

    override suspend fun flushCache() {
        blurHashDecoder.clearCache()
    }
}