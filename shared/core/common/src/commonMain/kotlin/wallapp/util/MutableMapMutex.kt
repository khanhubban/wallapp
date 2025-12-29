package wallapp.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutableMapMutex<K, V>(initialMap: Map<K, V> = mapOf()) {
    private val mutex = Mutex()
    private val map: MutableMap<K, V> = initialMap.toMutableMap()

    suspend fun put(key: K, value: V) = mutex.withLock {
        map[key] = value
    }

    suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    suspend fun getOrPut(key: K, defaultValue: () -> V): V = mutex.withLock {
        map.getOrPut(key, defaultValue)
    }

    suspend fun remove(key: K): V? = mutex.withLock {
        map.remove(key)
    }

    suspend fun size(): Int = mutex.withLock { map.size }
}
