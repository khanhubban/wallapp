@file:Suppress("MemberVisibilityCanBePrivate")

package wallapp.preferences

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appicon.AppIcon
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.prefs.PreferenceDefaults
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.SettingFactory.mutableSettingFlow
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher
import wallapp.settings.observeForProcessBridge
import wallapp.settings.resolveSettingsAccessor
import wallapp.theme.ThemeType

open class UserPreferencesAltProcess(
    defaults: PreferenceDefaults,
    internal val userSettings: Settings,
    internal val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    private val coroutineScopeMain: CoroutineScope,
) : UserPreferences {

    internal val updateDispatcher = SettingsUpdateDispatcher(userSettings)

    override val themeType = mutableSettingFlow(defaults.theme, null,
        updateDispatcher, coroutineScopeMain, mapper = { ThemeType.valueOf(it) }, inverseMapper = { it.name }).observeForProcessBridge()

    override val appIcon: MutableStateFlow<AppIcon> = mutableSettingFlow(defaults.appIcon, null,
        updateDispatcher, coroutineScopeMain, mapper = { AppIcon.valueOf(it) }, inverseMapper = { it.name }).observeForProcessBridge()

    override val currentRemixId: MutableObservableValue<String>
            = mutablePreference(defaults.currentRemixId)

    override val currentDesignId: MutableObservableValue<String>
        = mutablePreference(defaults.currentDesignId)

    override val nextRemixId: MutableObservableValue<String>
            = mutablePreference(defaults.nextRemixId)

    override val currentPreviewRemixId: MutableObservableValue<String>
            = mutablePreference(defaults.currentPreviewRemixId)

    override val firebaseCloudMessagingToken: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.firebaseCloudMessagingToken)

    override val favorites: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.favorites)

    override val followings: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.followings)

    override val purchases: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.purchases)

    override val deviceInfo: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.deviceInfo)

    override val nextWallpaperUseFavorites: MutableStateFlow<Boolean>
            = mutablePreferenceFlow(defaults.nextWallpaperUseFavorites)

    override val wallpaperDownloadEvents: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.wallpaperDownloadEvents)

    override val acceptedTerms: MutableStateFlow<Boolean>
            = mutablePreferenceFlow(defaults.acceptedTerms)

    override val joinNewsletter: MutableStateFlow<Boolean>
            = mutablePreferenceFlow(defaults.joinNewsletter)

    override val reportUsageStats: MutableStateFlow<Boolean>
            = mutablePreferenceFlow(defaults.reportUsageStats)

    override val receiveNotifications: MutableStateFlow<Boolean>
            = mutablePreferenceFlow(defaults.receiveNotifications)

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
            else -> {
                preferenceInfo
//                    .also {
//                        Log.v("Using default values: ${it.key}: ${it.default()}")
//                    }
            }
        }
    }

    private inline fun <reified T : Any> mutablePreferenceFlow(preferenceInfo: PreferenceInfo<T>): MutableStateFlow<T> {
        return mutableSettingFlow(
            preferenceInfo,
            settings = null,
            updateDispatcher,
            coroutineScopeMain,
        ).observeForProcessBridge()
    }

    private inline fun <reified T : Any> mutablePreference(defaultPreferenceInfo: PreferenceInfo<T>)
            : MutableObservableValue<T> {
        return mutableSetting(getPreferenceInfo(defaultPreferenceInfo), null,
            updateDispatcher, coroutineScopeMain).observeForProcessBridge()
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
