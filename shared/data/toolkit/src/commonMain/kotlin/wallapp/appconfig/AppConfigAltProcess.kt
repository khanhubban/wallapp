package wallapp.appconfig

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.WallpaperScreenTheme
import wallapp.flavorconfig.FlavorConfig
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.prefs.PreferenceDefaults
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.SettingFactory.mutableSettingFlow
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher
import wallapp.settings.resolveSettingsAccessor

open class AppConfigAltProcess(
    private val defaults: PreferenceDefaults,
    internal val userSettings: Settings,
    internal val deviceSettings: Settings,
    internal val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    internal val coroutineScopeMain: CoroutineScope,
    internal val flavorConfig: FlavorConfig,
) : AppConfig {

    internal val updateDispatcher = SettingsUpdateDispatcher(
        listOf(userSettings, deviceSettings))

    override val defaultRemixId = defaults.defaultRemixId.default()

    override val defaultDesignId = defaults.defaultDesignId.default()

    override val bucketOverride: MutableObservableValue<String>
        = mutablePreference(defaults.bucketOverride)

    override val allWallpapersUnlocked: MutableObservableValue<Boolean>
        = mutablePreference(defaults.allWallpapersUnlocked)

    override val showTutorialOnFirstRun: Boolean
        get() = defaults.preferenceDefaultsProvider.showTutorialOnFirstRun

    override val simulateNetworkFailure: MutableObservableValue<Boolean>
        = mutablePreference(defaults.simulateNetworkFailure)

    override val adsEnabled: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.adsEnabled, settings = deviceSettings)

    override val allowFullWidthFeedItems: StateFlow<Boolean>
        = mutablePreferenceFlow(defaults.allowFullWidthFeedItems, settings = deviceSettings)

    override val staggeredFeed: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.staggeredFeed, settings = deviceSettings)

    override val pagedIndexScreen: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.pagedIndexScreen, settings = deviceSettings)

    override val spotlightBottomSheet: MutableObservableValue<Boolean>
        = mutablePreference(defaults.spotlightBottomSheet)

    override val spotlightShowsCollection: MutableObservableValue<Boolean>
        = mutablePreference(defaults.spotlightShowsCollection)

    override val spotlightShowsOtherCollections: MutableObservableValue<Boolean>
        = mutablePreference(defaults.spotlightShowsOtherCollections)

    override val enableIosNativeNavigation: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.enableIosNativeNavigation, settings = deviceSettings)

    override val enableLogging: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.enableLogging, settings = deviceSettings)

    override val enableNativeUiRendering: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.enableNativeUiRendering, settings = deviceSettings)

    override val spotlightTheme: MutableObservableValue<WallpaperScreenTheme> = mutableSetting(
        getPreferenceInfo(defaults.spotlightTheme),
        null,
        updateDispatcher,
        coroutineScopeMain,
        mapper = { WallpaperScreenTheme.valueOf(it) },
        inverseMapper = { it.name },
    ).observeForProcessBridge()

    override val enableFeedPaging: MutableStateFlow<Boolean> =
        mutablePreferenceFlow(defaults.enableFeedPaging, settings = deviceSettings)

    override val showPerformanceStats: MutableStateFlow<Boolean> =
        mutablePreferenceFlow(defaults.showPerformanceStats, settings = deviceSettings)

    override val enableNativeIosCollectionScreen: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.enableNativeIosCollectionScreen, settings = deviceSettings)

    private inline fun <reified T : Any> getPreferenceInfo(preferenceInfo: PreferenceInfo<T>)
        : PreferenceInfo<T> {
        val accessor = resolveSettingsAccessor(T::class)
        return when {
            userSettings.contains(preferenceInfo.key) -> {
                PreferenceInfo<T>(preferenceInfo.key,
                    accessor.get(userSettings, preferenceInfo.key, preferenceInfo.default()))
//                    .also {
//                        Log.d("UserSharedPreferences values: ${it.key}: ${it.default()}")
//                    }
            }
            deviceSettings.contains(preferenceInfo.key) -> {
                PreferenceInfo<T>(preferenceInfo.key,
                    accessor.get(deviceSettings, preferenceInfo.key, preferenceInfo.default()))
//                    .also {
//                        Log.d("DeviceSharedPreferences values: ${it.key}: ${it.default()}")
//                    }
            }
            else -> {
                preferenceInfo
//                    .also {
//                        Log.v("Using default values: ${it.key}: ${it.default()}")
//                    }
            }
        }
    }

    private inline fun <reified T : Any> mutablePreference(defaultPreferenceInfo: PreferenceInfo<T>)
        : MutableObservableValue<T> {
        return mutableSetting(
            getPreferenceInfo(defaultPreferenceInfo),
            null,
            updateDispatcher,
            coroutineScopeMain,
            ).observeForProcessBridge()
    }

    private inline fun <reified T : Any> mutablePreferenceFlow(
        preferenceInfo: PreferenceInfo<T>,
        settings: Settings,
    ): MutableStateFlow<T> {
        return mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
//            .observeForProcessBridge() // TODO: Add alt-process support for Flow
    }

    /**
     * Must be called for each [MutableObservableValue] instance so [processBridge] can be updated
     * with variable changes.
     */
    private fun <T : Any> MutableObservableValue<T>.observeForProcessBridge()
        : MutableObservableValue<T> {
        return processBridgePreferenceManager.observeForProcessBridge(this)
    }
}