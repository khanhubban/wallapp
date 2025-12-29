package wallapp.collection

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * A concurrent mutable map backed by a list of pairs.
 * Not as optimal as the [LinkedHashMap] provided by the standard library, but iOS exhibits crashes
 * when using the Kotlin default [mutableMapOf]. Use sparingly and with caution.
 */
class MutableMapConcurrent<K, V> : MutableMap<K, V> {
    private val items = mutableListOf<Pair<K, V>>()
    private val lock = SynchronizedObject()

    override val size: Int
        get() = synchronized(lock) { items.size }
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = synchronized(lock) { items.map { Entry(it) }.toMutableSet() }
    override val keys: MutableSet<K>
        get() = synchronized(lock) { items.map { it.first }.toMutableSet() }
    override val values: MutableCollection<V>
        get() = synchronized(lock) { items.map { it.second }.toMutableList() }

    override fun containsKey(key: K): Boolean {
        return synchronized(lock) { items.any { it.first == key } }
    }
    override fun containsValue(value: V): Boolean {
        return synchronized(lock) { items.any { it.second == value } }
    }

    override fun get(key: K): V? {
        return synchronized(lock) { items.firstOrNull { it.first == key }?.second }
    }
    override fun isEmpty(): Boolean {
        return synchronized(lock) { items.isEmpty() }
    }

    override fun clear() = synchronized(lock) {
        items.clear()
    }

    override fun put(key: K, value: V): V? = synchronized(lock) {
        val index = items.indexOfFirst { it.first == key }
        if (index != -1) {
            val oldValue = items[index].second
            items[index] = Pair(key, value)
            oldValue
        } else {
            items.add(Pair(key, value))
            null
        }
    }

    override fun putAll(from: Map<out K, V>) = synchronized(lock) {
        from.forEach { put(it.key, it.value) }
    }

    override fun remove(key: K): V? = synchronized(lock) {
        val index = items.indexOfFirst { it.first == key }
        if (index != -1) {
            val oldValue = items[index].second
            items.removeAt(index)
            oldValue
        } else null
    }

    private data class Entry<K, V>(var pair: Pair<K, V>) : MutableMap.MutableEntry<K, V> {
        override val key: K get() = pair.first
        override var value: V
            get() = pair.second
            set(value) {
                pair = Pair(key, value)
            }

        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }
    }
}
