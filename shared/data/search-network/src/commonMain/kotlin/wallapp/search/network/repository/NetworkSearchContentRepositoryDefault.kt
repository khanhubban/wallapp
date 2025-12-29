package wallapp.search.network.repository

import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import wallapp.log.Logger
import wallapp.search.model.NetworkSearchMetadata

class NetworkSearchContentRepositoryDefault(
    private val repositoryNetwork: NetworkSearchContentRepositoryNetwork,
) : NetworkSearchContentRepository {

    companion object {
        val Log = Logger("NetworkSearchContentRepositoryDefault")
    }

    private var cachedNetworkContent: NetworkSearchMetadata? = null

    enum class FetchSource {
        CachedNetwork,
        Network,
    }

    override fun fetchNetworkContent(forceRefresh: Boolean): Flow<NetworkSearchMetadata?> = channelFlow {
        fun NetworkSearchMetadata?.emit(source: FetchSource) {
            Log.v("Emitting network content from $source")
            trySendBlocking(this)
        }

        val cachedResultNetwork = cachedNetworkContent

        // Emit cached network content if available.
        if (!forceRefresh) {
            if (cachedResultNetwork != null) {
                cachedResultNetwork.emit(FetchSource.CachedNetwork)
                // If we have a cached result and we're not forcing a refresh, we're done.
                if (!forceRefresh) return@channelFlow
            }
        }

        Log.i("fetchNetworkContent(forceRefresh = true)")
        repositoryNetwork.fetchNetworkContent(forceRefresh = true).collect { networkResult ->
            cachedNetworkContent = networkResult
            networkResult.emit(FetchSource.Network)
        }
    }
}