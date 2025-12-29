package wallapp.process.preference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.log.Log
import wallapp.preference.MutableObservableValue
import wallapp.preference.ObservableValue
import wallapp.process.bridge.ProcessBridge
import wallapp.process.listener.ProcessListener
import wallapp.process.listener.ProcessListenerManager
import wallapp.util.isMainThread


class ProcessBridgePreferenceManagerDefault(
    val processBridge: ProcessBridge,
    processListenerManager: ProcessListenerManager,
    val coroutineScopeMain: CoroutineScope,
) : ProcessBridgePreferenceManager {

    private val processListener = object : ProcessListener {
        override fun <T : Any> onPreferenceChange(key: String, value: T) {
            this@ProcessBridgePreferenceManagerDefault.onPreferenceChange(key, value)
        }
    }

    init {
        Log.d("processBridge: ${processBridge::class.simpleName}")
        processListenerManager.addListener(processListener)
    }

    val preferences = mutableMapOf<String, MutableObservableValue<*>>()

    override fun <T : Any> observeForProcessBridge(preference: MutableObservableValue<T>)
            : MutableObservableValue<T> {
        require(preferences[preference.key()] == null) { "Preference with key \"${preference.key()}\" already exists" }
        return preference.apply {
            subscribe { value ->
                if (tempIgnoreOnChangeKeyList.contains(preference.key())) {
//                    Log.d("${processBridge.logPrefix()} [ON IGNORE LIST - IGNORING] onChange(): key: ${key()} value: $value")
                } else {
//                    Log.d("${processBridge.logPrefix()} onChange(): key: ${key()} value: $value")
                    processBridge.updatePreferenceValue(key(), value)
                }
            }
            preferences[preference.key()] = this
        }
    }

    /**
     * Used to prevent cyclic propagating of variable value changes.
     *
     * Intended usage is:
     *   1. [onPreferenceChange] receives a preference change from another process.
     *   2. [updateIfNewIgnoreOnChangeListener] is called to update the preference value.
     *   3. [updateIfNewIgnoreOnChangeListener] adds the preference key to
     *   [tempIgnoreOnChangeKeyList], then calls [updateIfNew] on the preference.
     *   4. The listener added in [observeForProcessBridge] is called with the value change. This
     *   listener checks if the key is on [tempIgnoreOnChangeKeyList], and if so, does update the
     *   [processBridge] with this change. It is here where we stop the cyclic propagation.
     *   5. Back in [updateIfNewIgnoreOnChangeListener], the preference key is removed from
     *   [tempIgnoreOnChangeKeyList].
     */
    private val tempIgnoreOnChangeKeyList = mutableListOf<String>()
    private fun <T> MutableObservableValue<T>.updateIfNewIgnoreOnChangeListener(value: T) {
        val updateValueBlock = {
            tempIgnoreOnChangeKeyList.add(key())
            updateIfNew(value)
//            require(value() == value) { "Preference key=${key()} failed to update value to \"${value}\" (current=\"${value()}\")" }
            tempIgnoreOnChangeKeyList.remove(key())
        }

        if (isMainThread()) {
//            Log.d("[Update] already on main thread, ${key()}")
            updateValueBlock.invoke()
        } else {
            coroutineScopeMain.launch {
//                Log.d("[Update] via coroutine to main thread, ${key()}")
                updateValueBlock.invoke()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> onPreferenceChange(key: String, value: T) {
        preferences[key]?.also {
            when {
                shouldLongValueBeString(it, value) -> {
                    val observable = (it as MutableObservableValue<String>)
                    val newValue = (value as Long).toString()

//                    Log.d("${processBridge.logPrefix()} onPreferenceChange(): key: $key, value: ${observable.value} -> $newValue")
                    observable.updateIfNewIgnoreOnChangeListener(newValue)
                }
                shouldLongValueBeDouble(it, value) -> {
                    val observable = (it as MutableObservableValue<Double>)
                    val newValue = (value as Long).toDouble()

//                    Log.d("${processBridge.logPrefix()} onPreferenceChange(): key: $key, value: ${observable.value} -> $newValue")
                    observable.updateIfNewIgnoreOnChangeListener(newValue)
                }
                else -> {
                    val observable = (it as MutableObservableValue<T>)

//                    Log.d("${processBridge.logPrefix()} onPreferenceChange(): key: $key, value: ${observable.value} -> $value")
                    observable.updateIfNewIgnoreOnChangeListener(value)
                }
            }
        } ?: run {
//            Log.v("onPreferenceChange(): No record of preference with key: $key, nothing to do...")
        }
    }

    /**
     * See issue #1052
     * Checks if the types of [value] and [ObservableValue] are different due to the issue
     * described in #1052
     */
    private fun <T> shouldLongValueBeDouble(observable: ObservableValue<*>, value: T) =
        value is Long && observable.value is Double

    private fun <T> shouldLongValueBeString(observable: ObservableValue<*>, value: T) =
        value is Long && observable.value is String
}