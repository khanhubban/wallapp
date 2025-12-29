package wallapp.account.data

import kotlinx.coroutines.flow.Flow

interface AccountDataManager {

    val dataRefreshed: Flow<Unit>

    suspend fun syncLocalDataToRemote(): Boolean

    suspend fun syncMetadataToRemote()

    suspend fun deletePIIData()

    suspend fun deleteLocalData()
}