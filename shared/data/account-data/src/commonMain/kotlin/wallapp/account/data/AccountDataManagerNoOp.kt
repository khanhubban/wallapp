package wallapp.account.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object AccountDataManagerNoOp : AccountDataManager {

    override val dataRefreshed: Flow<Unit> = flowOf(Unit)

    override suspend fun syncLocalDataToRemote() = false

    override suspend fun syncMetadataToRemote() { }

    override suspend fun deletePIIData() { }

    override suspend fun deleteLocalData() { }
}