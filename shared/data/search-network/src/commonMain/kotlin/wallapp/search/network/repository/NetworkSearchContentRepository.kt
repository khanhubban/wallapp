package wallapp.search.network.repository

import kotlinx.coroutines.flow.Flow
import wallapp.search.model.NetworkSearchMetadata

interface NetworkSearchContentRepository {

    fun fetchNetworkContent(forceRefresh: Boolean): Flow<NetworkSearchMetadata?>
}