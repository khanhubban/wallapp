package wallapp.media.network.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import wallapp.media.network.model.NetworkMediaData
import wallapp.media.network.model.NetworkMediaMapResult
import wallapp.mediamap.MediaMapLogger
import wallapp.util.simpleClassName

class NetworkMediaMapRepositoryCombined(
    private val networkMediaMapRepositoryNetwork: NetworkMediaMapRepository,
) : NetworkMediaMapRepository {

    companion object {
        val Log = MediaMapLogger
    }

    private var cachedNetworkMediaData: NetworkMediaData? = null

    override fun fetchNetworkMediaMap(forceRefresh: Boolean): Flow<NetworkMediaMapResult> = flow {
        if (!forceRefresh) {
            cachedNetworkMediaData?.also {
                Log.i("Emitting cached data")
                emit(NetworkMediaMapResult.Success(it, sourceId = "local cache"))
                return@flow // exit if it wasn't forceRefresh and we emitted cached data
            }
        }

        networkMediaMapRepositoryNetwork.fetchNetworkMediaMap(forceRefresh = forceRefresh)
            .collect { result ->
                Log.i("Network media map result: ${result.simpleClassName}")

                when (result) {
                    is NetworkMediaMapResult.Success -> {
                        emit(result)
                        cachedNetworkMediaData.also { cachedData ->
                            val updatedData = result.networkMediaData
                            if (cachedData != updatedData) {
                                cachedNetworkMediaData = updatedData
                            }
                        }
                    }

                    is NetworkMediaMapResult.Error -> {
                        emit(result)  // Propagate the error
                    }
                }
            }
    }
}