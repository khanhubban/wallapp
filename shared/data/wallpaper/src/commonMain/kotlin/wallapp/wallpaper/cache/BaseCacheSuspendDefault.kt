package wallapp.wallpaper.cache

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BaseCacheSuspendDefault(
    private val baseCache: BaseCache
) : BaseCacheSuspend {

    override suspend fun putData(key: String, data: ByteArray): Boolean {
        return suspendCancellableCoroutine { continuation ->
            baseCache.putData(key, data) {
                if (continuation.isActive) {
                    continuation.resume(it)
                }
            }
        }
    }

    override suspend fun getData(key: String): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            baseCache.getData(key) {
                if (continuation.isActive) {
                    continuation.resume(it)
                }
            }
        }
    }

    override suspend fun isCached(key: String): Boolean {
        return baseCache.isCached(key)
    }

    override suspend fun clearCache() {
        baseCache.clearCache()
    }
}