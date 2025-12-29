package wallapp.image.hash

import wallapp.image.hash.ImageHashDecoder.Result
import wallapp.resource.ImageHash

object ImageHashDecoderNoOp : ImageHashDecoder {

    override suspend fun decode(imageHash: ImageHash, width: Int, height: Int, asByteArray: Boolean): Result = Result.Failure

    override suspend fun flushCache() { }
}