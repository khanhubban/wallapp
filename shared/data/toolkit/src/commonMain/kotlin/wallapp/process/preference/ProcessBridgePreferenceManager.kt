package wallapp.process.preference

import wallapp.preference.MutableObservableValue
import wallapp.process.bridge.ProcessBridge

/**
 * [ProcessBridgePreferenceManager]
 *
 * Performs various management tasks for using preferences across processes. There should be an
 * instance for each class that manages [MutableObservableValue]s (so [SharedPreferenceStorage]
 * and [DeviceSharedPreferenceStorage] would each have their own instance).
 *
 * Usage:
 *
 *  1). Create / @Inject a new instance inside your preference related class.
 *  2). You *must* call [observeForProcessBridge] for each [MutableObservableValue] instance in
 *  the preference related class. This configures a listener that monitors for preference value
 *  changes, and upon such a change, passes that change along via [ProcessBridge] if necessary.
 *
 */
interface ProcessBridgePreferenceManager {

    fun <T : Any> observeForProcessBridge(preference: MutableObservableValue<T>)
            : MutableObservableValue<T>

}