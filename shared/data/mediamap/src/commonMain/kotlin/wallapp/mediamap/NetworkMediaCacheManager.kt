package wallapp.mediamap

import wallapp.media.network.model.NetworkMediaData

interface NetworkMediaCacheManager {
    suspend fun saveData(data: NetworkMediaData)
    suspend fun loadData(): NetworkMediaData?
    suspend fun clearCache()
}