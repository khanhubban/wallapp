package wallapp.account.data

interface AccountDataRepositoryController : AccountDataRepository {

    suspend fun syncLocalConnectionsToRemote(): Boolean

    suspend fun deleteLocalConnections()
}