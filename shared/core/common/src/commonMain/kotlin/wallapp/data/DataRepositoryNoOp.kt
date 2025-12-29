package wallapp.data

object DataRepositoryNoOp : DataRepository {

    override suspend fun getDataBlob(dataHandle: DataHandle): DataBlob? = null
}