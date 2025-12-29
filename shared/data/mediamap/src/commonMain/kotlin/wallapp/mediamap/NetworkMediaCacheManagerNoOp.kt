package wallapp.mediamap

import wallapp.media.network.model.NetworkMediaData

object NetworkMediaCacheManagerNoOp : NetworkMediaCacheManager {
    override suspend fun saveData(data: NetworkMediaData) { }

    override suspend fun loadData(): NetworkMediaData? = null

    override suspend fun clearCache() { }
}