package wallapp.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.flavorconfig.FlavorConfig
import wallapp.preference.MutableObservableValue
import wallapp.prefs.DevicePreferenceStorage
import wallapp.time.TimeRepository


class AppStateMainProcess(
    val devicePreferenceStorage: DevicePreferenceStorage,
    timeRepository: TimeRepository,
    val flavorConfig: FlavorConfig,
): AppState {

    override val appInstallTime: Long
        get() = devicePreferenceStorage.appInstallTime
    override val appInstallVersionName: String
        get() = devicePreferenceStorage.appInstallVersionName
    override val lastAppRunVersionName: MutableObservableValue<String>
        get() = devicePreferenceStorage.lastAppRunVersionName
    override val processSessionStartTime: Long = timeRepository.currentTime
    override val appShowing: Boolean
        get() = devicePreferenceStorage.appShowing.value
    override val deepLinkRemixes: MutableObservableValue<String>
        get() = devicePreferenceStorage.deepLinkRemixes
    override val lastNetworkRefreshTime: MutableObservableValue<Long>
        get() = devicePreferenceStorage.lastNetworkRefreshTime
    override val lastNetworkApiPath: MutableObservableValue<String>
        get() = devicePreferenceStorage.lastNetworkApiPath
    override val dataRefreshFromNetworkEvent: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.dataRefreshFromNetworkEvent
    override val isCurrentSystemWallpaperApp: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.isCurrentSystemWallpaperApp
    override val isCurrentSystemWallpaperAppRefreshTime: MutableObservableValue<Long>
        get() = devicePreferenceStorage.isCurrentSystemWallpaperAppRefreshTime
    override val lastHomeScreenWallpaperData: MutableStateFlow<String>
        get() = devicePreferenceStorage.lastHomeScreenWallpaperData
    override val lastLockScreenWallpaperData: MutableStateFlow<String>
        get() = devicePreferenceStorage.lastLockScreenWallpaperData
    override val lastKnownDarkModeState: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.lastKnownDarkModeState
    override val wallpaperHistory: MutableObservableValue<String>
        get() = devicePreferenceStorage.wallpaperHistory
    override val exposeUpgradeScrollScreen: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.exposeUpgradeScrollScreen
    override val debugLicenseState: MutableObservableValue<Int>
        get() = devicePreferenceStorage.debugLicenseState
    override val reviewFeedItemDismissedTime: MutableObservableValue<Long>
        get() = devicePreferenceStorage.reviewFeedItemDismissedTime
    override val reviewFeedItemDismissedCount: MutableObservableValue<Int>
        get() = devicePreferenceStorage.reviewFeedItemDismissedCount
    override val inAppReviewShownOnce: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.inAppReviewShownOnce
    override val inAppReviewShownTime: MutableObservableValue<Long>
        get() = devicePreferenceStorage.inAppReviewShownTime
    override val firstRunOnboardingDismissed: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.firstRunOnboardingDismissed
    override val homeOnboardingDismissed: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.homeOnboardingDismissed
    override val doubleLicenseCheckFinished: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.doubleLicenseCheckFinished
    override val lastAppCloseTime: MutableObservableValue<Long>
        get() = devicePreferenceStorage.lastAppCloseTime
    override val lastWallpaperUsageTimeFuzzy: MutableObservableValue<Long>
        get() = devicePreferenceStorage.lastWallpaperUsageTimeFuzzy
    override val lastNotifyUserPurchasePendingOrderId: MutableStateFlow<String>
        get() = devicePreferenceStorage.lastNotifyUserPurchasePendingOrderId
    override val pendingPurchaseOrderId: MutableStateFlow<String>
        get() = devicePreferenceStorage.pendingPurchaseOrderId
    override val systemPhotoStatusCache: MutableStateFlow<String>
        get() = devicePreferenceStorage.systemPhotoStatusCache
    override val imageFormatCodeInAppCompose: MutableStateFlow<String>
        get() = devicePreferenceStorage.imageFormatCodeInAppCompose
    override val randomSeedIndex: MutableStateFlow<Long>
        get() = devicePreferenceStorage.randomSeedIndex
}
