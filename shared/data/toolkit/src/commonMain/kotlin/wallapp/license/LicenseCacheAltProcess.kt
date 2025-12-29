package wallapp.license

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.license.cache.LicenseCache
import wallapp.license.cache.resetAll
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.prefs.PreferenceDefaults
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher
import wallapp.settings.resolveSettingsAccessor

class LicenseCacheAltProcess(
    private val defaults: PreferenceDefaults,
    internal val settings: Settings,
    private val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    private val coroutineScopeMain: CoroutineScope,
) : LicenseCache {

    internal val updateDispatcher = SettingsUpdateDispatcher(settings)

    override val cachedLicenseState by lazy { mutablePreferenceFlow(defaults.cachedLicenseState) }

    override val licenseUserId = mutablePreference(defaults.licenseUserId)

    override val rewardUnlockedWallpapers: MutableStateFlow<String> = mutablePreferenceFlow(defaults.rewardUnlockedWallpapers)

    private inline fun <reified T : Any> getPreferenceInfo(preferenceInfo: PreferenceInfo<T>)
        : PreferenceInfo<T> {
        val accessor = resolveSettingsAccessor(T::class)
        return when {
            settings.contains(preferenceInfo.key) -> {
                PreferenceInfo<T>(
                    preferenceInfo.key,
                    accessor.get(settings, preferenceInfo.key, preferenceInfo.default())
                )
                    .also {
//                        Log.d("DeviceSharedPreferences values: ${it.key}: ${it.default()}")
                    }
            }
            else -> {
                preferenceInfo.also {
//                    Log.v("Using default values: ${it.key}: ${it.default()}")
                }
            }
        }
    }

    private inline fun <reified T : Any> mutablePreferenceFlow(preferenceInfo: PreferenceInfo<T>): MutableStateFlow<T> {
        return SettingFactory.mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
//            .observeForProcessBridge() // TODO: Add alt-process support for Flow
    }

    private inline fun <reified T : Any> mutablePreference(defaultPreferenceInfo: PreferenceInfo<T>)
        : MutableObservableValue<T> {
        return mutableSetting(
            getPreferenceInfo(defaultPreferenceInfo), null,
            updateDispatcher, coroutineScopeMain,
        ).observeForProcessBridge()
    }

    /**
     * Must be called for each [MutableObservableValue] instance so [processBridge] can be updated
     * with variable changes.
     */
    private fun <T : Any> MutableObservableValue<T>.observeForProcessBridge()
        : MutableObservableValue<T> {
        return processBridgePreferenceManager.observeForProcessBridge(this)
    }

    override fun resetAllToDefault() {
        resetAll(defaults)
    }
}