package wallapp.process.preference

import wallapp.preference.MutableObservableValue

class ProcessBridgePreferenceManagerStub : ProcessBridgePreferenceManager {
    override fun <T : Any> observeForProcessBridge(preference: MutableObservableValue<T>)
            : MutableObservableValue<T> {
        return preference
    }
}