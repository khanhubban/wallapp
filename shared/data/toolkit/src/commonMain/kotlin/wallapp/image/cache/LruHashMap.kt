package wallapp.image.cache

expect class LruHashMap<K : Any, V : Any>(
    initialCapacity: Int = 16,
    loadFactor: Float = 0.75F,
) {

    constructor(original: LruHashMap<out K, V>)

    val isEmpty: Boolean
    val entries: Set<Map.Entry<K, V>>

    operator fun get(key: K): V?

    fun put(key: K, value: V): V?

    fun remove(key: K): V?
}

internal fun <K : Any, V : Any> LruHashMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    return get(key) ?: defaultValue().also {
        put(key, it)
    }
}