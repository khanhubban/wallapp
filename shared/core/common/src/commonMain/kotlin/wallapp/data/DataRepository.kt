package wallapp.data

interface DataRepository {

    suspend fun getDataBlob(dataHandle: DataHandle): DataBlob?
}
