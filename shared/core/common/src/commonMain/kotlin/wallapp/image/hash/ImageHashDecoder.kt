package wallapp.image.hash

import androidx.compose.ui.graphics.ImageBitmap
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.resource.ImageHash

interface ImageHashDecoder {

    @SealedInterop.Enabled
    sealed class Result {

        data class SuccessCompose(
            val imageBitmap: ImageBitmap,
        ): Result()

        data class SuccessData(
            val data: ByteArray,
        ): Result()

        data object Failure: Result()
    }

    suspend fun decode(imageHash: ImageHash, width: Int, height: Int, asByteArray: Boolean = false): Result

    suspend fun flushCache()
}