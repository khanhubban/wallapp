@file:Suppress("MemberVisibilityCanBePrivate")

package wallapp.prefs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appicon.AppIcon
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.SettingFactoryCommon.mutableSettingFlow
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher
import wallapp.settings.observeForProcessBridge
import wallapp.theme.ThemeType

open class PreferenceStorageDefault(
    defaults: PreferenceDefaults,
    internal val settings: Settings,
    internal val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    private val coroutineScopeMain: CoroutineScope,
) : PreferenceStorage {

    internal val updateDispatcher = SettingsUpdateDispatcher(settings)

    override val themeType = mutableSettingFlow(
        defaults.theme,
        settings,
        updateDispatcher,
        coroutineScopeMain,
        mapper = { ThemeType.valueOf(it) },
        inverseMapper = { it.name },
    ).observeForProcessBridge()

    override val appIcon = mutableSettingFlow(
        defaults.appIcon,
        settings,
        updateDispatcher,
        coroutineScopeMain,
        mapper = { AppIcon.valueOf(it) },
        inverseMapper = { it.name },
    ).observeForProcessBridge()

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

    private inline fun <reified T : Any> mutablePreferenceFlow(preferenceInfo: PreferenceInfo<T>): MutableStateFlow<T> {
        return mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
//            .observeForProcessBridge() // TODO: Add alt-process support for Flow
    }

    private inline fun <reified T : Any> mutablePreference(preferenceInfo: PreferenceInfo<T>): MutableObservableValue<T> {
        return mutableSetting(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
            .observeForProcessBridge()
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
