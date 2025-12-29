package wallapp.process.listener


class ProcessListenerManager {

    private val _listeners = mutableListOf<ProcessListener>()
    val listeners: List<ProcessListener>
        get() = _listeners

    fun addListener(listener: ProcessListener) {
        if (_listeners.contains(listener)) return
        _listeners.add(listener)
    }

    fun removeListener(listener: ProcessListener) {
        if (_listeners.contains(listener)) {
            _listeners.remove(listener)
        }
    }

    fun sendPreferenceChanges(items: List<Pair<String, Any>>) {
        items.forEach { item ->
            listeners.forEach { listener ->
                listener.onPreferenceChange(item.first, item.second)
            }
        }
    }
}