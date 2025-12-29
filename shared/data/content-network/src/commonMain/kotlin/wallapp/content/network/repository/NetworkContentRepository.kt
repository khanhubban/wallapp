package wallapp.content.network.repository

import kotlinx.coroutines.flow.Flow

interface NetworkContentRepository {

    fun fetchNetworkContent(forceRefresh: Boolean = false, forceCache: Boolean = false): Flow<NetworkContentResult>
}