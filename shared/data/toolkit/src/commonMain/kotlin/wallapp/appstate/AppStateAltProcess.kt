@file:Suppress("MemberVisibilityCanBePrivate")
package wallapp.appstate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.flavorconfig.FlavorConfig
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.prefs.PreferenceDefaults
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.SettingFactory.staticSetting
import wallapp.settings.SettingFactoryCommon.mutableSettingFlow
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher
import wallapp.settings.observeForProcessBridge
import wallapp.settings.resolveSettingsAccessor
import wallapp.time.TimeRepository


open class AppStateAltProcess(
    defaults: PreferenceDefaults,
    internal val deviceSettings: Settings,
    timeRepository: TimeRepository,
    internal val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    val flavorConfig: FlavorConfig,
    internal val coroutineScopeMain: CoroutineScope,
) : AppState {

    internal val updateDispatcher = SettingsUpdateDispatcher(deviceSettings)

    override val appInstallTime = staticSetting(
        defaults.appInstallTime,
        deviceSettings,
        coroutineScopeMain,
        initialValueResolver = { defaults.appInstallTime.default() },
    )

    override val appInstallVersionName = staticSetting(
        defaults.appInstallVersionName,
        deviceSettings,
        coroutineScopeMain,
        initialValueResolver = { defaults.appInstallVersionName.default() },
    )

    override val lastAppRunVersionName: MutableObservableValue<String> =
        mutablePreference(defaults.lastAppRunVersionName)

    override val processSessionStartTime: Long = timeRepository.currentTime

    internal val _appShowing: MutableObservableValue<Boolean>
            = mutablePreference(defaults.appShowing)
    override val appShowing: Boolean
        get() = _appShowing.value

    override val isCurrentSystemWallpaperApp: MutableObservableValue<Boolean>
            = mutablePreference(defaults.isCurrentSystemWallpaperApp)
    override val isCurrentSystemWallpaperAppRefreshTime: MutableObservableValue<Long>
            = mutablePreference(defaults.isCurrentSystemWallpaperAppRefreshTime)
    override val lastHomeScreenWallpaperData: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.lastHomeScreenWallpaperData)
    override val lastLockScreenWallpaperData: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.lastLockScreenWallpaperData)

    override val deepLinkRemixes: MutableObservableValue<String>
            = mutablePreference(defaults.deepLinkRemixes)

    override val lastNetworkRefreshTime: MutableObservableValue<Long>
            = mutablePreference(defaults.lastNetworkRefreshTime)

    override val lastNetworkApiPath: MutableObservableValue<String>
            = mutablePreference(defaults.lastNetworkApiPath)

    override val dataRefreshFromNetworkEvent: MutableObservableValue<Boolean>
            = mutablePreference(defaults.dataRefreshFromNetworkEvent)

    override val lastKnownDarkModeState: MutableObservableValue<Boolean>
            = mutablePreference(defaults.lastKnownDarkModeState)

    override val wallpaperHistory: MutableObservableValue<String>
            = mutablePreference(defaults.wallpaperHistory)

    override val exposeUpgradeScrollScreen: MutableObservableValue<Boolean>
            = mutablePreference(defaults.exposeUpgradeScrollScreen)

    override val debugLicenseState: MutableObservableValue<Int>
            = mutablePreference(defaults.debugLicenseState)

    override val reviewFeedItemDismissedTime: MutableObservableValue<Long>
        = mutablePreference(defaults.reviewFeedItemDismissedTime)

    override val reviewFeedItemDismissedCount: MutableObservableValue<Int>
        = mutablePreference(defaults.reviewFeedItemDismissedCount)

    override val inAppReviewShownTime: MutableObservableValue<Long>
        = mutablePreference(defaults.inAppReviewShownTime)

    override val inAppReviewShownOnce: MutableObservableValue<Boolean>
        = mutablePreference(defaults.inAppReviewShownOnce)

    override val firstRunOnboardingDismissed: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.firstRunOnboardingDismissed)

    override val homeOnboardingDismissed: MutableStateFlow<Boolean>
        = mutablePreferenceFlow(defaults.homeOnboardingDismissed)

    override val doubleLicenseCheckFinished: MutableObservableValue<Boolean>
        = mutablePreference(defaults.doubleLicenseCheckFinished)

    override val lastAppCloseTime: MutableObservableValue<Long>
        = mutablePreference(defaults.lastAppCloseTime)

    override val lastWallpaperUsageTimeFuzzy: MutableObservableValue<Long>
        = mutablePreference(defaults.lastWallpaperUsageTimeFuzzy)

    override val lastNotifyUserPurchasePendingOrderId: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.lastNotifyUserPurchasePendingOrderId)
    override val pendingPurchaseOrderId: MutableStateFlow<String>
            = mutablePreferenceFlow(defaults.pendingPurchaseOrderId)

    override val systemPhotoStatusCache: MutableStateFlow<String> =
        mutablePreferenceFlow(defaults.systemPhotoStatusCache)

    override val imageFormatCodeInAppCompose: MutableStateFlow<String> =
        mutablePreferenceFlow(defaults.imageFormatCodeInAppCompose)

    override val randomSeedIndex: MutableStateFlow<Long>
            = mutablePreferenceFlow(defaults.randomSeedIndex)

    private inline fun <reified T : Any> getPreferenceInfo(preferenceInfo: PreferenceInfo<T>)
            : PreferenceInfo<T> {
        val accessor = resolveSettingsAccessor(T::class)
        return when {
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

    private inline fun <reified T : Any> mutablePreferenceFlow(defaultPreferenceInfo: PreferenceInfo<T>)
            : MutableStateFlow<T> {
        return mutableSettingFlow(
            getPreferenceInfo(defaultPreferenceInfo),
            deviceSettings,
            updateDispatcher,
            coroutineScopeMain,
        ).observeForProcessBridge()
    }

    private inline fun <reified T : Any> mutablePreference(defaultPreferenceInfo: PreferenceInfo<T>)
            : MutableObservableValue<T> {
        return mutableSetting(
            getPreferenceInfo(defaultPreferenceInfo),
            deviceSettings,
            updateDispatcher,
            coroutineScopeMain,
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
}
