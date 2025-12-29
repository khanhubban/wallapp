package wallapp.data

class DataRepositoryDefault : DataRepository {

    override suspend fun getDataBlob(dataHandle: DataHandle): DataBlob? {
        return when (dataHandle) {
            is DataHandle.InMemory -> dataHandle.dataBlob
            is DataHandle.DiskBacked -> readDataBlobFromFile(dataHandle.filePath)
        }
    }
}