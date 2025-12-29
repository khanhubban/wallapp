package wallapp.media.network.repository

import kotlinx.coroutines.flow.Flow
import wallapp.media.network.model.NetworkMediaMapResult

interface NetworkMediaMapRepository {

    fun fetchNetworkMediaMap(forceRefresh: Boolean): Flow<NetworkMediaMapResult>
}