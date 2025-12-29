package wallapp.resource

object ResourceRepositoryNoOp : ResourceRepository {

    override suspend fun readBytes(fileName: String): ByteArray {
        TODO("Not yet implemented")
    }
}