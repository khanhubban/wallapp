package wallapp.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.preference.MutableObservableValue


/**
 * Houses app state related variables that persist between app sessions.
 */
interface AppState {

    val appInstallTime: Long
    val appInstallVersionName: String

    val lastAppRunVersionName: MutableObservableValue<String>

    val processSessionStartTime: Long

    /**
     * Do you mean to use [wallapp.appvisibility.AppVisibility.isVisible] instead?
     */
    val appShowing: Boolean

    val isCurrentSystemWallpaperApp: MutableObservableValue<Boolean>
    val isCurrentSystemWallpaperAppRefreshTime: MutableObservableValue<Long>
    val lastHomeScreenWallpaperData: MutableStateFlow<String>
    val lastLockScreenWallpaperData: MutableStateFlow<String>

    val deepLinkRemixes: MutableObservableValue<String>

    val lastNetworkRefreshTime: MutableObservableValue<Long>
    val lastNetworkApiPath: MutableObservableValue<String>
    /**
     * This is not a persisted information, it is used as a cross-platform way of
     * sending an event when data is refreshed from network. Ignore the included
     * boolean value.
     */
    val dataRefreshFromNetworkEvent: MutableObservableValue<Boolean>

    val lastKnownDarkModeState: MutableObservableValue<Boolean>
    val wallpaperHistory: MutableObservableValue<String>
    val exposeUpgradeScrollScreen: MutableObservableValue<Boolean>
    val debugLicenseState: MutableObservableValue<Int>
    val reviewFeedItemDismissedTime: MutableObservableValue<Long>
    val reviewFeedItemDismissedCount: MutableObservableValue<Int>
    val inAppReviewShownOnce: MutableObservableValue<Boolean>
    val inAppReviewShownTime: MutableObservableValue<Long>
    val firstRunOnboardingDismissed: MutableStateFlow<Boolean>
    val homeOnboardingDismissed: MutableStateFlow<Boolean>

    val doubleLicenseCheckFinished: MutableObservableValue<Boolean>

    val lastAppCloseTime: MutableObservableValue<Long>
    val lastWallpaperUsageTimeFuzzy: MutableObservableValue<Long>

    val lastNotifyUserPurchasePendingOrderId: MutableStateFlow<String>
    val pendingPurchaseOrderId: MutableStateFlow<String>

    val systemPhotoStatusCache: MutableStateFlow<String>

    val imageFormatCodeInAppCompose: MutableStateFlow<String>

    val randomSeedIndex: MutableStateFlow<Long>
}

fun AppState.resetPendingPurchasesData() {
    pendingPurchaseOrderId.value = ""
    lastNotifyUserPurchasePendingOrderId.value = ""
}