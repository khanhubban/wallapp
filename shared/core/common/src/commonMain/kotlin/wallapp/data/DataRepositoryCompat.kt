package wallapp.data

/**
 * This implementation works, but only supports in-memory data blobs. Use in combination with
 * [UrlDownloaderCompat].
 */
class DataRepositoryCompat : DataRepository {

    override suspend fun getDataBlob(dataHandle: DataHandle): DataBlob {
        return when (dataHandle) {
            is DataHandle.InMemory -> {
                dataHandle.dataBlob
            }
            is DataHandle.DiskBacked -> {
                TODO("Not yet implemented")
            }
        }
    }
}