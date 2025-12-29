package wallapp.mediamap

import wallapp.media.network.model.NetworkMediaData
import wallapp.settings.Settings

class NetworkMediaCacheManagerDefault(private val settings: Settings) : NetworkMediaCacheManager {

    companion object {
        private const val CacheKey = "cachedMediaData"
        val Log = MediaMapLogger
    }

    override suspend fun saveData(data: NetworkMediaData) {
        data.exportString.also {
            settings.putString(CacheKey, it)
            Log.i("Saved network media data (size: ${it.length} bytes)")
        }
    }

    override suspend fun loadData(): NetworkMediaData? {
        return settings.getString(CacheKey, "").ifEmpty { null }
            ?.let { jsonString ->
                NetworkMediaData.fromJson(jsonString).also {
                    Log.i("Loaded cached network media data (size: ${jsonString.length} bytes)")
                }
            }
    }

    override suspend fun clearCache() {
        settings.putString(CacheKey, "")
    }
}