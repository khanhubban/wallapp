package wallapp.prefs

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.WallpaperScreenTheme
import wallapp.preference.MutableObservableValue


interface DevicePreferenceStorage {
    val appInstallTime: Long

    val appInstallVersionName: String

    val lastAppRunVersionName: MutableObservableValue<String>

    val exposeUpgradeScrollScreen: MutableObservableValue<Boolean>

    val allWallpapersUnlocked: MutableObservableValue<Boolean>

    val isCurrentSystemWallpaperApp: MutableObservableValue<Boolean>
    val isCurrentSystemWallpaperAppRefreshTime: MutableObservableValue<Long>

    val lastHomeScreenWallpaperData: MutableStateFlow<String>
    val lastLockScreenWallpaperData: MutableStateFlow<String>

    val lastNetworkRefreshTime: MutableObservableValue<Long>

    val lastNetworkApiPath: MutableObservableValue<String>

    val lastKnownDarkModeState: MutableObservableValue<Boolean>

    val bucketOverride: MutableObservableValue<String>

    val appShowing: MutableObservableValue<Boolean>

    val firstRunOnboardingDismissed: MutableStateFlow<Boolean>

    val homeOnboardingDismissed: MutableStateFlow<Boolean>

    val deepLinkRemixes: MutableObservableValue<String>

    val wallpaperHistory: MutableObservableValue<String>

    /**
     * This is not a persisted information, it is used as a cross-platform way of
     * sending an event when data is refreshed from network. Ignore the included
     * boolean value.
     */
    val dataRefreshFromNetworkEvent: MutableObservableValue<Boolean>

    val simulateNetworkFailure: MutableObservableValue<Boolean>

    val reviewFeedItemDismissedTime: MutableObservableValue<Long>

    val reviewFeedItemDismissedCount: MutableObservableValue<Int>

    val inAppReviewShownOnce: MutableObservableValue<Boolean>

    val inAppReviewShownTime: MutableObservableValue<Long>

    val doubleLicenseCheckFinished: MutableObservableValue<Boolean>

    val lastAppCloseTime: MutableObservableValue<Long>
    val lastWallpaperUsageTimeFuzzy: MutableObservableValue<Long>

    val lastNotifyUserPurchasePendingOrderId: MutableStateFlow<String>
    val pendingPurchaseOrderId: MutableStateFlow<String>

    val forceAds: MutableStateFlow<Boolean>
    val staggeredFeed: MutableStateFlow<Boolean>
    val allowFullWidthFeedItems: MutableStateFlow<Boolean>
    val pagedIndexScreen: MutableStateFlow<Boolean>
    val wallpaperScreenBottomSheet: MutableObservableValue<Boolean>
    val wallpaperScreenShowsCollection: MutableObservableValue<Boolean>
    val wallpaperScreenShowsOtherCollections: MutableObservableValue<Boolean>
    val wallpaperScreenTheme: MutableObservableValue<WallpaperScreenTheme>

    val enableIosNativeNavigation: MutableStateFlow<Boolean>
    val enableLogging: MutableStateFlow<Boolean>
    val enableNativeUiRendering: MutableStateFlow<Boolean>
    val enableNativeIosCollectionScreen: MutableStateFlow<Boolean>

    val randomSeedIndex: MutableStateFlow<Long>

    val showPerformanceStats: MutableStateFlow<Boolean>

    val systemPhotoStatusCache: MutableStateFlow<String>

    val debugBillingData: MutableStateFlow<String>
    val debugLicenseState: MutableObservableValue<Int>
    val useDebugBillingManager: MutableStateFlow<Boolean>
    val useDebugRewardAdCount: MutableStateFlow<Boolean>

    val purchasesRestoredForId: MutableStateFlow<String>

    val imageFormatCodeInAppCompose: MutableStateFlow<String>
    val enableFeedPaging: MutableStateFlow<Boolean>

    val recentlyViewedWallpaperIds: MutableStateFlow<String>
    val recentlyViewedCollectionIds: MutableStateFlow<String>
    val recentlyViewedArtistIds: MutableStateFlow<String>

    val lastExpirationEpochTimeForWhichUserNotified: MutableStateFlow<Long>
    val lastExpiredSubscriptionNotifyCount: MutableStateFlow<Int>

    val lastRequestReviewEpochTime: MutableStateFlow<Long>
}
