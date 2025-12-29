package wallapp.resource

interface ResourceRepository {

    suspend fun readBytes(fileName: String): ByteArray

}