package wallapp.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.log.Log
import wallapp.preference.MutableObservableValue
import wallapp.preference.ObservableValue
import wallapp.preference.PreferenceInfo

expect object SettingFactory {

    inline fun <reified T : Any> setting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): ObservableValue<T>

    inline fun <reified T : Any, R : Any> setting(
        pref: PreferenceInfo<T>,
        settings: Settings,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
    ): ObservableValue<R>

    inline fun <reified T : Any> mutableSettingFlow(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): MutableStateFlow<T>

    inline fun<reified T : Any, R: Any> mutableSettingFlow(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
        noinline inverseMapper: (R) -> T,
    ): MutableStateFlow<R>

    inline fun <reified T : Any> mutableSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): MutableObservableValue<T>

    inline fun<reified T : Any, R: Any> mutableSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
        noinline inverseMapper: (R) -> T
    ): MutableObservableValue<R>

    inline fun <reified T : Any> staticSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings,
        coroutineScope: CoroutineScope,
        initialValueResolver: () -> T,
    ): T
}

fun <T : Any> MutableStateFlow<T>.observeForProcessBridge()
        : MutableStateFlow<T> {
    Log.w("TODO: Implement observeForProcessBridge() for MutableStateFlow")
    return this
}