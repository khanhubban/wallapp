package wallapp.content.network.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import wallapp.log.Logger

class NetworkContentRepositoryDefault(
    private val repositoryNetwork: NetworkContentRepository,
) : NetworkContentRepository {

    companion object {
        val Log = Logger("NetworkContentRepositoryDefault")
    }

    private var cachedContentNetworkResult: NetworkContentResult? = null

    override fun fetchNetworkContent(forceRefresh: Boolean, forceCache: Boolean): Flow<NetworkContentResult> {
        if (forceCache) {
            return cachedContentNetworkResult?.let { flowOf(it) }
                ?: flowOf(NetworkContentResult.Error("No cached content"))
        }
        if (forceRefresh) {
            cachedContentNetworkResult = null
            Log.i("cleared cachedContentNetwork")
            return fetchNetworkContentInternal()
        }
        return merge(
            flowOf(cachedContentNetworkResult).filterNotNull(),
            fetchNetworkContentInternal()
        ).onEach { Log.d("fetchNetworkContent() result: $it") }
    }

    private fun fetchNetworkContentInternal(): Flow<NetworkContentResult> =
        repositoryNetwork.fetchNetworkContent(forceRefresh = true).map { networkResult ->
            when (networkResult) {
                is NetworkContentResult.Success -> {
                    cachedContentNetworkResult = networkResult
                    Log.i("set cachedContentNetwork from network")
                    networkResult
                }

                is NetworkContentResult.Error -> {
                    Log.w("⛔️ Network fetch error: ${networkResult.errorMessage}")
                    cachedContentNetworkResult ?: networkResult
                }
            }
        }
}