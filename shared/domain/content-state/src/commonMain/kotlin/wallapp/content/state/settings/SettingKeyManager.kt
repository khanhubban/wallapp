package wallapp.content.state.settings

import wallapp.preference.ObservableValue

class SettingKeyManager {
    fun <T> getSettingKey(observable: ObservableValue<T>): String {
        return observable.key()
    }
}