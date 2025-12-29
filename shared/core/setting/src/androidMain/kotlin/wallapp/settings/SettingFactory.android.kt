package wallapp.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.preference.MutableObservableValue
import wallapp.preference.ObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.settings.SettingFactoryCommon as SettingFactoryImpl

actual object SettingFactory {

    actual inline fun <reified T : Any> setting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): ObservableValue<T> = SettingFactoryImpl.setting(preferenceInfo, settings, updateDispatcher, coroutineScope)

    actual inline fun <reified T : Any, R : Any> setting(
        pref: PreferenceInfo<T>,
        settings: Settings,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
    ): ObservableValue<R> = SettingFactoryImpl
        .setting(pref, settings, updateDispatcher, coroutineScope, observeAndUpdateFlow = false, mapper)

    actual inline fun <reified T : Any> mutableSettingFlow(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): MutableStateFlow<T> = SettingFactoryImpl.mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScope)

    actual inline fun <reified T : Any, R : Any> mutableSettingFlow(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
        noinline inverseMapper: (R) -> T,
    ): MutableStateFlow<R> = SettingFactoryImpl.mutableSettingFlow(
        preferenceInfo,
        settings,
        updateDispatcher,
        coroutineScope,
        mapper,
        inverseMapper,
    )

    actual inline fun <reified T : Any> mutableSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
    ): MutableObservableValue<T> = SettingFactoryImpl.mutableSetting(preferenceInfo, settings, updateDispatcher, coroutineScope)

    actual inline fun <reified T : Any, R : Any> mutableSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings?,
        updateDispatcher: SettingsUpdateDispatcher,
        coroutineScope: CoroutineScope,
        noinline mapper: (T) -> R,
        noinline inverseMapper: (R) -> T,
    ): MutableObservableValue<R> = SettingFactoryImpl.mutableSetting(
        preferenceInfo,
        settings,
        updateDispatcher,
        coroutineScope,
        observeAndUpdateFlow = false,
        mapper,
        inverseMapper,
    )

    actual inline fun <reified T : Any> staticSetting(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings,
        coroutineScope: CoroutineScope,
        initialValueResolver: () -> T,
    ): T = SettingFactoryImpl.staticSetting(preferenceInfo, settings, coroutineScope, initialValueResolver)
}